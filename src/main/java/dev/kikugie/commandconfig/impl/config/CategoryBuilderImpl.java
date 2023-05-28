package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import dev.kikugie.commandconfig.impl.config.util.IntelliJEndorphinProducer;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CategoryBuilderImpl implements CategoryBuilder {
    private final String name;
    private final List<CategoryBuilder> categories = new ArrayList<>();
    private final List<OptionBuilder<?>> options = new ArrayList<>();
    private BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc;
    private Runnable saveFunc;
    private Supplier<Text> description;

    public CategoryBuilderImpl(String name) {
        this.name = name;
    }

    @Override
    public CategoryBuilder printFunc(BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    @Override
    public CategoryBuilder saveFunc(Runnable saveFunc) {
        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public CategoryBuilder description(Supplier<Text> text) {
        this.description = text;
        return this;
    }

    @Override
    public CategoryBuilder category(CategoryBuilder category) {
        this.categories.add(category);
        return this;
    }

    @Override
    public CategoryBuilder option(OptionBuilder<?> option) {
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
    public LiteralArgumentBuilder<CommandSource> build() {
        LiteralArgumentBuilder<CommandSource> category = literal(name);

        IntelliJEndorphinProducer.createNodes(category, categories, options, printFunc, saveFunc);

        if (description != null)
            category.executes(context -> printFunc.apply(context, description.get()));

        return category;
    }
}
