package dev.kikugie.commandconfig.impl.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.CommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@ApiStatus.Internal
@SuppressWarnings("unused")
public abstract class CommandNodeImpl<S extends CommandSource> implements CommandNode<S> {
    protected final String name;
    protected final Class<S> type;
    protected BiFunction<CommandContext<S>, Text, Integer> printFunc;
    @Nullable
    protected Runnable saveFunc;

    @Nullable
    protected Supplier<Text> helpFunc;

    protected CommandNodeImpl(String name, Class<S> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public int print(CommandContext<S> context, Text text) {
        Validate.notNull(printFunc, Reference.nodeError(name, Reference.NO_PRINT_FUNC));

        return printFunc.apply(context, text);
    }

    @Override
    public int help(CommandContext<S> context) {
        Validate.notNull(printFunc, Reference.nodeError(name, Reference.NO_PRINT_FUNC));

        return helpFunc != null ? printFunc.apply(context, helpFunc.get()) : -1;
    }

    @Override
    public void save() {
        if (saveFunc != null)
            saveFunc.run();
    }

    /**
     * Specifies result output function.
     *
     * @param printFunc Accepts {@link CommandContext} and {@link Text}, produces integer result
     * @return this
     */
    CommandNode<S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    /**
     * Runs every time value is set. Basically, a global listener.
     *
     * @param saveFunc Saving runnable
     * @return this
     */
    CommandNode<S> saveFunc(@NotNull Runnable saveFunc) {
        this.saveFunc = saveFunc;
        return this;
    }

    /**
     * Specifies value used for `help` subcommand.
     *
     * @param helpFunc Produces helper text
     * @return this
     */
    CommandNode<S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        this.helpFunc = helpFunc;
        return this;
    }

    public abstract LiteralArgumentBuilder<S> build();

    @Nullable
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        return null;
    }

    public void buildNodes(LiteralArgumentBuilder<S> root, Collection<? extends CommandNodeImpl<S>> nodes) {
        nodes.forEach(builder -> {
            BiFunction<CommandContext<S>, Text, Integer> printFunc = builder.printFunc;
            Runnable saveFunc = builder.saveFunc;

            if (printFunc == null && this.printFunc != null)
                builder.printFunc(this.printFunc);
            if (saveFunc == null && this.saveFunc != null)
                builder.saveFunc(this.saveFunc);

            root.then(builder.build());
        });
    }

    public void buildHelpers(LiteralArgumentBuilder<S> root, Collection<? extends CommandNodeImpl<S>> nodes) {
        nodes.forEach(builder -> {
            LiteralArgumentBuilder<S> node = builder.buildHelpFunc();
            if (node != null)
                root.then(node);
        });
    }
}
