package de.constt.nyra.client.roots.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class SettingImplementation<T> {

    private final String name;
    protected T value;
    private final List<Consumer<SettingImplementation<T>>> changeListeners = new ArrayList<>();

    protected SettingImplementation(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        // Only trigger if value actually changes
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            notifyChangeListeners();
        }
    }

    /**
     * Adds a listener that gets called when this setting's value changes
     */
    public void addChangeListener(Consumer<SettingImplementation<T>> listener) {
        changeListeners.add(listener);
    }

    /**
     * Removes a previously added change listener
     */
    public void removeChangeListener(Consumer<SettingImplementation<T>> listener) {
        changeListeners.remove(listener);
    }

    private void notifyChangeListeners() {
        for (Consumer<SettingImplementation<T>> listener : changeListeners) {
            listener.accept(this);
        }
    }

    public abstract void renderImGui();
}