package dev.kikugie.commandconfig.testmod;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.option.ExtendedOptions;
import dev.kikugie.commandconfig.api.option.SimpleOptions;
import dev.kikugie.commandconfig.api.util.Defaults;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

//#if MC > 11802
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//#else
//$$ import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
//$$ import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
//#endif

/* backup in case formatter yeets it
    //#if MC > 11802
    import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
    import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
    //#else
    //$$ import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
    //$$ import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
    //#endif
 */

@SuppressWarnings("unused")
public class TestModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("testmod");

    private static void createMessyConfig() {
        Config config = new Config(true, 1, 1.0F, "stringtest", "quoted string test", "greedy string test", new ArrayList<>(), Config.ExampleEnum.A);
        var configCommand = CommandConfigBuilder.create("test", FabricClientCommandSource.class)
                .category((source) -> CategoryBuilder.create("numbers", source)
                        .option((unused) -> SimpleOptions.integer("integer", source)
                                .valueAccess(() -> Text.of("Integer option is " + config.intOpt),
                                        (val) -> {
                                            config.intOpt = val;
                                            return Text.of("Integer option set to " + val);
                                        })
                                .helpFunc(() -> Text.of("This is an integer option")))
                        .option((unused) -> SimpleOptions.floatArg("float", source)
                                .valueAccess(() -> Text.of("Float option is " + config.floatOpt),
                                        (val) -> {
                                            config.floatOpt = val;
                                            return Text.of("Float option set to " + val);
                                        })
                                .helpFunc(() -> Text.of("This is a float option"))))
                .category((source) -> CategoryBuilder.create("strings", source)
                        .option((unused) -> SimpleOptions.string("word", source)
                                .valueAccess(() -> Text.of("String option is " + config.stringOpt),
                                        (val) -> {
                                            config.stringOpt = val;
                                            return Text.of("String option set to \"" + val + "\"");
                                        })
                                .helpFunc(() -> Text.of("This is a string option")))
                        .option((unused) -> SimpleOptions.quotedString("quoted", source)
                                .valueAccess(() -> Text.of("String option is " + config.quotedStringOpt),
                                        (val) -> {
                                            config.quotedStringOpt = val;
                                            return Text.of("String option set to \"" + val + "\"");
                                        })
                                .helpFunc(() -> Text.of("This is a quoted string option")))
                        .option((unused) -> SimpleOptions.quotedString("greedy", source)
                                .valueAccess(() -> Text.of("String option is " + config.greedyStringOpt),
                                        (val) -> {
                                            config.greedyStringOpt = val;
                                            return Text.of("String option set to \"" + val + "\"");
                                        })
                                .helpFunc(() -> Text.of("This is a greedy string option"))))
                .option((source) -> SimpleOptions.bool("boolean", source)
                        .valueAccess(() -> Text.of("Boolean option is " + config.boolOpt),
                                (val) -> {
                                    config.boolOpt = val;
                                    return Text.of("Boolean option set to \"" + val + "\"");
                                })
                        .helpFunc(() -> Text.of("This is a boolean option")))
                .option((source) -> ExtendedOptions.enumArg("enum", Config.ExampleEnum.class, source)
                        .valueAccess(() -> Text.of("Enum option is " + config.enumOpt),
                                (val) -> {
                                    config.enumOpt = val;
                                    return Text.of("Enum option set to \"" + val + "\"");
                                })
                        .helpFunc(() -> Text.of("This is an enum option")))
                .option((source) -> ExtendedOptions.intList("list", source)
                        .elementAccess(
                                (index) -> index < config.intListOpt.size() ? Text.of("List element is " + config.intListOpt.get(index)) : Text.of("Invalid index!"),
                                (index, val) -> {
                                    if (index < config.intListOpt.size()) {
                                        config.intListOpt.set(index, val);
                                        return Text.of("Element set");
                                    }
                                    return Text.of("Invalid index!");
                                },
                                (value) -> {
                                    config.intListOpt.add(value);
                                    return Text.of("Element appended");
                                },
                                (index) -> {
                                    if (index < config.intListOpt.size()) {
                                        int removed = config.intListOpt.get(index);
                                        config.intListOpt.remove(index);
                                        return new Pair<>(removed, Text.of("Element removed"));
                                    }
                                    return new Pair<>(null, Text.of("Invalid index!"));
                                })
                        .valueAccess(() -> Text.of("List option is " + config.intListOpt),
                                (val) -> {
                                    config.intListOpt = val;
                                    return Text.of("List option set to \"" + val + "\"");
                                })
                        .helpFunc(() -> Text.of("This is a list option")))
                .printFunc((context, text) -> {
                    context.getSource().sendFeedback(text);
                    return 1;
                })
                .helpFunc(() -> Text.of("This is a test command"))
                .saveFunc(config::save)
                .build();
        //#if MC > 11802
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(configCommand));
        //#else
        //$$ ClientCommandManager.DISPATCHER.register(configCommand);
        //#endif
    }

    private static void createDefaultedConfig() {
        Config config = new Config(true, 1, 1.0F, "stringtest", "quoted string test", "greedy string test", new ArrayList<>(), Config.ExampleEnum.A);
        var configCommand = CommandConfigBuilder.client("test")
                .category((source) -> CategoryBuilder.create("numbers", source)
                        .option((unused) -> SimpleOptions.integer("integer", source)
                                .valueAccess(Defaults.defaultValueAccess(() -> config.intOpt, (val) -> config.intOpt = val))
                                .helpFunc(() -> Text.of("This is an integer option")))
                        .option((unused) -> SimpleOptions.floatArg("float", source)
                                .valueAccess(Defaults.defaultValueAccess(() -> config.floatOpt, (val) -> config.floatOpt = val))
                                .helpFunc(() -> Text.of("This is a float option"))))
                .category((source) -> CategoryBuilder.create("strings", source)
                        .option((unused) -> SimpleOptions.string("word", source)
                                .valueAccess(Defaults.defaultValueAccess(() -> config.stringOpt, (val) -> config.stringOpt = val))
                                .helpFunc(() -> Text.of("This is a string option")))
                        .option((unused) -> SimpleOptions.quotedString("quoted", source)
                                .valueAccess(Defaults.defaultValueAccess(() -> config.quotedStringOpt, (val) -> config.quotedStringOpt = val))
                                .helpFunc(() -> Text.of("This is a quoted string option")))
                        .option((unused) -> SimpleOptions.quotedString("greedy", source)
                                .valueAccess(Defaults.defaultValueAccess(() -> config.greedyStringOpt, (val) -> config.greedyStringOpt = val))
                                .helpFunc(() -> Text.of("This is a greedy string option"))))
                .option((source) -> SimpleOptions.bool("boolean", source)
                        .valueAccess(Defaults.defaultValueAccess(() -> config.boolOpt, (val) -> config.boolOpt = val))
                        .helpFunc(() -> Text.of("This is a boolean option")))
                .option((source) -> ExtendedOptions.enumArg("enum", Config.ExampleEnum.class, source)
                        .valueAccess(Defaults.defaultValueAccess(() -> config.enumOpt, (val) -> config.enumOpt = val))
                        .helpFunc(() -> Text.of("This is an enum option")))
                .option((source) -> ExtendedOptions.intList("list", source)
                        .elementAccess(Defaults.defaultElementAccess(() -> config.intListOpt))
                        .valueAccess(Defaults.defaultValueAccess(() -> config.intListOpt, (val) -> config.intListOpt = val))
                        .helpFunc(() -> Text.of("This is a list option")))
                .helpFunc(() -> Text.of("This is a test command\nAnd this is a second line!"))
                .saveFunc(config::save)
                .build();
        //#if MC > 11802
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(configCommand));
        //#else
        //$$ ClientCommandManager.DISPATCHER.register(configCommand);
        //#endif
    }

    @Override
    public void onInitializeClient() {
        createDefaultedConfig();
    }
}
