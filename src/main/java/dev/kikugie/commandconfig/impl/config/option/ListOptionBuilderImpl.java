package dev.kikugie.commandconfig.impl.config.option;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.ListOptionBuilder;
import dev.kikugie.commandconfig.impl.command.ListArgumentType;
import dev.kikugie.commandconfig.impl.config.builders.OptionBuilderImpl;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@SuppressWarnings("unchecked")
public class ListOptionBuilderImpl<L extends List<T>, T, S extends CommandSource, U extends ArgumentType<T>> extends OptionBuilderImpl<L, S> implements ListOptionBuilder<L, T, S> {
    private final ListArgumentType<T, U> listArgumentType;
    private final U argumentType;
    private final Class<T> valueType;
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private BiFunction<Integer, T, Text> elementSetter;
    private Function<Integer, Text> elementGetter;
    private Function<T, Text> elementAppender;
    private Function<Integer, Pair<T, Text>> elementRemover;

    public ListOptionBuilderImpl(String name, ListArgumentType<T, U> listArgumentType, U argumentType, Class<T> valueType, Class<S> type) {
        super(name, type);
        this.listArgumentType = listArgumentType;
        this.argumentType = argumentType;
        this.valueType = valueType;
    }

    @Override
    public ListOptionBuilder<L, T, S> elementAccess(@NotNull Function<Integer, Text> getter,
                                                    @NotNull BiFunction<Integer, T, Text> setter,
                                                    @NotNull Function<T, Text> appender,
                                                    @NotNull Function<Integer, Pair<T, Text>> remover) {
        this.elementGetter = getter;
        this.elementSetter = setter;
        this.elementAppender = appender;
        this.elementRemover = remover;
        return this;
    }

    @Override
    public ListOptionBuilder<L, T, S> elementListener(@NotNull Consumer<T> listener) {
        this.listeners.add(listener);
        return this;
    }

    @NotNull
    @Override
    public LiteralArgumentBuilder<S> build() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.MISSING_PRINT_FUNC));
        LiteralArgumentBuilder<S> option = literal(name);
        option.then(list()).then(add()).then(put()).then(get()).then(remove());

        return option;
    }

    private LiteralArgumentBuilder<S> list() {
        return (LiteralArgumentBuilder<S>) (Object) literal("list")
                .executes(context -> printFunc.apply((CommandContext<S>) (Object) context, value.get()))
                .then(argument("value", listArgumentType)
                        .executes(context -> {
                            L val = (L) ListArgumentType.getList(context, "value");
                            int res = printFunc.apply((CommandContext<S>) (Object) context, value.set(val));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> add() {
        return (LiteralArgumentBuilder<S>) (Object) literal("add")
                .then(argument("value", argumentType)
                        .executes(context -> {
                            T val = context.getArgument("value", valueType);
                            int res = printFunc.apply((CommandContext<S>) (Object) context, elementAppender.apply(val));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> get() {
        return (LiteralArgumentBuilder<S>) (Object) literal("get")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            int index = context.getArgument("index", Integer.class);
                            int res = printFunc.apply((CommandContext<S>) (Object) context, elementGetter.apply(index));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> remove() {
        return (LiteralArgumentBuilder<S>) (Object) literal("remove")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            int index = context.getArgument("index", Integer.class);
                            Pair<T, Text> pair = elementRemover.apply(index);
                            int res = printFunc.apply((CommandContext<S>) (Object) context, pair.getSecond());
                            listeners.forEach(it -> it.accept(pair.getFirst()));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> put() {
        return (LiteralArgumentBuilder<S>) (Object) literal("put")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .then(argument("value", argumentType)
                                .executes(context -> {
                                    T val = context.getArgument("value", valueType);
                                    int index = context.getArgument("index", Integer.class);
                                    int res = printFunc.apply((CommandContext<S>) (Object) context, elementSetter.apply(index, val));
                                    listeners.forEach(it -> it.accept(val));

                                    save();
                                    return res;
                                })));
    }
}
