package dev.kikugie.commandconfig.api.util;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.Reference;
import dev.kikugie.commandconfig.api.option.ListElementAccess;
import dev.kikugie.commandconfig.api.option.OptionValueAccess;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Defaults {
    public static <T> OptionValueAccess<T> defaultValueAccess(String name, Supplier<T> getter, Consumer<T> setter) {
        return new OptionValueAccess<>(
                name,
                () -> Reference.translated("commandconfig.response.option.get", getter.get()),
                (val) -> {
                    setter.accept(val);
                    return Reference.translated("commandconfig.response.option.set", val);
                });
    }

    public static <L extends List<T>, T> ListElementAccess<T> defaultElementAccess(String name, Supplier<L> listSupplier) {
        return new ListElementAccess<>(
                name,
                (index) -> {
                    L list = listSupplier.get();
                    return index >= 0 && index < list.size() ? Reference.translated("commandconfig.response.option.element.get", list.get(index)) : Reference.translated("commandconfig.response.error.invalid_index", index);
                },
                (index, value) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        list.set(index, value);
                        return Reference.translated("commandconfig.response.option.element.set", value, index);
                    }
                    return Reference.translated("commandconfig.response.error.invalid_index", index);
                },
                (value) -> {
                    L list = listSupplier.get();
                    list.add(value);
                    return Reference.translated("commandconfig.response.option.element.add", value, list.size());
                },
                (index) -> {
                    L list = listSupplier.get();
                    if (index >= 0 && index < list.size()) {
                        T value = list.get(index);
                        // Uses removeAll to avoid remove method ambiguity
                        list.removeAll(List.of(value));
                        return new Pair<>(value, Reference.translated("commandconfig.response.option.element.remove", value, index));
                    }
                    return new Pair<>(null, Reference.translated("commandconfig.response.error.invalid_index", index));
                });
    }
}
