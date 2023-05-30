package dev.kikugie.commandconfig.api.option;

import com.mojang.brigadier.arguments.*;
import dev.kikugie.commandconfig.api.builders.ListOptionBuilder;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import dev.kikugie.commandconfig.impl.command.CustomEnumArgumentType;
import dev.kikugie.commandconfig.impl.command.ListArgumentType;
import dev.kikugie.commandconfig.impl.option.GenericOptionBuilderImpl;
import dev.kikugie.commandconfig.impl.option.ListOptionBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.util.StringIdentifiable;

import java.util.List;

/**
 * Wrappers for complex options.
 */
@SuppressWarnings("unused")
public class ExtendedOptions {
    /**
     * Creates an enum option builder. Passed enum must implement {@link StringIdentifiable}.
     *
     * @param name    Option name. Cannot contain spaces
     * @param enumArg Target enum class
     * @param type    {@link CommandSource} class reference, passed from top level node
     */
    public static <T extends Enum<T> & StringIdentifiable, S extends CommandSource> OptionBuilder<T, S> enumArg(String name, Class<T> enumArg, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, CustomEnumArgumentType.enumArg(enumArg), enumArg, type);
    }

    public static <L extends List<Integer>, S extends CommandSource> ListOptionBuilder<L, Integer, S> intList(String name, Class<S> type) {
        IntegerArgumentType argumentType = IntegerArgumentType.integer();
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, Integer.class, type);
    }

    public static <L extends List<Long>, S extends CommandSource> ListOptionBuilder<L, Long, S> longList(String name, Class<S> type) {
        LongArgumentType argumentType = LongArgumentType.longArg();
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, Long.class, type);
    }

    public static <L extends List<Float>, S extends CommandSource> ListOptionBuilder<L, Float, S> floatList(String name, Class<S> type) {
        FloatArgumentType argumentType = FloatArgumentType.floatArg();
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, Float.class, type);
    }

    public static <L extends List<Double>, S extends CommandSource> ListOptionBuilder<L, Double, S> doubleList(String name, Class<S> type) {
        DoubleArgumentType argumentType = DoubleArgumentType.doubleArg();
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, Double.class, type);
    }

    public static <L extends List<String>, S extends CommandSource> ListOptionBuilder<L, String, S> stringList(String name, Class<S> type) {
        StringArgumentType argumentType = StringArgumentType.word();
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, String.class, type);
    }

    public static <L extends List<String>, S extends CommandSource> ListOptionBuilder<L, String, S> quotedStringList(String name, Class<S> type) {
        StringArgumentType argumentType = StringArgumentType.string();
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, String.class, type);
    }
}
