package dev.kikugie.commandconfig.impl.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import dev.kikugie.commandconfig.api.option.OptionValueAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@ApiStatus.Internal
public abstract class OptionBuilderImpl<T, S extends CommandSource> extends CommandNodeImpl<S> implements OptionBuilder<T, S> {
    protected final String name;
    protected OptionValueAccess<T> valueAccess;

    public OptionBuilderImpl(String name, Class<S> type) {
        super(type);
        this.name = name;

        Validate.matchesPattern(name, Reference.ALLOWED_NAMES, Reference.optionError(name, Reference.INVALID_NAME));
    }

    @Override
    public OptionBuilderImpl<T, S> printFunc(@NotNull BiFunction<CommandContext<S>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> saveFunc(@NotNull Runnable saveFunc) {
        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> helpFunc(@NotNull Supplier<Text> helpFunc) {
        this.helpFunc = helpFunc;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> valueAccess(@NotNull OptionValueAccess<T> access) {
        this.valueAccess = access;
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> valueAccess(@NotNull Supplier<Text> getter, @NotNull Function<T, Text> setter) {
        this.valueAccess = new OptionValueAccess<>(name, getter, setter);
        return this;
    }

    @Override
    public OptionBuilderImpl<T, S> listener(@NotNull BiConsumer<T, String> listener) {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NULL_VALUE_LISTENER));

        this.valueAccess.addListener(listener);
        return this;
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<S> buildHelpFunc() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NO_PRINT_FUNC));
        if (helpFunc == null)
            return null;

        LiteralArgumentBuilder<S> option = literal(name);
        return option.executes(context ->
                printFunc.apply(context, helpFunc.get()));
    }

    public void save() {
        if (saveFunc != null)
            saveFunc.run();
    }
}
