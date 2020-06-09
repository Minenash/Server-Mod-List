package com.minenash.servermodlist.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.regex.Pattern;

@Environment(EnvType.SERVER)
public class ConfigManager {

    private static final File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "servermodlist.json");
    private static final Pattern FABRIC_PATTERN = Pattern.compile("^fabric-.*(-v\\d+)$");
    private static final Logger LOGGER = LogManager.getLogger("ServerModList");

    private static final String ERR_COLOR = "\u001B[31m";
    private static final String MODNAME = "\033[0;33mServer Mod List\033[0m";

    public static void load() {
        try {
            if (!configFile.exists())
                createConfig();
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                if (!ServerData.generate(new Gson().fromJson(reader, JsonObject.class)))
                    LOGGER.error(MODNAME + ERR_COLOR + ": Invalid Config. Using default config, your file has NOT been overwritten");
                else
                    LOGGER.info(MODNAME + ": Loaded config.");
            }
        }
        catch (FileNotFoundException e) {
            LOGGER.error(MODNAME + ERR_COLOR + ": Couldn't load config; reverting to defaults");
            e.printStackTrace();
            ServerData.generate(null);
        }
    }

    public static void createConfig() {
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(generateConfig());

        LOGGER.info(MODNAME + ": Creating default config.");

        try (FileWriter fileWriter = new FileWriter(configFile)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JsonObject generateConfig() {

        JsonArray required = new JsonArray();
        JsonArray recommended = new JsonArray();
        JsonArray supports = new JsonArray();
        JsonArray hidden = new JsonArray();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata meta = mod.getMetadata();
            String id = meta.getId();

            if (id.equals("minecraft") || id.equals("fabricloader") || id.equals("fabric-api-base") || FABRIC_PATTERN.matcher(id).matches())
                continue;

            if (id.equals("fabric"))
                hidden.add(id);
            else if (id.equals("servermodlist"))
                supports.add(id);
            else if ( (meta.containsCustomValue("modmenu:api") && meta.getCustomValue("modmenu:api").getAsBoolean())
                    || (meta.containsCustomValue("modmenu:parent") && meta.getCustomValue("modmenu:parent").getAsBoolean()))
                hidden.add(id);
            else
                required.add(id);
        }

        JsonObject config = new JsonObject();
        config.addProperty("auto", true);

        JsonObject mods = new JsonObject();
        mods.add("required", required);
        mods.add("recommended", recommended);
        mods.add("hidden", hidden);
        mods.add("supports", supports);

        config.add("mods", mods);
        return config;
    }

}
