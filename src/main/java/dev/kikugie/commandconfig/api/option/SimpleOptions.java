package dev.kikugie.commandconfig.api.option;

import com.mojang.brigadier.arguments.*;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import dev.kikugie.commandconfig.impl.config.option.GenericOptionBuilderImpl;
import net.minecraft.command.CommandSource;

public class SimpleOptions<T, S extends CommandSource> {
    public static <S extends CommandSource> OptionBuilder<Boolean, S> bool(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, BoolArgumentType.bool(), Boolean.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Integer, S> integer(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, IntegerArgumentType.integer(), Integer.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Integer, S> integer(String name, int min, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, IntegerArgumentType.integer(min), Integer.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Integer, S> integer(String name, int min, int max, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, IntegerArgumentType.integer(min, max), Integer.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Long, S> longArg(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, LongArgumentType.longArg(), Long.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Long, S> longArg(String name, long min, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, LongArgumentType.longArg(min), Long.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Long, S> longArg(String name, long min, long max, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, LongArgumentType.longArg(min, max), Long.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Float, S> floatArg(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, FloatArgumentType.floatArg(), Float.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Float, S> floatArg(String name, float min, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, FloatArgumentType.floatArg(min), Float.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Float, S> floatArg(String name, float min, float max, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, FloatArgumentType.floatArg(min, max), Float.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Double, S> doubleArg(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, DoubleArgumentType.doubleArg(), Double.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Double, S> doubleArg(String name, double min, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, DoubleArgumentType.doubleArg(min), Double.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<Double, S> doubleArg(String name, double min, double max, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, DoubleArgumentType.doubleArg(min, max), Double.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<String, S> string(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, StringArgumentType.word(), String.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<String, S> quotedString(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, StringArgumentType.string(), String.class, type);
    }

    public static <S extends CommandSource> OptionBuilder<String, S> greedyString(String name, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, StringArgumentType.greedyString(), String.class, type);
    }
}
