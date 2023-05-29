package dev.kikugie.commandconfig.api.option;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

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
    private final String name;

    public OptionValueAccess(String name, Supplier<Text> getter, Function<T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
        this.name = name;
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
