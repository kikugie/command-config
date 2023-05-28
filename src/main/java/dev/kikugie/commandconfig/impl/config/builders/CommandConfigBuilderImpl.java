package dev.kikugie.commandconfig.impl.config.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.CommandNode;
import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import dev.kikugie.commandconfig.impl.config.CommandNodeImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandConfigBuilderImpl<S extends CommandSource> extends CommandNodeImpl<S> implements CommandConfigBuilder<S> {
    private final String baseCommand;
    private final List<CategoryBuilderImpl<S>> categories = new ArrayList<>();
    private final List<OptionBuilderImpl<?, S>> options = new ArrayList<>();

    public CommandConfigBuilderImpl(String command, Class<S> type) {
        super(type);
        Validate.matchesPattern(command, Reference.ALLOWED_NAMES, Reference.baseError(command, Reference.INVALID_NAME));

        this.baseCommand = command;
    }

    @Override
    public CommandConfigBuilder<S> category(@NotNull Function<Class<S>, CategoryBuilder<S>> category) {
        Validate.notNull(category, Reference.baseError(baseCommand, Reference.NULL_CATEGORY));

        this.categories.add((CategoryBuilderImpl<S>) category.apply(type));
        return this;
    }

    @Override
    public CommandConfigBuilder<S> option(@NotNull Function<Class<S>, OptionBuilder<?, S>> option) {
        Validate.notNull(option, Reference.baseError(baseCommand, Reference.NULL_OPTION));

        this.options.add((OptionBuilderImpl<?, S>) option.apply(type));
        return this;
    }

    @Override
    public CommandNode<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        Validate.notNull(printFunc, Reference.baseError(baseCommand, Reference.MISSING_PRINT_FUNC));

        this.printFunc = printFunc;
        return this;
    }

    @Override
    public CommandNode<S> saveFunc(@NotNull Runnable saveFunc) {
        Validate.notNull(printFunc, Reference.baseError(baseCommand, Reference.MISSING_SAVE_FUNC));

        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public CommandNode<S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        Validate.notNull(printFunc, Reference.baseError(baseCommand, Reference.MISSING_HELP_FUNC));

        this.helpFunc = helpFunc;
        return this;
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        Validate.notNull(printFunc, Reference.baseError(baseCommand, Reference.MISSING_HELP_FUNC));

        LiteralArgumentBuilder<S> command = literal("help");
        if (helpFunc != null)
            command.executes(context ->
                    printFunc.apply(context, helpFunc.get()));

        buildHelpers(command, options);
        buildHelpers(command, categories);

        return command.getArguments().isEmpty() ? null : command;
    }

    @Override
    public LiteralArgumentBuilder<S> build() {
        LiteralArgumentBuilder<S> command = literal(baseCommand);

        buildNodes(command, options);
        buildNodes(command, categories);

        LiteralArgumentBuilder<S> helpNode = buildHelpFunc();
        if (helpNode != null)
            command.then(helpNode);

        return command;
    }
}
