package dev.kikugie.commandconfig.api;

import dev.kikugie.commandconfig.impl.config.CategoryBuilderImpl;
import dev.kikugie.commandconfig.impl.config.OptionBuilderImpl;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface CategoryBuilder<S extends CommandSource> extends CommandNode<S> {
    static <S extends CommandSource> CategoryBuilderImpl<S> create(String name) {
        return new CategoryBuilderImpl<>(name);
    }

    CategoryBuilder<S> category(@NotNull Supplier<CategoryBuilder<S>> category);

    CategoryBuilder<S> option(@NotNull Supplier<OptionBuilder<?, S>> option);
}
