package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CategoryBuilderImpl<S extends CommandSource> implements CategoryBuilder<S> {
    private final String name;
    private final List<CategoryBuilder<S>> categories = new ArrayList<>();
    private final List<OptionBuilder<?, S>> options = new ArrayList<>();
    private BiFunction<CommandContext<S>, Text, Integer> printFunc;
    private Runnable saveFunc;
    private Supplier<Text> description;

    public CategoryBuilderImpl(String name) {
        this.name = name;
    }

    @Override
    public CategoryBuilder<S> printFunc(BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    @Override
    public CategoryBuilder<S> saveFunc(Runnable saveFunc) {
        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public CategoryBuilder<S> description(Supplier<Text> text) {
        this.description = text;
        return this;
    }

    @Override
    public CategoryBuilder<S> category(CategoryBuilder<S> category) {
        this.categories.add(category);
        return this;
    }

    @Override
    public CategoryBuilder<S> option(OptionBuilder<?, S> option) {
        this.options.add(option);
        return this;
    }

    @Override
    public boolean hasPrintFunc() {
        return printFunc != null;
    }

    @Override
    public boolean hasSaveFunc() {
        return saveFunc != null;
    }

    @Override
    public LiteralArgumentBuilder<S> build() {
        LiteralArgumentBuilder<S> command = literal(name);

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

        if (description != null)
            command.executes(context -> printFunc.apply(context, description.get()));

        return command;
    }
}
