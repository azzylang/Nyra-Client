package de.constt.nyra.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static boolean checkIfFileExist(Path pathToFile) {
        return Files.exists(pathToFile) && Files.isRegularFile(pathToFile);
    }

    public static void ensureFileExists(Path pathToFile) throws IOException {
        if (checkIfFileExist(pathToFile)) return;
        Path parentDir = pathToFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.createFile(pathToFile);
    }

    public static void createFile(Path pathToFile, String content) throws IOException {
        Path parentDir = pathToFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.writeString(pathToFile, content, StandardCharsets.UTF_8);
    }

    public static void removeFile(Path pathToFile) throws IOException {
        if (checkIfFileExist(pathToFile)) {
            Files.delete(pathToFile);
        }
    }

    public static String readFile(Path pathToFile) throws IOException {
        if (!checkIfFileExist(pathToFile)) {
            throw new IOException("File does not exist: " + pathToFile);
        }
        return Files.readString(pathToFile, StandardCharsets.UTF_8);
    }

    public static List<String> readLines(Path pathToFile) throws IOException {
        if (!checkIfFileExist(pathToFile)) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String line : Files.readAllLines(pathToFile, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) result.add(line.trim());
        }
        return result;
    }

    public static boolean fileContainsText(File file, String text) {
        return fileContainsText(file.toPath(), text);
    }

    public static boolean fileContainsText(Path pathToFile, String text) {
        if (!checkIfFileExist(pathToFile)) return false;
        try (BufferedReader reader = new BufferedReader(
                new FileReader(pathToFile.toFile(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(text)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void writeLines(Path pathToFile, List<String> lines) throws IOException {
        Path parentDir = pathToFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.write(pathToFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void appendLine(Path pathToFile, String line) throws IOException {
        Path parentDir = pathToFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.writeString(pathToFile, line + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static boolean removeLine(Path pathToFile, String text) throws IOException {
        List<String> lines   = readLines(pathToFile);
        int          before  = lines.size();
        lines.removeIf(l -> l.trim().equals(text));
        writeLines(pathToFile, lines);
        return lines.size() < before;
    }
}