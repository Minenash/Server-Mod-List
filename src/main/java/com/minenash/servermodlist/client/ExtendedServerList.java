package com.minenash.servermodlist.client;

import com.minenash.servermodlist.client.config.ConfigManager;
import com.minenash.servermodlist.client.enums.ServerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ExtendedServerList {

    private static final Map<String, ClientServerData> serverMods = new HashMap<>();
    private static final Map<String, ServerType> serverTypes = new HashMap<>();

    public static ClientServerData getData(String server) {
        return serverMods.get(server);
    }
    public static void setData(String server, ClientServerData mods) {
        serverMods.put(server, mods);
    }

    public static ServerType getServerType(String server) {
        if (ConfigManager.serverTypeOverrides.get(server) != null)
            return ConfigManager.serverTypeOverrides.get(server).asServerType;
        return serverTypes.get(server) == null ? ServerType.NO_INFO : serverTypes.get(server);
    }
    public static void setServerType(String server, ServerType type) { serverTypes.put(server, type); }

    public static void clear() {
        serverMods.clear();
        serverTypes.clear();
    }

}
