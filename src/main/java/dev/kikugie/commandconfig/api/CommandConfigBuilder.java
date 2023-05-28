package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.impl.config.CommandConfigBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface CommandConfigBuilder<S extends CommandSource> {
    static <S extends CommandSource> CommandConfigBuilderImpl<S> create(String name) {
        return new CommandConfigBuilderImpl<>(name);
    }
    CommandConfigBuilder<S> help(Supplier<Text> content);

    CommandConfigBuilder<S> print(BiFunction<CommandContext<S>, Text, Integer> printFunc);

    CommandConfigBuilder<S> save(Runnable saveFunc);

    CommandConfigBuilder<S> category(CategoryBuilder<S> category);

    CommandConfigBuilder<S> option(OptionBuilder<?, S> option);

    LiteralArgumentBuilder<S> build();
}
