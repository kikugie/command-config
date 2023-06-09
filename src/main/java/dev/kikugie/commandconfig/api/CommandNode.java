package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/**
 * Basic functionality for command nodes.
 *
 * @param <S> {@link CommandSource} type
 */
public interface CommandNode<S extends CommandSource> {
    /**
     * Runs {@code printFunc}
     */
    int print(CommandContext<S> context, Text text);

    /**
     * Runs {@code helpFunc}
     */
    int help(CommandContext<S> context);

    /**
     * Runs {@code saveFunc}
     */
    void save();
}
