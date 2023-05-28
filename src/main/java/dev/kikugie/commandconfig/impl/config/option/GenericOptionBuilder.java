package dev.kikugie.commandconfig.impl.config.option;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kikugie.commandconfig.api.OptionBuilder;
import dev.kikugie.commandconfig.api.OptionValue;
import dev.kikugie.commandconfig.impl.config.OptionValueImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GenericOptionBuilder<T> implements OptionBuilder<T> {

    private final String name;
    private final Supplier<ArgumentType<T>> argumentType;
    private final Class<T> type;
    private OptionValue<T> value;
    private Supplier<Text> help;
    private BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc;
    private Runnable saveFunc;

    public GenericOptionBuilder(String name, Supplier<ArgumentType<T>> argumentType, Class<T> type) {
        this.name = name;
        this.argumentType = argumentType;
        this.type = type;
    }

    @Override
    public OptionBuilder<T> printFunc(BiFunction<CommandContext<CommandSource>, Text, Integer> printFunc) {
        this.printFunc = printFunc;
        return this;
    }

    @Override
    public OptionBuilder<T> saveFunc(Runnable saveFunc) {
        this.saveFunc = saveFunc;
        return this;
    }

    @Override
    public OptionBuilder<T> value(OptionValue<T> value) {
        this.value = value;
        return this;
    }

    @Override
    public OptionBuilder<T> value(Supplier<Text> getter, Function<T, Text> setter) {
        this.value = new OptionValueImpl<>(getter, setter);
        return this;
    }

    @Override
    public OptionBuilder<T> help(Supplier<Text> help) {
        this.help = help;
        return this;
    }

    @Override
    public boolean hasPrintFunc() {
        return printFunc != null;
    }

    @Override
    public boolean hasSaveFunc() {
        return saveFunc != null;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        LiteralArgumentBuilder<CommandSource> option = literal(name);

        // Getter node
        option.executes(context ->
                printFunc.apply(context, value.get()));

        // Setter node
        RequiredArgumentBuilder<CommandSource, T> argument = argument(name, argumentType.get());
        argument.executes(context -> {
            int result = printFunc.apply(context, value.set(context.getArgument(name, type)));
            saveFunc.run();
            return result;
        });

        // Helper node
        if (help != null) {
            LiteralArgumentBuilder<CommandSource> helpArg = literal("help");
            helpArg.executes(context ->
                    printFunc.apply(context, help.get()));
            argument.then(helpArg);
        }

        return option.then(argument);
    }
}
