package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.kikugie.commandconfig.impl.config.OptionBuilderImpl;
import dev.kikugie.commandconfig.impl.config.option.GenericOptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OptionBuilder<T, S extends CommandSource> extends CommandNode<S> {
    static <T, S extends CommandSource> OptionBuilder<T, S> generic(String name, Supplier<ArgumentType<T>> argumentType, Class<T> type) {
        return new GenericOptionBuilder<>(name, argumentType, type);
    }

    static <S extends CommandSource> OptionBuilder<Integer, S> integer(String name) {
        return new GenericOptionBuilder<>(name, IntegerArgumentType::integer, Integer.class);
    }

    OptionBuilder<T, S> value(@NotNull Supplier<Text> getter, @NotNull Function<T, Text> setter);

    OptionBuilder<T, S> listener(@NotNull BiConsumer<String, T> listener);
}
