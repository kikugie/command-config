package dev.kikugie.commandconfig.impl.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.kikugie.commandconfig.Reference;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Given a source argument type returns list type of it.
 * Entries must be separated with a spaces.
 * <br>
 * Taken from Client Commands by EarthComputer.
 *
 * @see <a href="https://github.com/Earthcomputer/clientcommands/blob/fabric/src/main/java/net/earthcomputer/clientcommands/command/arguments/ListArgumentType.java">Source code</a>
 * @see <a href="https://modrinth.com/mod/client-commands">Mod page</a>
 */
public class ListArgumentType<T, U extends ArgumentType<T>> implements ArgumentType<List<T>> {
    private static final SimpleCommandExceptionType TOO_FEW_ARGUMENTS_EXCEPTION = new SimpleCommandExceptionType(
            Reference.translated("commandconfig.response.error.too_few_args"));

    private final U argumentType;
    private final int min;
    private final int max;

    private ListArgumentType(U argumentType, int min, int max) {
        this.argumentType = argumentType;
        this.min = min;
        this.max = max;
    }

    public static <T, U extends ArgumentType<T>> ListArgumentType<T, U> list(U argumentType) {
        return new ListArgumentType<>(argumentType, 1, Integer.MAX_VALUE);
    }

    public static <T, U extends ArgumentType<T>> ListArgumentType<T, U> list(U argumentType, int min) {
        return new ListArgumentType<>(argumentType, min, Integer.MAX_VALUE);
    }

    public static <T, U extends ArgumentType<T>> ListArgumentType<T, U> list(U argumentType, int min, int max) {
        return new ListArgumentType<>(argumentType, min, max);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(final CommandContext<?> context, final String name) {
        return (List<T>) context.getArgument(name, List.class);
    }

    @Override
    public List<T> parse(StringReader reader) throws CommandSyntaxException {
        List<T> parsedArguments = new ArrayList<>();
        int cursor = reader.getCursor();
        int readAmount = 0;
        try {
            while (reader.canRead() && readAmount < this.max) {
                cursor = reader.getCursor();
                parsedArguments.add(this.argumentType.parse(reader));
                readAmount++;
                // read in the separator
                if (reader.canRead()) {
                    reader.expect(' ');
                }
            }
        } catch (CommandSyntaxException e) {
            if (readAmount < this.min) {
                throw e;
            }
            reader.setCursor(cursor);
        }
        if (readAmount < this.min) {
            throw TOO_FEW_ARGUMENTS_EXCEPTION.create();
        }
        return parsedArguments;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        int readAmount = 0;
        int cursor = reader.getCursor();
        try {
            while (reader.canRead() && readAmount < this.max - 1) {
                this.argumentType.parse(reader);
                readAmount++;
                // read in the separator
                if (reader.canRead()) {
                    reader.expect(' ');
                    cursor = reader.getCursor();
                }
            }
        } catch (CommandSyntaxException ignored) {
        }
        return this.argumentType.listSuggestions(context, builder.createOffset(cursor));
    }

    @Override
    public Collection<String> getExamples() {
        Collection<String> elementExamples = argumentType.getExamples();
        if (elementExamples.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> elementExamplesList;
        if (elementExamples instanceof List<String> lst) {
            elementExamplesList = lst;
        } else {
            elementExamplesList = new ArrayList<>(elementExamples);
        }

        Random rand = new Random(0);
        String[] ret = new String[3];
        for (int i = 0; i < 3; i++) {
            StringBuilder sb = new StringBuilder();
            int times = min + rand.nextInt(Math.min(min + 10, max) - min + 1);
            for (int j = 0; j < times; j++) {
                if (j != 0) {
                    sb.append(' ');
                }
                sb.append(elementExamplesList.get(rand.nextInt(elementExamples.size())));
            }
            ret[i] = sb.toString();
        }
        return Arrays.asList(ret);
    }
}
