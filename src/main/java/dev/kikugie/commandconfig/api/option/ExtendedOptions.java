package dev.kikugie.commandconfig.api.option;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.kikugie.commandconfig.api.builders.ListOptionBuilder;
import dev.kikugie.commandconfig.impl.command.ListArgumentType;
import dev.kikugie.commandconfig.impl.config.option.ListOptionBuilderImpl;
import net.minecraft.command.CommandSource;

import java.util.List;

public class ExtendedOptions<T, S extends CommandSource> {
    static <L extends List<T>, S extends CommandSource, T> ListOptionBuilder<L, T, S> genericList(String name, ArgumentType<T> argumentType, Class<T> valueType, Class<S> type) {
        return new ListOptionBuilderImpl<>(name, ListArgumentType.list(argumentType), argumentType, valueType, type);
    }
}
