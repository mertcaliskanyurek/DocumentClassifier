package main.gui;

public interface WindowInterface {

    void addLog(String log);
    void addOutput(String output);
    void clearOutput();
    void setEnabledAllComponents(boolean enabled);
}
