package de.constt.nyra.client.clientcommands;

import de.constt.nyra.client.annotations.ModuleInfoAnnotation;
import de.constt.nyra.client.clientcommands.utils.ClickGuiCommand;
import de.constt.nyra.client.roots.implementations.CategoryImplementation;
import de.constt.nyra.client.roots.implementations.CommandImplementation;

import java.util.ArrayList;
import java.util.List;

public class CCommandManager {
    private static final List<CommandImplementation> COMMANDS = new ArrayList<>();
    public static String cmdPrefix;

    public static void init() {
        setCmdPrefix("$");

        COMMANDS.add(new ClickGuiCommand());
    }

    public static void setCmdPrefix(String prefix) {
        cmdPrefix = prefix;
    }

    public static List<CommandImplementation> getCommands() {
        return COMMANDS;
    }

    public static int numCommands() {
        return getCommands().size();
    }

    public static <T extends CommandImplementation> T getCommand(Class<T> commandClass) {
        for (var command : getCommands()) {
            if (command.getClass() == commandClass) {
                return commandClass.cast(command);
            }
        }
        return null;
    }

    public static void executeCommand(Class<? extends CommandImplementation> commandClass, String[] parts) {
        var command = getCommand(commandClass);
        if (command != null) {
            command.executeCommand(parts);
        }
    }

    public static CategoryImplementation.Categories getCategory(Class<?> clazz) {
        return clazz.getAnnotation(ModuleInfoAnnotation.class).category();
    }
}
