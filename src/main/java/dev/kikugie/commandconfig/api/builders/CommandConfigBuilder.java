package dev.kikugie.commandconfig.api.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.impl.builders.CommandConfigBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Main class of the mod.
 *
 * @param <S> Command source used in command responses. Use FabricClientCommandSource or ServerCommandSource
 */
public interface CommandConfigBuilder<S extends CommandSource> extends CommandNode<S> {
    /**
     * Creates a command builder. After adding all features should be finalised with {@link #build()} to produce a command node.
     * <br>
     * Example client-side config setup:
     * <pre>{@code
     * var config = CommandConfigBuilder.create("test-config", FabricClientCommandSource.class)
     *         .option((source) -> {
     *             var option = SimpleOptions.integer("test-int", source);
     *             option.valueAccess(() -> Text.of("Getting int value"),
     *                     (val) -> Text.of("Setting int value"));
     *             option.helpFunc(() -> Text.of("`test-int` helper"));
     *             return option;
     *         })
     *         .category((source) -> {
     *             var category = CategoryBuilder.create("test-category", source);
     *             category.option((optionSource) -> {
     *                 var option = SimpleOptions.bool("test-bool", optionSource);
     *                 option.valueAccess(() -> Text.of("Getting bool value"),
     *                         (val) -> Text.of("Setting bool value"));
     *                 return option;
     *             });
     *             category.helpFunc(() -> Text.of("`test-category` helper"));
     *             return category;
     *         })
     *         .printFunc((context, text) -> {
     *             context.getSource().sendFeedback(text);
     *             return 1;
     *         })
     *         .helpFunc(() -> Text.of("`test-config` helper"))
     *         .saveFunc(() -> {
     *         })
     *         .build();
     * ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(config));
     * }</pre>
     * If a command node has {@code helpFunc()} spedified, `help` node will be generated at the base command.
     * <br>
     * Produced command tree:
     * <pre>{@code
     * /test-config test-int -> "Getting int value"
     *            |          \> <Integer> -> "Setting int value"
     *            - test-category test-bool -> "Getting bool value"
     *            |                         \> <Boolean> -> "Setting bool value"
     *            - help -> "`test-config` helper"
     *                   - test-int -> "`test-int` helper"
     *                   \ test-category -> "`test-category` helper"
     *                   // `test-bool` doesn't specify a helper function
     * }</pre>
     *
     * @param name base command name. Cannot contain spaces
     * @param type CommandSource class reference
     * @return {@link CommandConfigBuilderImpl}
     */
    static <S extends CommandSource> CommandConfigBuilderImpl<S> create(String name, Class<S> type) {


        return new CommandConfigBuilderImpl<>(name, type);
    }

    /**
     * Creates a category, which can contain options and other categories.
     *
     * @param category Provides CommandSource class reference for creating a category, returns a builder for it
     * @return this
     */
    CommandConfigBuilder<S> category(@NotNull Function<Class<S>, CategoryBuilder<S>> category);

    /**
     * Creates an option with arbitrary type.
     *
     * @param option Provides CommandSource class reference for creating an option, returns a builder for it
     * @return this
     */
    CommandConfigBuilder<S> option(@NotNull Function<Class<S>, OptionBuilder<?, S>> option);

    /**
     * Specifies result output function.
     *
     * @param printFunc Accepts {@link CommandContext} and {@link Text}, produces integer result
     * @return this
     */
    @Override
    CommandConfigBuilder<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc);

    /**
     * Runs every time value is set. Basically, a global listener.
     *
     * @param saveFunc Saving runnable
     * @return
     */
    @Override
    CommandConfigBuilder<S> saveFunc(@NotNull Runnable saveFunc);

    /**
     * Specifies value used for `help` subcommand.
     *
     * @param helpFunc Produces helper text
     * @return
     */
    @Override
    CommandConfigBuilder<S> helpFunc(@NotNull Supplier<Text> helpFunc);

    /**
     * Gathers all options and categories, producing a command tree.
     *
     * @return Top level command node with all branches.
     */
    @NotNull
    @Override
    LiteralArgumentBuilder<S> build();
}
