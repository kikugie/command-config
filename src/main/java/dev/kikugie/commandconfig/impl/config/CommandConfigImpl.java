package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.CommandConfig;
import dev.kikugie.commandconfig.api.OptionBuilder;
import dev.kikugie.commandconfig.impl.config.util.IntelliJEndorphinProducer;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandConfigImpl implements CommandConfig {
    private final LiteralArgumentBuilder<CommandSource> command;
    private final CommandDispatcher<CommandSource> dispatcher;

    public CommandConfigImpl(LiteralArgumentBuilder<CommandSource> command, CommandDispatcher<CommandSource> dispatcher) {
        this.command = command;
        this.dispatcher = dispatcher;
    }

    @Override
    public void register() {
        dispatcher.register(command);
    }

    public static class BuilderImpl implements Builder {
        private final List<CategoryBuilder> categories = new ArrayList<>();
        private final List<OptionBuilder<?>> options = new ArrayList<>();
        private final CommandDispatcher<CommandSource> dispatcher;
        private final String baseCommand;
        private Supplier<Text> help;
        private BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc;
        private Runnable saveFunc;

        public BuilderImpl(String command, CommandDispatcher<CommandSource> dispatcher) {
            this.dispatcher = dispatcher;
            this.baseCommand = command;
        }

        @Override
        public Builder help(Supplier<Text> content) {
            this.help = content;
            return this;
        }

        @Override
        public Builder print(BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc) {
            this.printFunc = printFunc;
            return this;
        }

        @Override
        public Builder save(Runnable saveFunc) {
            this.saveFunc = saveFunc;
            return this;
        }

        @Override
        public Builder category(CategoryBuilder category) {
            this.categories.add(category);
            return this;
        }

        @Override
        public Builder option(OptionBuilder<?> option) {
            this.options.add(option);
            return this;
        }

        @Override
        public CommandConfig build() {
            LiteralArgumentBuilder<CommandSource> command = literal(baseCommand);

            IntelliJEndorphinProducer.createNodes(command, categories, options, printFunc, saveFunc);

            if (help != null)
                command.executes(context -> printFunc.apply(context, help.get()));

            return new CommandConfigImpl(command, dispatcher);
        }
    }
}
