package dev.kikugie.commandconfig.impl.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.kikugie.commandconfig.Reference;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.command.CommandSource;
import net.minecraft.util.StringIdentifiable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Custom implementation of `EnumArgumentType`. Created mainly for <1.19 compatibility.
 *
 * @param <T> Enum type
 */
public class CustomEnumArgumentType<T extends Enum<T> & StringIdentifiable> implements ArgumentType<T> {
    private static final DynamicCommandExceptionType INVALID_ENUM_EXCEPTION = new DynamicCommandExceptionType(
            value -> Reference.translated("commandconfig.response.error.invalid_enum", value));
    private final Object2ObjectArrayMap<String, T> values;

    public CustomEnumArgumentType(T[] values) {
        String[] keys = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            keys[i] = values[i].asString();
        }
        this.values = new Object2ObjectArrayMap<>(keys, values);
    }

    public static <T extends Enum<T> & StringIdentifiable> CustomEnumArgumentType<T> enumArg(Class<T> enumClass) {
        return new CustomEnumArgumentType<>(enumClass.getEnumConstants());
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        T result = values.get(string);
        if (result == null)
            throw INVALID_ENUM_EXCEPTION.create(string);
        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(values.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return values.keySet();
    }
}
