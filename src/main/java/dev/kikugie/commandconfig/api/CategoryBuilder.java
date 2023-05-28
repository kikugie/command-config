package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface CategoryBuilder {
    CategoryBuilder printFunc(BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc);

    CategoryBuilder saveFunc(Runnable saveFunc);

    CategoryBuilder description(Supplier<Text> text);

    CategoryBuilder category(CategoryBuilder category);

    CategoryBuilder option(OptionBuilder<?> option);

    LiteralArgumentBuilder<CommandSource> build();

    boolean hasPrintFunc();

    boolean hasSaveFunc();
}
