package dev.kikugie.commandconfig.api.builders;

import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.impl.config.builders.CommandConfigBuilderImpl;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface CommandConfigBuilder<S extends CommandSource> extends CommandNode<S> {
    static <S extends CommandSource> CommandConfigBuilderImpl<S> create(String name, Class<S> type) {
        return new CommandConfigBuilderImpl<>(name, type);
    }

    CommandConfigBuilder<S> category(@NotNull Function<Class<S>, CategoryBuilder<S>> category);

    CommandConfigBuilder<S> option(@NotNull Function<Class<S>, OptionBuilder<?, S>> option);
}
