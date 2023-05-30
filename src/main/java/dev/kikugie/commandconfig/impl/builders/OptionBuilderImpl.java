package dev.kikugie.commandconfig.impl.builders;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import dev.kikugie.commandconfig.api.option.access.OptionValueAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@ApiStatus.Internal
public abstract class OptionBuilderImpl<T, S extends CommandSource> extends CommandNodeImpl<S> implements OptionBuilder<T, S> {
    protected final String name;
    protected final List<ArgumentBuilder<S, ?>> extraNodes = new ArrayList<>();
    protected OptionValueAccess<T> valueAccess;

    public OptionBuilderImpl(String name, Class<S> type) {
        super(type);
        this.name = name;

        Validate.matchesPattern(name, Reference.ALLOWED_NAMES, Reference.optionError(name, Reference.INVALID_NAME));
    }

    @Override
    public OptionBuilder<T, S> then(@NotNull ArgumentBuilder<S, ?> node) {
        Validate.notNull(node, Reference.baseError(name, Reference.NULL_NODE));

        this.extraNodes.add(node);
        return this;
    }

    @Override
    public OptionBuilder<T, S> valueAccess(@NotNull OptionValueAccess<T> access) {
        Validate.notNull(access, Reference.optionError(name, Reference.NULL_VALUE_ACCESS));

        this.valueAccess = access.name(name);
        return this;
    }

    @Override
    public OptionBuilder<T, S> valueAccess(@NotNull Supplier<Text> getter, @NotNull Function<T, Text> setter) {
        this.valueAccess = new OptionValueAccess<>(name, getter, setter);
        return this;
    }

    @Override
    public OptionBuilder<T, S> listener(@NotNull BiConsumer<T, String> listener) {
        Validate.notNull(listener, Reference.optionError(name, Reference.NULL_LISTENER));
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NO_VALUE_LISTENER));

        this.valueAccess.addListener(listener);
        return this;
    }

    @Override
    public OptionBuilder<T, S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        super.printFunc(printFunc);
        return this;
    }

    @Override
    public OptionBuilder<T, S> saveFunc(@NotNull Runnable saveFunc) {
        super.saveFunc(saveFunc);
        return this;
    }

    @Override
    public OptionBuilder<T, S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        super.helpFunc(helpFunc);
        return this;
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NO_PRINT_FUNC));
        if (helpFunc == null)
            return null;

        LiteralArgumentBuilder<S> option = literal(name);
        return option.executes(this::help);
    }
}
