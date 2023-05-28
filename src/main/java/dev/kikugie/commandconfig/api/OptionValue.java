package dev.kikugie.commandconfig.api;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface OptionValue<T> {
    Text set(@NotNull T val);

    Text get();

    void addListener(@NotNull Consumer<T> listener);
}
