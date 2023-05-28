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

public interface OptionBuilder<T> {
    static <T> OptionBuilder<T> generic(String name, Supplier<ArgumentType<T>> argumentType, Class<T> type) {
        return new GenericOptionBuilder<>(name, argumentType, type);
    }

    OptionBuilder<T> printFunc(BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc);

    OptionBuilder<T> saveFunc(Runnable saveFunc);

    OptionBuilder<T> value(OptionValue<T> value);

    OptionBuilder<T> value(Supplier<Text> getter, Function<T, Text> setter);

    OptionBuilder<T> help(Supplier<Text> help);

    LiteralArgumentBuilder<CommandSource> build();

    boolean hasPrintFunc();

    boolean hasSaveFunc();
}
