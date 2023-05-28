package dev.kikugie.commandconfig.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.kikugie.commandconfig.api.CategoryBuilder;
import dev.kikugie.commandconfig.api.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class TestConfig {
    public static void create() {
        var config = CommandConfigBuilder.<FabricClientCommandSource>create("testcmd")
                .category(CategoryBuilder.<FabricClientCommandSource>create("test")
                        .option(OptionBuilder.<Integer, FabricClientCommandSource>generic(
                                        "example",
                                        IntegerArgumentType::integer,
                                        Integer.class)
                                .value(
                                        () -> Text.of("Nothing!"),
                                        (val) -> Text.of("Tried setting to " + val)
                                )
                        )
                )
                .print((source, text) -> {
                    source.getSource().sendFeedback(text);
                    return 1;
                })
                .save(() -> {})
                .help(() -> Text.of("amazing!"))
                .build();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(config));
    }
}
