package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Basic functionality for command nodes.
 *
 * @param <S> CommandSource type
 */
public interface CommandNode<S extends CommandSource> {

    @NotNull
    LiteralArgumentBuilder<S> build();
}
