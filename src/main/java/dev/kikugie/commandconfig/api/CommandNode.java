package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface CommandNode<S extends CommandSource> {
    CommandNode<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc);

    CommandNode<S> saveFunc(@NotNull Runnable saveFunc);

    CommandNode<S> helpFunc(@NotNull Supplier<Text> helpFunc);

    @NotNull
    LiteralArgumentBuilder<S> build();
}
