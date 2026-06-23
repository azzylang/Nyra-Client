plugins {
    id("dev.kikugie.loom-back-compat")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    mavenCentral()

    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")

    flatDir {
        dirs("lib")
    }
}

dependencies {
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    fapi("fabric-lifecycle-events-v1", "fabric-resource-loader-v0", "fabric-content-registries-v0", "fabric-registry-sync-v0", "fabric-message-api-v1")

    fun includeImplementation(
        notation: String,
        configure: ExternalModuleDependency.() -> Unit = {}
    ) {
        val dep = create(notation) as ExternalModuleDependency
        configure(dep)
        add("implementation", dep)
        add("include", dep)
    }

    // Discord IPC - LOCAL JAR ONLY (jagrosh is not on any Maven repo)
    val discordJar = files(rootProject.file("lib/DiscordIPC-0.10.2.jar"))
    implementation(discordJar)
    include(discordJar)

    // Junixsocket
    implementation("com.kohlschutter.junixsocket:junixsocket-common:2.6.0")
    implementation("com.kohlschutter.junixsocket:junixsocket-native-common:2.6.0")
    implementation("com.kohlschutter.junixsocket:junixsocket-core:2.6.0")

    // IMGUI
    includeImplementation("io.github.spair:imgui-java-binding:${property("deps.imgui_version")}")
    includeImplementation("io.github.spair:imgui-java-lwjgl3:${property("deps.imgui_version")}") {
        exclude(group = "org.lwjgl")
        exclude(group = "org.lwjgl.lwjgl")
    }

    includeImplementation("io.github.spair:imgui-java-natives-windows:${property("deps.imgui_version")}")
    includeImplementation("io.github.spair:imgui-java-natives-linux:${property("deps.imgui_version")}")
    includeImplementation("io.github.spair:imgui-java-natives-macos:${property("deps.imgui_version")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    accessWidenerPath = sc.process(
        rootProject.file("src/main/resources/nyra.ct"),
        "build/processed.ct"
    )

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    register<Copy>("collectBuilds") {
        group = "build"
        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}