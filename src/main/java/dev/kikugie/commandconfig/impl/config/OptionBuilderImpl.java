package dev.kikugie.commandconfig.impl.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public abstract class OptionBuilderImpl<T, S extends CommandSource> extends CommandNodeImpl<S> implements OptionBuilder<T, S> {
    protected final String name;
    protected final List<BiConsumer<String, T>> listeners = new ArrayList<>();
    protected OptionValueImpl<T> value;

    public OptionBuilderImpl(String name) {
        this.name = name;
    }

    @Override
    public OptionBuilderImpl<T, S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.MISSING_PRINT_FUNC));

        this.printFunc = printFunc;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> saveFunc(@NotNull Runnable saveFunc) {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.MISSING_SAVE_FUNC));

        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        this.helpFunc = helpFunc;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> value(@NotNull Supplier<Text> getter, @NotNull Function<T, Text> setter) {
        this.value = new OptionValueImpl<>(getter, setter);
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> listener(@NotNull BiConsumer<String, T> listener) {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NULL_LISTENER));

        this.listeners.add(listener);
        return this;
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.MISSING_PRINT_FUNC));

        LiteralArgumentBuilder<S> option = literal(name);
        return option.executes(context ->
                printFunc.apply(context, helpFunc.get()));
    }
}
