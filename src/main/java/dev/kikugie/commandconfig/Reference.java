package dev.kikugie.commandconfig;

public class Reference {
    public static final String MOD_ID = "commandconfig";
    public static final String MOD_VERSION = "0.0.0";
    public static final String MOD_NAME = "Command Config";
    public static final String ALLOWED_NAMES = "^[a-zA-Z0-9-_]+$";
    public static final String MISSING_PRINT_FUNC = "printFunc must be set for %s \"%s\" or passed from top level node";
    public static final String MISSING_SAVE_FUNC = "saveFunc must be set for %s \"%s\" or passed from top level node";
    public static final String MISSING_HELP_FUNC = "helpFunc must be set for %s \"%s\"";
    public static final String NULL_LISTENER = "Listener can't be null for %s \"%s\"";
    public static final String NULL_OPTION = "Option can't be null for %s \"%s\"";
    public static final String NULL_CATEGORY = "Category can't be null for %s \"%s\"";
    public static final String INVALID_NAME = "Invalid name for %s: \"%s\".\nOption and category names can only contain letters, numbers, dashes and underscores without spaces.";

    public static String optionError(String name, String error) {
        return String.format(error, "option", name);
    }

    public static String categoryError(String name, String error) {
        return String.format(error, "category", name);
    }

    public static String baseError(String name, String error) {
        return String.format(error, "command", name);
    }
}
