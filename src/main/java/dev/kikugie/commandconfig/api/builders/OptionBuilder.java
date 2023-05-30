package dev.kikugie.commandconfig.api.builders;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.api.option.ExtendedOptions;
import dev.kikugie.commandconfig.api.option.access.OptionValueAccess;
import dev.kikugie.commandconfig.api.option.SimpleOptions;
import dev.kikugie.commandconfig.impl.option.GenericOptionBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Option node in the command.
 *
 * @param <T> Argument used in the option
 * @param <S> CommandSource type
 * @see #generic(String, ArgumentType, Class, Class)
 * @see SimpleOptions
 * @see ExtendedOptions
 */
@SuppressWarnings("unused")
public interface OptionBuilder<T, S extends CommandSource> extends CommandNode<S> {
    /**
     * Creates a builder for any simple argument type.
     * <br>
     * Generic option example:
     * <pre>{@code
     * // Category setup
     * .option((source) -> {
     *     // In this case SimpleOptions.integer() can be used
     *     OptionBuilder option = OptionBuilder.generic("example",
     *             IntegerArgumentType.integer(),
     *             Integer.class,
     *             source);
     *     option.valueAccess(
     *             () -> Text.of("Example option is " + config.getExample()),
     *             (val) -> {
     *                 config.setExample(val):
     *                 return Text.of("Value set!");
     *             }
     *     );
     *     option.helpFunc(() -> Text.of("This is an example integer option."));
     *     option.listener((val, id) -> config.update(val, id));
     *     return option;
     * })
     * }</pre>
     * Produced command:
     * <pre>{@code
     * /... example -> get value
     *              \> <Integer> -> set value
     * }</pre>
     *
     * @param name         option name. Cannot contain spaces
     * @param argumentType command argument type matching `valueType`
     * @param valueType    value type of the option
     * @param type         CommandSource class reference, passed from top level node
     * @return {@link GenericOptionBuilderImpl}
     * @see SimpleOptions
     * @see ExtendedOptions
     */
    static <T, S extends CommandSource> OptionBuilder<T, S> generic(String name, ArgumentType<T> argumentType, Class<T> valueType, Class<S> type) {
        return new GenericOptionBuilderImpl<>(name, argumentType, valueType, type);
    }

    /**
     * Adds a custom command node to the option. Here be dragons!
     * @param node Command node builder
     * @return this
     */
    OptionBuilder<T, S> node(@NotNull ArgumentBuilder<S, ?> node);

    /**
     * Interface for modifying config state.
     *
     * @param access {@link OptionValueAccess} instance
     * @return this
     */
    OptionBuilder<T, S> valueAccess(@NotNull OptionValueAccess<T> access);

    /**
     * Interface for modifying config state.
     *
     * @param getter Gets the value and returns response {@link Text}
     * @param setter Accepts new value and returns response {@link Text}
     * @return this
     */
    OptionBuilder<T, S> valueAccess(@NotNull Supplier<Text> getter, @NotNull Function<T, Text> setter);

    /**
     * Adds a listener that is invoked upon changing the value.
     *
     * @param listener Accepts new value and option's ID
     * @return this
     */
    OptionBuilder<T, S> listener(@NotNull BiConsumer<T, String> listener);

    /**
     * Specifies result output function.
     *
     * @param printFunc Accepts {@link CommandContext} and {@link Text}, produces integer result
     * @return this
     */
    OptionBuilder<T, S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc);

    /**
     * Runs every time value is set. Basically, a global listener.
     *
     * @param saveFunc Saving runnable
     * @return this
     */
    OptionBuilder<T, S> saveFunc(@NotNull Runnable saveFunc);

    /**
     * Specifies value used for `help` subcommand.
     *
     * @param helpFunc Produces helper text
     * @return this
     */
    OptionBuilder<T, S> helpFunc(@NotNull Supplier<Text> helpFunc);
}
