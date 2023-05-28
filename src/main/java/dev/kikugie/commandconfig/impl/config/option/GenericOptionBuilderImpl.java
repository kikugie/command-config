package dev.kikugie.commandconfig.impl.config.option;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.impl.config.builders.OptionBuilderImpl;
import net.minecraft.command.CommandSource;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GenericOptionBuilderImpl<T, S extends CommandSource> extends OptionBuilderImpl<T, S> {
    private final ArgumentType<T> argumentType;
    private final Class<T> valueType;

    public GenericOptionBuilderImpl(String name, ArgumentType<T> argumentType, Class<T> valueType, Class<S> type) {
        super(name, type);
        this.argumentType = argumentType;
        this.valueType = valueType;
    }

    @Override
    public @NotNull LiteralArgumentBuilder<S> build() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.MISSING_PRINT_FUNC));
        LiteralArgumentBuilder<S> option = literal(name);

        // Getter node
        option.executes(context ->
                printFunc.apply(context, value.get()));

        // Setter node
        RequiredArgumentBuilder<S, T> argument = argument(name, argumentType);
        argument.executes(context -> {
            T newVal = context.getArgument(name, valueType);
            int result = printFunc.apply(context, value.set(newVal));

            save();
            return result;
        });

        return option.then(argument);
    }
}
