package dev.kikugie.commandconfig.impl.option;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.builders.ListOptionBuilder;
import dev.kikugie.commandconfig.api.option.access.ListElementAccess;
import dev.kikugie.commandconfig.impl.builders.OptionBuilderImpl;
import dev.kikugie.commandconfig.impl.command.ListArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@SuppressWarnings("unchecked")
@ApiStatus.Internal
public class ListOptionBuilderImpl<L extends List<T>, T, S extends CommandSource, U extends ArgumentType<T>> extends OptionBuilderImpl<L, S> implements ListOptionBuilder<L, T, S> {
    private final ListArgumentType<T, U> listArgumentType;
    private final U argumentType;
    private final Class<T> valueType;
    private final String typeName;
    private ListElementAccess<T, S> elementAccess;

    public ListOptionBuilderImpl(String name,
                                 ListArgumentType<T, U> listArgumentType,
                                 U argumentType,
                                 Class<T> valueType,
                                 Class<S> type) {
        super(name, type);
        this.listArgumentType = listArgumentType;
        this.argumentType = argumentType;
        this.valueType = valueType;
        this.typeName = valueType.getSimpleName();
    }

    @Override
    public ListOptionBuilder<L, T, S> elementAccess(@NotNull ListElementAccess<T, S> access) {
        Validate.notNull(access, Reference.optionError(name, Reference.NULL_ELEMENT_ACCESS));

        this.elementAccess = access.name(name);
        return this;
    }

    @Override
    public ListOptionBuilder<L, T, S> elementAccess(@NotNull BiFunction<CommandContext<S>, Integer, Text> getter,
                                                    @NotNull TriFunction<CommandContext<S>, Integer, T, Text> setter,
                                                    @NotNull BiFunction<CommandContext<S>, T, Text> appender,
                                                    @NotNull BiFunction<CommandContext<S>, Integer, Pair<@Nullable T, Text>> remover) {
        this.elementAccess = new ListElementAccess<>(name, getter, setter, appender, remover);
        return this;
    }

    @Override
    public ListOptionBuilder<L, T, S> elementAccess(@NotNull Function<Integer, Text> getter,
                                                    @NotNull BiFunction<Integer, T, Text> setter,
                                                    @NotNull Function<T, Text> appender,
                                                    @NotNull Function<Integer, Pair<@Nullable T, Text>> remover) {
        this.elementAccess = new ListElementAccess<>(name,
                (context, index) -> getter.apply(index),
                (context, index, val) -> setter.apply(index, val),
                (context, val) -> appender.apply(val),
                (context, index) -> remover.apply(index));
        return this;
    }

    @Override
    public ListOptionBuilder<L, T, S> elementListener(@NotNull BiConsumer<String, T> listener) {
        Validate.notNull(listener, Reference.optionError(name, Reference.NULL_LISTENER));
        Validate.notNull(elementAccess, Reference.optionError(name, Reference.NO_ELEMENT_LISTENER));

        this.elementAccess.addListener(listener);
        return this;
    }

    @NotNull
    public LiteralArgumentBuilder<S> build() {
        Validate.notNull(printFunc, Reference.optionError(name, Reference.NO_PRINT_FUNC));
        if (extraNodes.isEmpty())
            Validate.notNull(valueAccess, Reference.optionError(name, Reference.NO_VALUE_ACCESS));

        LiteralArgumentBuilder<S> option = literal(name);
        extraNodes.forEach(it -> it.accept(option));
        if (valueAccess == null)
            return option;

        option.then(list());
        if (elementAccess != null)
            option.then(append()).then(put()).then(get()).then(remove());

        return option;
    }

    private LiteralArgumentBuilder<S> list() {
        return (LiteralArgumentBuilder<S>) (Object) literal("list")
                .executes(context -> printFunc.apply((CommandContext<S>) (Object) context,
                        valueAccess.get((CommandContext<S>) (Object) context)))
                .then(argument(typeName + "...", listArgumentType)
                        .executes(context -> {
                            L val = (L) ListArgumentType.getList(context, typeName + "...");
                            int res = print((CommandContext<S>) (Object) context,
                                    valueAccess.set((CommandContext<S>) (Object) context, val));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> append() {
        return (LiteralArgumentBuilder<S>) (Object) literal("add")
                .then(argument(typeName, argumentType)
                        .executes(context -> {
                            T val = context.getArgument(typeName, valueType);
                            int res = print((CommandContext<S>) (Object) context,
                                    elementAccess.append((CommandContext<S>) (Object) context, val));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> get() {
        return (LiteralArgumentBuilder<S>) (Object) literal("get")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            int index = context.getArgument("index", Integer.class);
                            int res = print((CommandContext<S>) (Object) context,
                                    elementAccess.get((CommandContext<S>) (Object) context, index));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> remove() {
        return (LiteralArgumentBuilder<S>) (Object) literal("remove")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            int index = context.getArgument("index", Integer.class);
                            int res = print((CommandContext<S>) (Object) context,
                                    elementAccess.remove((CommandContext<S>) (Object) context, index));

                            save();
                            return res;
                        }));
    }

    private LiteralArgumentBuilder<S> put() {
        return (LiteralArgumentBuilder<S>) (Object) literal("set")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .then(argument(typeName, argumentType)
                                .executes(context -> {
                                    T val = context.getArgument(typeName, valueType);
                                    int index = context.getArgument("index", Integer.class);
                                    int res = print((CommandContext<S>) (Object) context,
                                            elementAccess.set((CommandContext<S>) (Object) context, index, val));

                                    save();
                                    return res;
                                })));
    }
}
