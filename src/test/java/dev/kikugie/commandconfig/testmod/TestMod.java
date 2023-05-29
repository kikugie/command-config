package dev.kikugie.commandconfig.testmod;

import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.option.SimpleOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("testmod");
    private static void createConfig() {
        var config = CommandConfigBuilder.create("test-config", FabricClientCommandSource.class)
                .option((source) -> {
                    var option = SimpleOptions.integer("test-int", source);
                    option.valueAccess(() -> Text.of("Getting int value"),
                            (val) -> Text.of("Setting int value"));
                    option.helpFunc(() -> Text.of("`test-int` helper"));
                    return option;
                })
                .category((source) -> {
                    var category = CategoryBuilder.create("test-category", source);
                    category.option((optionSource) -> {
                        var option = SimpleOptions.bool("test-bool", optionSource);
                        option.valueAccess(() -> Text.of("Getting bool value"),
                                (val) -> Text.of("Setting bool value"));
                        return option;
                    });
                    category.helpFunc(() -> Text.of("`test-category` helper"));
                    return category;
                })
                .printFunc((context, text) -> {
                    context.getSource().sendFeedback(text);
                    return 1;
                })
                .helpFunc(() -> Text.of("`test-config` helper"))
                .saveFunc(() -> {
                })
                .build();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(config));
    }

    @Override
    public void onInitialize() {
        createConfig();
    }
}
