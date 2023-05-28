package dev.kikugie.commandconfig.api.builders;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.impl.config.option.GenericOptionBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OptionBuilder<T, S extends CommandSource> extends CommandNode<S> {
    static <T, S extends CommandSource> OptionBuilder<T, S> generic(String name, ArgumentType<T> argumentType, Class<T> valueType, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, argumentType, valueType, type);
    }

    OptionBuilder<T, S> value(@NotNull Supplier<Text> getter, @NotNull Function<T, Text> setter);

    OptionBuilder<T, S> listener(@NotNull Consumer<T> listener);
}
