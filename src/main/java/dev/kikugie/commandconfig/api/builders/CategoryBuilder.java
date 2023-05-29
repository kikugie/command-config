package dev.kikugie.commandconfig.api.builders;

import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.impl.builders.CategoryBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <S> CommandSource type
 */
public interface CategoryBuilder<S extends CommandSource> extends CommandNode<S> {
    /**
     * Creates a category builder, which can contain options and other categories.
     * <br>
     * Example category:
     * <pre>{@code
     * // Base builder or category setup
     * .category((source) -> {
     *     CategoryBuilder category = CategoryBuilder.create("example-category", source);
     *     category.option((source) -> {...});
     *     category.helpFunc(() -> Text.of("This category has options!"));
     *     return category;
     * })
     * }</pre>
     *
     * @param name category name. Cannot contain spaces
     * @param type CommandSource class reference, passed from top level node
     * @return {@link CategoryBuilderImpl}
     */
    static <S extends CommandSource> CategoryBuilder<S> create(String name, Class<S> type) {
        return new CategoryBuilderImpl<>(name, type);
    }

    /**
     * Creates a category, which can contain options and other categories.
     *
     * @param category Provides CommandSource class reference for creating a category, returns a builder for it
     * @return this
     */
    CategoryBuilder<S> category(@NotNull Function<Class<S>, CategoryBuilder<S>> category);

    /**
     * Creates an option with arbitrary type.
     *
     * @param option Provides CommandSource class reference for creating an option, returns a builder for it
     * @return this
     */
    CategoryBuilder<S> option(@NotNull Function<Class<S>, OptionBuilder<?, S>> option);

    /**
     * Specifies value used for `help` subcommand.
     *
     * @param helpFunc Produces helper text
     * @return
     */
    @Override
    CategoryBuilder<S> helpFunc(@NotNull Supplier<Text> helpFunc);
}
