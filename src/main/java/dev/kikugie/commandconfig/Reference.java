package dev.kikugie.commandconfig;

import net.minecraft.text.Text;

import java.util.function.Supplier;

//#if MC < 11900
//$$ import net.minecraft.text.TranslatableText;
//#endif

@SuppressWarnings("unused")
public class Reference {
    public static final String MOD_ID = "command-config";
    public static final String MOD_VERSION = "0.1.0";
    public static final String MOD_NAME = "Command Config Lib";
    public static final String ALLOWED_NAMES = "^[a-zA-Z0-9-_]+$";
    public static final String INVALID_NAME = "Invalid name for %s: \"%s\".\nOption and category names can only contain letters, numbers, dashes and underscores without spaces.";
    public static final String NULL_OPTION = "Option can't be null for %s \"%s\"";
    public static final String NULL_CATEGORY = "Category can't be null for %s \"%s\"";
    public static final String NULL_NODE = "Custom node can't be null for %s \"%s\"";
    public static final String NULL_LISTENER = "Listener can't be null for %s \"%s\"";
    public static final String NULL_ELEMENT_ACCESS = "Element access can't be null for %s \"%s\"";
    public static final String NULL_VALUE_ACCESS = "Value access can't be null for %s \"%s\"";
    public static final String NO_PRINT_FUNC = "No print function for %s \"%s\".\nAdd it using `printFunc` on current or any higher node.";
    public static final String NO_HELP_FUNC = "No help function for %s \"%s\".\nAdd it using `helpFunc` on current or any higher node.";
    public static final String NO_SAVE_FUNC = "No save function for %s \"%s\".\nAdd it using `saveFunc` on current or any higher node.";
    public static final String NO_ELEMENT_LISTENER = "No element access for list %s \"%s\".\nAdd it using `elementAccess()` before adding listeners.";
    public static final String NO_VALUE_LISTENER = "No value access for %s \"%s\".\nAdd it using `valueAccess()` before adding listeners.";
    public static final String NO_VALUE_ACCESS = "No value access for %s \"%s\".\nAdd it using `valueAccess()`.";
    public static final Supplier<Text> NO_HELP_SAD = () -> translated("commandconfig.response.error.no_help_func");

    public static String optionError(String name, String error) {
        return String.format(error, "option", name);
    }

    public static String categoryError(String name, String error) {
        return String.format(error, "category", name);
    }

    public static String baseError(String name, String error) {
        return String.format(error, "command", name);
    }
    public static String nodeError(String name, String error) {
        return String.format(error, "node", name);
    }


    /**
     * Wrapper for translatable text for <1.19 compatibility
     * @param key Translation key
     * @return Translated text
     */
    public static Text translated(String key) {
        //#if MC > 11802
        return Text.translatable(key);
        //#else
        //$$ return new TranslatableText(key);
        //#endif
    }

    /**
     * Wrapper for translatable text for <1.19 compatibility
     * @param key Translation key
     * @param args Translation arguments
     * @return Translated text
     */
    public static Text translated(String key, Object... args) {
        //#if MC > 11802
        return Text.translatable(key, args);
        //#else
        //$$ return new TranslatableText(key, args);
        //#endif
    }
}
