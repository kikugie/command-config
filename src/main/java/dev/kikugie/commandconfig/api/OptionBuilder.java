package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.impl.config.option.GenericOptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OptionBuilder<T, S extends CommandSource> {
    static <T, S extends CommandSource> OptionBuilder<T, S> generic(String name, Supplier<ArgumentType<T>> argumentType, Class<T> type) {
        return new GenericOptionBuilder<>(name, argumentType, type);
    }

    OptionBuilder<T, S> printFunc(BiFunction<CommandContext<S>, Text, Integer> printFunc);

    OptionBuilder<T, S> saveFunc(Runnable saveFunc);

    OptionBuilder<T, S> value(OptionValue<T> value);

    OptionBuilder<T, S> value(Supplier<Text> getter, Function<T, Text> setter);

    OptionBuilder<T, S> help(Supplier<Text> help);

    LiteralArgumentBuilder<S> build();

    boolean hasPrintFunc();

    boolean hasSaveFunc();
}
