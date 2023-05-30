package dev.kikugie.commandconfig.api.builders;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.api.option.access.ListElementAccess;
import dev.kikugie.commandconfig.impl.command.ListArgumentType;
import dev.kikugie.commandconfig.impl.option.ListOptionBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * List option node
 *
 * @param <L> List type
 * @param <T> List value type
 * @param <S> CommandSource type
 */
@SuppressWarnings("unused")
public interface ListOptionBuilder<L extends List<T>, T, S extends CommandSource> extends OptionBuilder<L, S> {
    /**
     * Creates a builder for a list option type.
     * <br>
     * Methods inherited from {@link OptionBuilder} apply to entire list. Optionally, {@link #elementAccess(ListElementAccess)} can be specified, which adds additional command nodes.
     * List option example:
     * <pre>{@code
     * // Category setup
     * .option((source) -> {
     *     // In this example ExtendedOptions.intList() can be used
     *     ListOptionBuilder option = ListOptionBuilder.genericList("example-list",
     *             IntegerArgumentType.integer(),
     *             Integer.class,
     *             source);
     *     option.valueAccess(
     *             () -> Text.of("Example list is " + config.getExampleList()),
     *             (val) -> {
     *                 config.setExampleList(val);
     *                 return Text.of("List set to " + val);
     *             }
     *     );
     *     option.elementAccess(
     *             (index) -> Text.of("Element at index " + index + " is " + config.getExampleList().get(index)),
     *             (index, val) -> {
     *                 config.getExampleList().set(index, val);
     *                 return Text.of("Element " + val + " set at index " + index);
     *             },
     *             (val) -> {
     *                 config.getExampleList().add(val);
     *                 return Text.of("Added element " + val);
     *             },
     *             (index) -> {
     *                 int removed = config.getExampleList().remove(index);
     *                 return new Pair<>(removed, Text.of("Removed element " + removed));
     *             }
     *     );
     * })
     * }</pre>
     * Produced command:
     * <pre>{@code
     * /... example-list list -> get list
     *                 |      \> [Integer...] -> set list
     *                 - get <index> -> get element
     *                 - set <index> <Integer> -> set element
     *                 - add <Integer> -> append element
     *                 \ remove <index> -> remove element
     * }</pre>
     *
     * @param name         option name. Cannot contain spaces
     * @param argumentType command argument type matching {@code `valueType`}
     * @param valueType    value type of the option
     * @param type         {@link CommandSource} class reference, passed from top level node
     * @return {@link ListOptionBuilderImpl}
     */
    static <L extends List<T>, S extends CommandSource, T> ListOptionBuilder<L, T, S> genericList(String name, ArgumentType<T> argumentType, Class<T> valueType, Class<S> type) {
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, valueType, type);
    }

    /**
     * Interface for modifying list elements.
     * <br>
     * <h2>Index bounds must be checked by the config state or provided functions</h2>
     *
     * @param access {@link ListElementAccess} instance
     * @return this
     */
    ListOptionBuilder<L, T, S> elementAccess(@NotNull ListElementAccess<T, S> access);

    /**
     * Interface for modifying list elements.
     * <br>
     * <h2>Index bounds must be checked by the config state or provided functions</h2>
     *
     * @param getter   Gets list element and returns response {@link Text}
     * @param setter   Accepts new element at specified index and returns response {@link Text}
     * @param appender Accepts new element to append at the end of the list and returns response {@link Text}
     * @param remover  Accepts element index, returns removed value and response {@link Text}
     * @return this
     */
    ListOptionBuilder<L, T, S> elementAccess(@NotNull BiFunction<CommandContext<S>, Integer, Text> getter,
                                             @NotNull TriFunction<CommandContext<S>, Integer, T, Text> setter,
                                             @NotNull BiFunction<CommandContext<S>, T, Text> appender,
                                             @NotNull BiFunction<CommandContext<S>, Integer, Pair<@Nullable T, Text>> remover);

    /**
     * Adds a listener that is invoked upon changing an element.
     *
     * @param listener Accepts new value and option's name
     * @return this
     */
    ListOptionBuilder<L, T, S> elementListener(@NotNull BiConsumer<T, String> listener);
}
