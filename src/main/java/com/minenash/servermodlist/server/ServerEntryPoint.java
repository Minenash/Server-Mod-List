package com.minenash.servermodlist.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class ServerEntryPoint implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ConfigManager.load();
    }

}
