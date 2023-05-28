package dev.kikugie.commandconfig.impl.config;

import dev.kikugie.commandconfig.api.OptionValue;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OptionValueImpl<T> implements OptionValue<T> {
    protected final List<Consumer<T>> listeners = new ArrayList<>();
    private final Supplier<Text> getter;
    private final Function<T, Text> setter;

    public OptionValueImpl(Supplier<Text> getter, Function<T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Text set(@NotNull T val) {
        Text result = setter.apply(val);
        listeners.forEach(it -> it.accept(val));
        return result;
    }

    @Override
    public Text get() {
        return getter.get();
    }

    @Override
    public void addListener(@NotNull Consumer<T> listener) {
        listeners.add(listener);
    }
}
