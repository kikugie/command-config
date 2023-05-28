package dev.kikugie.commandconfig.impl;

import dev.kikugie.commandconfig.api.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.OptionBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class TestConfig {
    public static void create() {
        var config = CommandConfigBuilder.<FabricClientCommandSource>create("testcmd")
                .option(() -> {
                    var option = OptionBuilder.<FabricClientCommandSource>integer("testint");
                    option.value(() -> Text.of("Getting value"), (val) -> Text.of("Setting value: " + val));
                    option.helpFunc(() -> Text.of("Hope this helps!"));
                    return option;
                })
                .printFunc((context, text) -> {
                    context.getSource().sendFeedback(text);
                    return 1;
                })
                .saveFunc(() -> {
                })
                .build();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(config));
    }
}
