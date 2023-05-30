package dev.kikugie.commandconfig.api.option.access;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class OptionValueAccess<T> {
    protected final List<BiConsumer<T, String>> listeners = new ArrayList<>();
    private final Supplier<Text> getter;
    private final Function<T, Text> setter;
    @Nullable
    private String name;

    public OptionValueAccess(@NotNull String name,
                             @NotNull Supplier<Text> getter,
                             @NotNull Function<T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
        this.name = name;
    }

    public OptionValueAccess(@NotNull Supplier<Text> getter,
                             @NotNull Function<T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public OptionValueAccess<T> name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public Text set(@NotNull T val) {
        Text result = setter.apply(val);
        listeners.forEach(it -> it.accept(val, name));
        return result;
    }

    public Text get() {
        return getter.get();
    }

    public void addListener(@NotNull BiConsumer<T, String> listener) {
        listeners.add(listener);
    }
}
