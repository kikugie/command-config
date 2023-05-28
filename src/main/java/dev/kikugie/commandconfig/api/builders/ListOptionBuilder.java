package dev.kikugie.commandconfig.api.builders;

import com.mojang.datafixers.util.Pair;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ListOptionBuilder<L extends List<T>, T, S extends CommandSource> extends OptionBuilder<L, S> {
    ListOptionBuilder<L, T, S> elementAccess(@NotNull Function<Integer, Text> getter,
                                             @NotNull BiFunction<Integer, T, Text> setter,
                                             @NotNull Function<T, Text> appender,
                                             @NotNull Function<Integer, Pair<T, Text>> remover);

    ListOptionBuilder<L, T, S> elementListener(@NotNull Consumer<T> listener);
}
