package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandConfigBuilderImpl<S extends CommandSource> implements CommandConfigBuilder<S> {
    private final List<CategoryBuilder<S>> categories = new ArrayList<>();
    private final List<OptionBuilder<?, S>> options = new ArrayList<>();
    private final String baseCommand;
    private Supplier<Text> help;
    private BiFunction<CommandContext<S>, Text, Integer> printFunc;
    private Runnable saveFunc;

    public CommandConfigBuilderImpl(String command) {
        this.baseCommand = command;
    }

    @Override
    public CommandConfigBuilder<S> help(Supplier<Text> content) {
        this.help = content;
        return this;
    }

    @Override
    public CommandConfigBuilder<S> print(BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    @Override
    public CommandConfigBuilder<S> save(Runnable saveFunc) {
        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public CommandConfigBuilder<S> category(CategoryBuilder<S> category) {
        this.categories.add(category);
        return this;
    }

    @Override
    public CommandConfigBuilder<S> option(OptionBuilder<?, S> option) {
        this.options.add(option);
        return this;
    }

    @Override
    public LiteralArgumentBuilder<S> build() {
        LiteralArgumentBuilder<S> command = literal(baseCommand);

        options.forEach(optionBuilder -> {
            if (!optionBuilder.hasPrintFunc())
                optionBuilder.printFunc(printFunc);
            if (!optionBuilder.hasSaveFunc())
                optionBuilder.saveFunc(saveFunc);
            command.then(optionBuilder.build());
        });
        categories.forEach(categoryBuilder -> {
            if (!categoryBuilder.hasPrintFunc())
                categoryBuilder.printFunc(printFunc);
            if (!categoryBuilder.hasSaveFunc())
                categoryBuilder.saveFunc(saveFunc);
            command.then(categoryBuilder.build());
        });

        if (help != null)
            command.executes(context -> printFunc.apply(context, help.get()));

        return command;
    }
}
