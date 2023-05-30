package dev.kikugie.commandconfig.api.option.access;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class ListElementAccess<T, S extends CommandSource> {
    private final List<BiConsumer<T, String>> listeners = new ArrayList<>();
    private final TriFunction<CommandContext<S>, Integer, T, Text> elementSetter;
    private final BiFunction<CommandContext<S>, Integer, Text> elementGetter;
    private final BiFunction<CommandContext<S>, T, Text> elementAppender;
    private final BiFunction<CommandContext<S>, Integer, Pair<T, Text>> elementRemover;
    @Nullable
    private String name;

    public ListElementAccess(@NotNull String name,
                             @NotNull BiFunction<CommandContext<S>, Integer, Text> getter,
                             @NotNull TriFunction<CommandContext<S>, Integer, T, Text> setter,
                             @NotNull BiFunction<CommandContext<S>, T, Text> appender,
                             @NotNull BiFunction<CommandContext<S>, Integer, Pair<@Nullable T, Text>> remover
    ) {
        this.elementGetter = getter;
        this.elementSetter = setter;
        this.elementAppender = appender;
        this.elementRemover = remover;
        this.name = name;
    }

    public ListElementAccess(@NotNull BiFunction<CommandContext<S>, Integer, Text> getter,
                             @NotNull TriFunction<CommandContext<S>, Integer, T, Text> setter,
                             @NotNull BiFunction<CommandContext<S>, T, Text> appender,
                             @NotNull BiFunction<CommandContext<S>, Integer, Pair<@Nullable T, Text>> remover
    ) {
        this.elementGetter = getter;
        this.elementSetter = setter;
        this.elementAppender = appender;
        this.elementRemover = remover;
    }

    public ListElementAccess<T, S> name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public Text set(@NotNull CommandContext<S> context, int index, @NotNull T val) {
        Text result = elementSetter.apply(context, index, val);
        listeners.forEach(it -> it.accept(val, name));
        return result;
    }

    public Text get(@NotNull CommandContext<S> context, int index) {
        return elementGetter.apply(context, index);
    }

    public Text append(@NotNull CommandContext<S> context, @NotNull T val) {
        Text result = elementAppender.apply(context, val);
        listeners.forEach(it -> it.accept(val, name));
        return result;
    }

    public Text remove(@NotNull CommandContext<S> context, int index) {
        Pair<T, Text> result = elementRemover.apply(context, index);
        listeners.forEach(it -> it.accept(result.getFirst(), name));
        return result.getSecond();
    }

    public void addListener(@NotNull BiConsumer<T, String> listener) {
        listeners.add(listener);
    }
}
