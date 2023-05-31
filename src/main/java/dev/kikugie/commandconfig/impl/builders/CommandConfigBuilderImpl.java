package dev.kikugie.commandconfig.impl.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.CommandConfigBuilder;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@ApiStatus.Internal
public class CommandConfigBuilderImpl<S extends CommandSource> extends CommandNodeImpl<S> implements CommandConfigBuilder<S> {
    protected final List<Consumer<LiteralArgumentBuilder<S>>> extraNodes = new ArrayList<>();
    private final String baseCommand;
    private final List<CategoryBuilderImpl<S>> categories = new ArrayList<>();
    private final List<OptionBuilderImpl<?, S>> options = new ArrayList<>();

    public CommandConfigBuilderImpl(String command, Class<S> type) {
        super(type);
        this.baseCommand = command;
        Validate.matchesPattern(command, Reference.ALLOWED_NAMES, Reference.baseError(baseCommand, Reference.INVALID_NAME));
    }

    @Override
    public CommandConfigBuilder<S> node(@NotNull Consumer<LiteralArgumentBuilder<S>> node) {
        Validate.notNull(node, Reference.baseError(baseCommand, Reference.NULL_NODE));

        this.extraNodes.add(node);
        return this;
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
    public CommandConfigBuilder<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        super.printFunc(printFunc);
        return this;
    }

    @Override
    public CommandConfigBuilder<S> saveFunc(@NotNull Runnable saveFunc) {
        super.saveFunc(saveFunc);
        return this;
    }

    @Override
    public CommandConfigBuilder<S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        super.helpFunc(helpFunc);
        return this;
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        Validate.notNull(printFunc, Reference.baseError(baseCommand, Reference.NO_PRINT_FUNC));

        LiteralArgumentBuilder<S> command = literal("help");

        buildHelpers(command, options);
        buildHelpers(command, categories);

        command.executes(context -> print(context, helpFunc != null ? helpFunc.get() : Reference.NO_HELP_SAD.get()));
        return command.getArguments().isEmpty() && helpFunc == null ? null : command;
    }

    @NotNull
    @Override
    public LiteralArgumentBuilder<S> build() {
        LiteralArgumentBuilder<S> command = literal(baseCommand);
        extraNodes.forEach(it -> it.accept(command));
        buildNodes(command, options);
        buildNodes(command, categories);

        LiteralArgumentBuilder<S> helpNode = buildHelpFunc();
        if (helpNode != null)
            command.then(helpNode);

        return command;
    }
}
