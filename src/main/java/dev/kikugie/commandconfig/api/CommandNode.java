package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Basic functionality for command nodes.
 *
 * @param <S> CommandSource type
 */
public interface CommandNode<S extends CommandSource> {
    int print(CommandContext<S> context, Text text);
    int help(CommandContext<S> context);
    void save();
    @NotNull
    LiteralArgumentBuilder<S> build();
}
