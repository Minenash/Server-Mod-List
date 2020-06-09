package com.minenash.servermodlist.client;

import com.minenash.servermodlist.client.config.ConfigManager;
import com.minenash.servermodlist.client.enums.Compatibility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ClientModList {

    private static final Map<String, String> mods = new TreeMap<>();

    public static void generate() {
        FabricLoader.getInstance().getAllMods().forEach( mod -> mods.put(mod.getMetadata().getId(), mod.getMetadata().getVersion().getFriendlyString()));
    }

    public static String getVersion(String modid) { return mods.get(modid); }

    public static Compatibility checkModCompatibility(String address) {
        ClientServerData data = ExtendedServerList.getData(address);

        if (data == null)
            return Compatibility.UNKNOWN;

        if (data.isModPack()) {
            if (ConfigManager.modpack_name == null || !ConfigManager.modpack_name.equals(data.getModPack().name))
                return Compatibility.FALSE;
            if (ConfigManager.modpack_version == null || data.getModPack().version.equals("No Version Provided"))
                return Compatibility.UNKNOWN;
            return ClientModList.checkVersion(ConfigManager.modpack_version, data.getModPack().version);
        }

        for (ModEntry entry : data.getMods()[0]) {
            Compatibility compat = isModCompatible(entry);
            if (compat != Compatibility.TRUE)
                return compat;
        }
        return Compatibility.TRUE;
    }

    private static final Pattern VERSION_PATTERN = Pattern.compile("v?(\\d+)\\.(\\d+)([^0-9]+.*)?");

    public static Compatibility isModCompatible(ModEntry mod) {

        String clientVersion = ClientModList.mods.get(mod.getModid());

        if (clientVersion == null)
            return Compatibility.FALSE;

        return checkVersion(clientVersion, mod.getVersion());

    }

    public static Compatibility checkVersion(String client, String server) {

        Matcher matcherS = VERSION_PATTERN.matcher(server);
        Matcher matcherC = VERSION_PATTERN.matcher(client);

        if (!matcherS.matches() || !matcherC.matches())
            return Compatibility.UNKNOWN;

        if (!matcherC.group(1).equals(matcherS.group(1)))
            return Compatibility.UNKNOWN;
        if (!matcherC.group(2).equals(matcherS.group(2)))
            return Compatibility.UNKNOWN;

        return Compatibility.TRUE;
    }
}
