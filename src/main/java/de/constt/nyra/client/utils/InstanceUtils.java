package de.constt.nyra.client.utils;

import java.io.File;
import java.io.IOException;

public class InstanceUtils {
    private static Integer instanceId;

    public static int getInstanceId() {
        if (instanceId != null) return instanceId;

        File dir = new File(System.getProperty("java.io.tmpdir"), "nyra_instances");
        dir.mkdirs();

        int id = 1;
        while (true) {
            File f = new File(dir, "instance_" + id + ".lock");
            try {
                if (f.createNewFile()) {
                    f.deleteOnExit();
                    instanceId = id;
                    return id;
                }
            } catch (IOException ignored) {
            }
            id++;
        }
    }
}