package dev.kikugie.commandconfig.impl.config.builders;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.OptionBuilder;
import dev.kikugie.commandconfig.impl.config.CommandNodeImpl;
import dev.kikugie.commandconfig.impl.config.OptionValueImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public abstract class OptionBuilderImpl<T, S extends CommandSource> extends CommandNodeImpl<S> implements OptionBuilder<T, S> {
    protected final String name;
    protected OptionValueImpl<T> value;

    public OptionBuilderImpl(String name, Class<S> type) {
        super(type);
        Validate.matchesPattern(name, Reference.ALLOWED_NAMES, Reference.optionError(name, Reference.INVALID_NAME));

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
    public OptionBuilderImpl<T, S> listener(@NotNull Consumer<T> listener) {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NULL_LISTENER));

        this.value.addListener(listener);
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

    public void save() {
        if (saveFunc != null)
            saveFunc.run();
    }
}
