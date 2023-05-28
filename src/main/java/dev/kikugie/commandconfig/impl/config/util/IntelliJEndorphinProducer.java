package dev.kikugie.commandconfig.impl.config.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BiFunction;

public class IntelliJEndorphinProducer {
    public static void createNodes(LiteralArgumentBuilder<CommandSource> command,
                                   List<CategoryBuilder> categories,
                                   List<OptionBuilder<?>> options,
                                   BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc,
                                   Runnable saveFunc) {
        options.forEach(optionBuilder -> {
            if (!optionBuilder.hasPrintFunc())
                optionBuilder.printFunc(printFunc);
            if (!optionBuilder.hasSaveFunc())
                optionBuilder.saveFunc(saveFunc);
            command.then(optionBuilder.build());
        });
        categories.forEach(categoryBuilder -> {
            if (!categoryBuilder.hasPrintFunc())
                categoryBuilder.printFunc(printFunc);
            if (!categoryBuilder.hasSaveFunc())
                categoryBuilder.saveFunc(saveFunc);
            command.then(categoryBuilder.build());
        });
    }
}
