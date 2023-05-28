package dev.kikugie.commandconfig.api;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public interface OptionValue<T> {
    Text set(@NotNull T val);

    Text get();
}
