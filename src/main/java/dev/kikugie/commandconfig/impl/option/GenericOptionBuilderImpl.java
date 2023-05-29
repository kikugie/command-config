package dev.kikugie.commandconfig.impl.option;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.impl.builders.OptionBuilderImpl;
import net.minecraft.command.CommandSource;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@ApiStatus.Internal
public class GenericOptionBuilderImpl<T, S extends CommandSource> extends OptionBuilderImpl<T, S> {
    private final ArgumentType<T> argumentType;
    private final Class<T> valueType;
    private final String typeName;

    public GenericOptionBuilderImpl(String name,
                                    ArgumentType<T> argumentType,
                                    Class<T> valueType,
                                    Class<S> type) {
        super(name, type);
        this.argumentType = argumentType;
        this.valueType = valueType;
        this.typeName = valueType.getSimpleName();
    }

    @Override
    public @NotNull LiteralArgumentBuilder<S> build() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NO_PRINT_FUNC));
        Validate.notNull(valueAccess, Reference.optionError(name, Reference.NO_VALUE_ACCESS));

        LiteralArgumentBuilder<S> option = literal(name);

        // Getter node
        option.executes(context ->
                printFunc.apply(context, valueAccess.get()));

        // Setter node
        RequiredArgumentBuilder<S, T> argument = argument(typeName, argumentType);
        argument.executes(context -> {
            T newVal = context.getArgument(typeName, valueType);
            int result = printFunc.apply(context, valueAccess.set(newVal));

            save();
            return result;
        });

        return option.then(argument);
    }
}
