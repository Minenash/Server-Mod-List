package com.minenash.servermodlist.client;

import com.minenash.servermodlist.client.ClientModList;
import com.minenash.servermodlist.client.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientModList.generate();
        ConfigManager.load();
    }
}
