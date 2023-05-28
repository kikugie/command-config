package dev.kikugie.commandconfig.api.builders;

import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.impl.config.builders.CategoryBuilderImpl;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface CategoryBuilder<S extends CommandSource> extends CommandNode<S> {
    static <S extends CommandSource> CategoryBuilderImpl<S> create(String name, Class<S> type) {
        return new CategoryBuilderImpl<>(name, type);
    }

    CategoryBuilder<S> category(@NotNull Function<Class<S>, CategoryBuilder<S>> category);

    CategoryBuilder<S> option(@NotNull Function<Class<S>, OptionBuilder<?, S>> option);
}
