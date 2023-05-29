package dev.kikugie.commandconfig.api.option;

import com.mojang.datafixers.util.Pair;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ListElementAccess<T> {
    private final List<BiConsumer<T, String>> listeners = new ArrayList<>();
    private final BiFunction<Integer, T, Text> elementSetter;
    private final Function<Integer, Text> elementGetter;
    private final Function<T, Text> elementAppender;
    private final Function<Integer, Pair<T, Text>> elementRemover;
    private final String name;

    public ListElementAccess(@NotNull String name,
                             @NotNull Function<Integer, Text> getter,
                             @NotNull BiFunction<Integer, T, Text> setter,
                             @NotNull Function<T, Text> appender,
                             @NotNull Function<Integer, Pair<@Nullable T, Text>> remover
    ) {
        this.elementGetter = getter;
        this.elementSetter = setter;
        this.elementAppender = appender;
        this.elementRemover = remover;
        this.name = name;
    }

    public Text set(int index, @NotNull T val) {
        Text result = elementSetter.apply(index, val);
        listeners.forEach(it -> it.accept(val, name));
        return result;
    }

    public Text get(int index) {
        return elementGetter.apply(index);
    }

    public Text append(@NotNull T val) {
        Text result = elementAppender.apply(val);
        listeners.forEach(it -> it.accept(val, name));
        return result;
    }

    public Text remove(int index) {
        Pair<T, Text> result = elementRemover.apply(index);
        listeners.forEach(it -> it.accept(result.getFirst(), name));
        return result.getSecond();
    }

    public void addListener(@NotNull BiConsumer<T, String> listener) {
        listeners.add(listener);
    }
}
