package dev.kikugie.commandconfig.impl;

import dev.kikugie.commandconfig.Reference;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandConfigMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);

    @Override
    public void onInitialize() {
        TestConfig.create();
    }
}
