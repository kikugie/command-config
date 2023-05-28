package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.CommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class CommandNodeImpl<S extends CommandSource> implements CommandNode<S> {
    protected BiFunction<CommandContext<S>, Text, Integer> printFunc;
    protected Runnable saveFunc;
    protected Supplier<Text> helpFunc;

    @Nullable
    public BiFunction<CommandContext<S>, Text, Integer> getPrintFunc() {
        return printFunc;
    }

    @Nullable
    public Runnable getSaveFunc() {
        return saveFunc;
    }

    @Nullable
    public Supplier<Text> getHelpFunc() {
        return helpFunc;
    }

    @Nullable
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        return null;
    }

    public void buildNodes(LiteralArgumentBuilder<S> root, Collection<? extends CommandNodeImpl<S>> nodes) {
        nodes.forEach(builder -> {
            BiFunction<CommandContext<S>, Text, Integer> printFunc = builder.getPrintFunc();
            Runnable saveFunc = builder.getSaveFunc();

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
