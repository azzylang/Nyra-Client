package de.constt.nyra.client.roots.implementations;


public class CommandImplementation {
    protected boolean enabled = false;

    protected String[] args;

    public void executeCommand(String[] parts) {
        toggle();
    }


    public void tick() {}

    public void toggle() {
        enabled = !enabled;
    }

    public boolean getEnabledStatus() {
        return enabled;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return this.args;
    }
}