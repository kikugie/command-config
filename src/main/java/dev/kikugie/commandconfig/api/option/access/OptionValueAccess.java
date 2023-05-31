package dev.kikugie.commandconfig.api.option.access;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public class OptionValueAccess<T, S extends CommandSource> {
    protected final List<BiConsumer<String, T>> listeners = new ArrayList<>();
    private final Function<CommandContext<S>, Text> getter;
    private final BiFunction<CommandContext<S>, T, Text> setter;
    @Nullable
    private String name;

    public OptionValueAccess(@NotNull String name,
                             @NotNull Function<CommandContext<S>, Text> getter,
                             @NotNull BiFunction<CommandContext<S>, T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
        this.name = name;
    }

    public OptionValueAccess(@NotNull Function<CommandContext<S>, Text> getter,
                             @NotNull BiFunction<CommandContext<S>, T, Text> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public OptionValueAccess<T, S> name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public Text set(@NotNull CommandContext<S> context, @NotNull T val) {
        Text result = setter.apply(context, val);
        listeners.forEach(it -> it.accept(name, val));
        return result;
    }

    public Text get(@NotNull CommandContext<S> context) {
        return getter.apply(context);
    }

    public void addListener(@NotNull BiConsumer<String, T> listener) {
        listeners.add(listener);
    }
}
