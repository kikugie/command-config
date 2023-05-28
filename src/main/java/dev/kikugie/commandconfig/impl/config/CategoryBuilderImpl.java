package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CategoryBuilderImpl<S extends CommandSource> extends CommandNodeImpl<S> implements CategoryBuilder<S> {
    private final String name;
    private final List<CategoryBuilderImpl<S>> categories = new ArrayList<>();
    private final List<OptionBuilderImpl<?, S>> options = new ArrayList<>();

    public CategoryBuilderImpl(String name) {
        this.name = name;
    }

    @Override
    public CategoryBuilder<S> category(@NotNull Supplier<CategoryBuilder<S>> category) {
        Validate.notNull(category, Reference.categoryError(name, Reference.NULL_CATEGORY));

        this.categories.add((CategoryBuilderImpl<S>) category.get());
        return this;
    }

    @Override
    public CategoryBuilder<S> option(@NotNull Supplier<OptionBuilder<?, S>> option) {
        Validate.notNull(option, Reference.categoryError(name, Reference.NULL_OPTION));

        this.options.add((OptionBuilderImpl<?, S>) option.get());
        return this;
    }

    @Override
    public CategoryBuilder<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        Validate.notNull(printFunc, Reference.categoryError(name, Reference.MISSING_PRINT_FUNC));

        this.printFunc = printFunc;
        return this;
    }

    @Override
    public CategoryBuilder<S> saveFunc(@NotNull Runnable saveFunc) {
        Validate.notNull(printFunc, Reference.categoryError(name, Reference.MISSING_SAVE_FUNC));

        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public CategoryBuilder<S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        this.helpFunc = helpFunc;
        return this;
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        Validate.notNull(printFunc, Reference.categoryError(name, Reference.MISSING_HELP_FUNC));

        LiteralArgumentBuilder<S> category = literal(name);
        if (helpFunc != null)
            category.executes(context ->
                    printFunc.apply(context, helpFunc.get()));

        buildHelpers(category, options);
        buildHelpers(category, categories);

        return category.getArguments().isEmpty() ? null : category;
    }

    @NotNull
    @Override
    public LiteralArgumentBuilder<S> build() {
        LiteralArgumentBuilder<S> category = literal(name);
        buildNodes(category, options);
        buildNodes(category, categories);

        return category;
    }
}
