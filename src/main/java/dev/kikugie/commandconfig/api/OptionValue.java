package dev.kikugie.commandconfig.api;

import net.minecraft.text.Text;

public interface OptionValue<T> {
    Text set(T val);

    Text get();
}
