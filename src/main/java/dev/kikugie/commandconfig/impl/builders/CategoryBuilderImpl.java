package dev.kikugie.commandconfig.impl.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@ApiStatus.Internal
public class CategoryBuilderImpl<S extends CommandSource> extends CommandNodeImpl<S> implements CategoryBuilder<S> {
    private final String name;
    private final List<CategoryBuilderImpl<S>> categories = new ArrayList<>();
    private final List<OptionBuilderImpl<?, S>> options = new ArrayList<>();

    public CategoryBuilderImpl(String name, Class<S> type) {
        super(type);
        this.name = name;

        Validate.matchesPattern(name, Reference.ALLOWED_NAMES, Reference.categoryError(name, Reference.INVALID_NAME));
    }

    @Override
    public CategoryBuilder<S> category(@NotNull Function<Class<S>, CategoryBuilder<S>> category) {
        Validate.notNull(category, Reference.categoryError(name, Reference.NULL_CATEGORY));

        this.categories.add((CategoryBuilderImpl<S>) category.apply(type));
        return this;
    }

    @Override
    public CategoryBuilder<S> option(@NotNull Function<Class<S>, OptionBuilder<?, S>> option) {
        Validate.notNull(option, Reference.categoryError(name, Reference.NULL_OPTION));

        this.options.add((OptionBuilderImpl<?, S>) option.apply(type));
        return this;
    }

    @Override
    public CategoryBuilder<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    @Override
    public CategoryBuilder<S> saveFunc(@NotNull Runnable saveFunc) {
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
        Validate.notNull(printFunc, Reference.categoryError(name, Reference.NO_PRINT_FUNC));

        LiteralArgumentBuilder<S> category = literal(name);
        buildHelpers(category, options);
        buildHelpers(category, categories);

        category.executes(context ->
                printFunc.apply(context, helpFunc != null ? helpFunc.get() : Reference.NO_HELP_SAD.get()));


        return category.getArguments().isEmpty() && helpFunc == null ? null : category;
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
