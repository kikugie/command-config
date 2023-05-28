package dev.kikugie.commandconfig.impl.config;

import dev.kikugie.commandconfig.api.OptionValue;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class OptionValueImpl<T> implements OptionValue<T> {
    private final Supplier<Text> getter;
    private final Function<T, Text> setter;

    public OptionValueImpl(Supplier<Text> getter, Function<T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Text set(@NotNull T val) {
        return setter.apply(val);
    }

    @Override
    public Text get() {
        return getter.get();
    }
}
