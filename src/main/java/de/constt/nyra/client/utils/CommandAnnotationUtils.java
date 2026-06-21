package de.constt.nyra.client.utils;


import de.constt.nyra.client.annotations.CommandInfoAnnotation;

public class CommandAnnotationUtils {
    public static String getName(Class<?> clazz) {
        CommandInfoAnnotation info = clazz.getAnnotation(CommandInfoAnnotation.class);
        if (info == null) return null;
        return info.name();
    }

    public static String getDescription(Class<?> clazz) {
        CommandInfoAnnotation info = clazz.getAnnotation(CommandInfoAnnotation.class);
        if (info == null) return null;
        return info.description();
    }

    public static String getCommand(Class<?> clazz) {
        CommandInfoAnnotation info = clazz.getAnnotation(CommandInfoAnnotation.class);
        if (info == null) return null;
        return info.command();
    }
}
