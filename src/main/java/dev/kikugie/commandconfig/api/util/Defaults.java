package dev.kikugie.commandconfig.api.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.option.access.ListElementAccess;
import dev.kikugie.commandconfig.api.option.access.OptionValueAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
     * @param <T>    Value type
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
     * @param <L>          List type
     * @param <T>          Value type
     * @return {@link ListElementAccess} to be passed to a list option
     */
    public static <L extends List<T>, T, S extends CommandSource> ListElementAccess<T, S> defaultElementAccess(Supplier<L> listSupplier) {
        return defaultElementAccess("commandconfig.response.option.element", listSupplier);
    }

    /**
     * Alternative default element access provider with specified translation key.
     * For given key following variants should exist:
     * <pre>{@code
     * - "{key}.get"
     * - "{key}.set"
     * - "{key}.add"
     * - "{key}.remove"
     * }</pre>
     *
     * @param listSupplier Supplier that returns target list reference
     * @param <L>          List type
     * @param <T>          Value type
     * @return {@link ListElementAccess} to be passed to a list option
     */
    public static <L extends List<T>, T, S extends CommandSource> ListElementAccess<T, S> defaultElementAccess(String baseTranslationKey, Supplier<L> listSupplier) {
        return new ListElementAccess<>(
                (context, index) -> {
                    L list = listSupplier.get();
                    return index >= 0 && index < list.size() ? Reference.translated(baseTranslationKey + ".get", list.get(index)) : Reference.translated("commandconfig.response.error.invalid_index", index, list.size());
                },
                (context, index, value) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        list.set(index, value);
                        return Reference.translated(baseTranslationKey + ".set", value, index);
                    }
                    return Reference.translated("commandconfig.response.error.invalid_index", index, list.size());
                },
                (context, value) -> {
                    L list = listSupplier.get();
                    list.add(value);
                    return Reference.translated(baseTranslationKey + ".add", value, list.size());
                },
                (context, index) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        T value = list.get(index);
                        // Uses removeAll to avoid remove method ambiguity
                        list.removeAll(List.of(value));
                        return new Pair<>(value, Reference.translated(baseTranslationKey + ".remove", value, index));
                    }
                    return new Pair<>(null, Reference.translated("commandconfig.response.error.invalid_index", index, list.size()));
                });
    }

    /**
     * Client-side print function that responds in the game chat.
     *
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
     *
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
     *
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
     *
     * @return Print function to be passed to {@code printFunc()} method
     */
    public static BiFunction<CommandContext<CommandContext<?>>, Text, Integer> loggerPrintFunc(Logger logger) {
        return (context, text) -> {
            logger.info(text.toString());
            return 1;
        };
    }
}
