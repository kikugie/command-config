package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.impl.config.CategoryBuilderImpl;
import dev.kikugie.commandconfig.impl.config.CommandConfigBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface CategoryBuilder<S extends CommandSource> {
    static <S extends CommandSource> CategoryBuilderImpl<S> create(String name) {
        return new CategoryBuilderImpl<>(name);
    }
    CategoryBuilder<S> printFunc(BiFunction<CommandContext<S>, Text, Integer> printFunc);

    CategoryBuilder<S> saveFunc(Runnable saveFunc);

    CategoryBuilder<S> description(Supplier<Text> text);

    CategoryBuilder<S> category(CategoryBuilder<S> category);

    CategoryBuilder<S> option(OptionBuilder<?, S> option);

    LiteralArgumentBuilder<S> build();

    boolean hasPrintFunc();

    boolean hasSaveFunc();
}
