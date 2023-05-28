package dev.kikugie.commandconfig.api;

import dev.kikugie.commandconfig.impl.config.CategoryBuilderImpl;
import dev.kikugie.commandconfig.impl.config.CommandConfigBuilderImpl;
import dev.kikugie.commandconfig.impl.config.OptionBuilderImpl;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface CommandConfigBuilder<S extends CommandSource> extends CommandNode<S> {
    static <S extends CommandSource> CommandConfigBuilderImpl<S> create(String name) {
        return new CommandConfigBuilderImpl<>(name);
    }

    CommandConfigBuilder<S> category(@NotNull Supplier<CategoryBuilder<S>> category);

    CommandConfigBuilder<S> option(@NotNull Supplier<OptionBuilder<?, S>> option);
}
