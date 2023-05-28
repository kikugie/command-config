package dev.kikugie.commandconfig.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.impl.config.CommandConfigImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface CommandConfig {
    void register();
    static Builder builder(String command, CommandDispatcher<CommandSource> dispatcher) {
        return new CommandConfigImpl.BuilderImpl(command, dispatcher);
    }

    interface Builder {
        Builder help(Supplier<Text> content);

        Builder print(BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc);

        Builder save(Runnable saveFunc);

        Builder category(CategoryBuilder category);

        Builder option(OptionBuilder<?> option);

        CommandConfig build();
    }
}
