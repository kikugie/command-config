package dev.kikugie.commandconfig.api.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.option.access.ListElementAccess;
import dev.kikugie.commandconfig.api.option.access.OptionValueAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.*;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

//#if MC > 11802
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//#else
//$$ import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
//#endif

/* backup in case formatter yeets it
    //#if MC > 11802
    import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
    //#else
    //$$ import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
    //#endif
 */

@SuppressWarnings("unused")
public class Defaults {
    /**
     * Provides default value access with following responses:
     * <pre>{@code
     * - get: Option value: <value>
     * - set: Option value set to: <value>
     * }</pre>
     *
     * @param getter Value getter
     * @param setter Value setter
     * @param <T> Value type
     * @return {@link OptionValueAccess} to be passed to an option
     */
    public static <T, S extends CommandSource> OptionValueAccess<T, S> defaultValueAccess(Supplier<T> getter, Consumer<T> setter) {
        return new OptionValueAccess<>(
                (context) -> Reference.translated("commandconfig.response.option.get", getter.get()),
                (context, val) -> {
                    setter.accept(val);
                    return Reference.translated("commandconfig.response.option.set", val);
                });
    }

    /**
     * Provides default list element access with following responses:
     * <pre>{@code
     * - get: Element value: <value>
     * - set: Set element value <value> at index <index>
     * - add: Added element value <value> at index <index>
     * - remove: Removed element value <value> at index <index>
     * - invalid index: Index <index> is out of bounds
     * }</pre>
     *
     * @param listSupplier Supplier that returns target list reference
     * @param <L> List type
     * @param <T> Value type
     * @return {@link ListElementAccess} to be passed to a list option
     */
    public static <L extends List<T>, T, S extends CommandSource> ListElementAccess<T, S> defaultElementAccess(Supplier<L> listSupplier) {
        return new ListElementAccess<>(
                (context,index) -> {
                    L list = listSupplier.get();
                    return index >= 0 && index < list.size() ? Reference.translated("commandconfig.response.option.element.get", list.get(index)) : Reference.translated("commandconfig.response.error.invalid_index", index);
                },
                (context,index, value) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        list.set(index, value);
                        return Reference.translated("commandconfig.response.option.element.set", value, index);
                    }
                    return Reference.translated("commandconfig.response.error.invalid_index", index);
                },
                (context,value) -> {
                    L list = listSupplier.get();
                    list.add(value);
                    return Reference.translated("commandconfig.response.option.element.add", value, list.size());
                },
                (context,index) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        T value = list.get(index);
                        // Uses removeAll to avoid remove method ambiguity
                        list.removeAll(List.of(value));
                        return new Pair<>(value, Reference.translated("commandconfig.response.option.element.remove", value, index));
                    }
                    return new Pair<>(null, Reference.translated("commandconfig.response.error.invalid_index", index));
                });
    }

    /**
     * Provides a literal node, that runs provided function upon executing.
     *
     * @param name Node name
     * @param func Function to execute
     * @param <S> {@link CommandSource} type
     * @return {@link LiteralArgumentBuilder} to be passed to {@code node()} method
     */
    public static <S extends CommandSource> LiteralArgumentBuilder<S> executorNode(@NotNull String name, @NotNull Function<CommandContext<S>, Integer> func) {
        LiteralArgumentBuilder<S> node = literal(name);
        return node.executes(func::apply);
    }

    /**
     * Provides a literal node named "reset", that runs provided config reset function.
     *
     * @param func Config reset function
     * @param <S> {@link CommandSource} type
     * @return {@link LiteralArgumentBuilder} to be passed to {@code node()} method
     */
    public static <S extends CommandSource> LiteralArgumentBuilder<S> resetNode(@NotNull Function<CommandContext<S>, Integer> func) {
        return executorNode("reset", func);
    }

    /**
     * Client-side print function that responds in the game chat.
     * @return Print function to be passed to {@code printFunc()} method
     */
    public static BiFunction<CommandContext<FabricClientCommandSource>, Text, Integer> clientPrintFunc() {
        return (context, text) -> {
            context.getSource().sendFeedback(text);
            return 1;
        };
    }

    /**
     * Server-side print function that responds to the source player in the game chat.
     * @return Print function to be passed to {@code printFunc()} method
     */
    public static BiFunction<CommandContext<ServerCommandSource>, Text, Integer> serverPrintFunc() {
        return (context, text) -> {
            context.getSource().sendFeedback(text, false);
            return 1;
        };
    }

    /**
     * Server-side print function that responds to the source player in the game chat and broadcasts to server operators.
     * @return Print function to be passed to {@code printFunc()} method
     */
    public static BiFunction<CommandContext<ServerCommandSource>, Text, Integer> broadcastPrintFunc() {
        return (context, text) -> {
            context.getSource().sendFeedback(text, true);
            return 1;
        };
    }

    /**
     * Print function that only writes responses to the provided logger.
     * @return Print function to be passed to {@code printFunc()} method
     */
    public static BiFunction<CommandContext<CommandContext<?>>, Text, Integer> loggerPrintFunc(Logger logger) {
        return (context, text) -> {
            logger.info(text.toString());
            return 1;
        };
    }
}
