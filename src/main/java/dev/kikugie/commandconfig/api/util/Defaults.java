package dev.kikugie.commandconfig.api.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.option.access.ListElementAccess;
import dev.kikugie.commandconfig.api.option.access.OptionValueAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;

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
    public static <T> OptionValueAccess<T> defaultValueAccess(Supplier<T> getter, Consumer<T> setter) {
        return new OptionValueAccess<>(
                () -> Reference.translated("commandconfig.response.option.get", getter.get()),
                (val) -> {
                    setter.accept(val);
                    return Reference.translated("commandconfig.response.option.set", val);
                });
    }

    public static <L extends List<T>, T> ListElementAccess<T> defaultElementAccess(Supplier<L> listSupplier) {
        return new ListElementAccess<>(
                (index) -> {
                    L list = listSupplier.get();
                    return index >= 0 && index < list.size() ? Reference.translated("commandconfig.response.option.element.get", list.get(index)) : Reference.translated("commandconfig.response.error.invalid_index", index);
                },
                (index, value) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        list.set(index, value);
                        return Reference.translated("commandconfig.response.option.element.set", value, index);
                    }
                    return Reference.translated("commandconfig.response.error.invalid_index", index);
                },
                (value) -> {
                    L list = listSupplier.get();
                    list.add(value);
                    return Reference.translated("commandconfig.response.option.element.add", value, list.size());
                },
                (index) -> {
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

    public static BiFunction<CommandContext<FabricClientCommandSource>, Text, Integer> clientPrintFunc() {
        return (context, text) -> {
            context.getSource().sendFeedback(text);
            return 1;
        };
    }

    public static BiFunction<CommandContext<ServerCommandSource>, Text, Integer> serverPrintFunc() {
        return (context, text) -> {
            context.getSource().sendFeedback(text, false);
            return 1;
        };
    }

    public static BiFunction<CommandContext<CommandContext<?>>, Text, Integer> loggerPrintFunc(Logger logger) {
        return (context, text) -> {
            logger.info(text.toString());
            return 1;
        };
    }
}
