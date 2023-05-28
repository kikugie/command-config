package dev.kikugie.commandconfig.impl.config.option;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.impl.config.OptionBuilderImpl;
import net.minecraft.command.CommandSource;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GenericOptionBuilder<T, S extends CommandSource> extends OptionBuilderImpl<T, S> {
    private final Supplier<ArgumentType<T>> argumentType;
    private final Class<T> type;

    public GenericOptionBuilder(String name, Supplier<ArgumentType<T>> argumentType, Class<T> type) {
        super(name);
        this.argumentType = argumentType;
        this.type = type;
    }

    @Override
    public @NotNull LiteralArgumentBuilder<S> build() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.MISSING_PRINT_FUNC));

        LiteralArgumentBuilder<S> option = literal(name);

        // Getter node
        option.executes(context ->
                printFunc.apply(context, value.get()));

        // Setter node
        RequiredArgumentBuilder<S, T> argument = argument(name, argumentType.get());
        argument.executes(context -> {
            T newVal = context.getArgument(name, type);
            int result = printFunc.apply(context, value.set(newVal));
            listeners.forEach(it -> it.accept(name, newVal));

            if (getSaveFunc() != null)
                getSaveFunc().run();
            return result;
        });

        return option.then(argument);
    }
}
