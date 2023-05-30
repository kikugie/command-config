package dev.kikugie.commandconfig.testmod;

public class ExampleConfig {
    private static ExampleConfig instance;
    public int intOpt = 0;
    public String strOpt = "";

    public static ExampleConfig getInstance() {
        if (instance == null) {
            instance = new ExampleConfig();
        }
        return instance;
    }

    public static void reset() {
        instance = new ExampleConfig();
    }

    public static void save() {
        // Command Config Lib currently doesn't provide config saving, implementation is left to the user
    }
}
