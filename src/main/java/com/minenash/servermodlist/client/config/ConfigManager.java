package com.minenash.servermodlist.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.minenash.servermodlist.client.ModPack;
import com.minenash.servermodlist.client.enums.AllowModpackImage;
import com.minenash.servermodlist.client.enums.ServerTypeOverride;
import com.minenash.servermodlist.client.enums.TextAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigManager {

    private static final File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "servermodlistc.json");
    private static final Logger LOGGER = LogManager.getLogger("ServerModList");

    private static final String ERR_COLOR = "\u001B[31m";
    private static final String MODNAME = "\033[0;33mServer Mod List\033[0m";

    public static boolean showCompatibilityLights = true;
    public static boolean showUnknownServerTypeIcon = true;
    public static TextAlignment modpackScreenTextAlignment = TextAlignment.CENTER;
    public static AllowModpackImage allowModpackImage = AllowModpackImage.ONCE;
    public static boolean checkFabricModVersionCompatibility = true;
    public static String modpack_name = null, modpack_version = null;

    public static Map<String, ServerTypeOverride> serverTypeOverrides = new HashMap<>();

    public static void load() {
        Gson gson = new Gson();
        try {
            if (!configFile.exists())
                saveConfig();
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                JsonObject config = new Gson().fromJson(reader, JsonObject.class);
                JsonElement compatLights = config.get("show_compatibility_lights");
                JsonElement unkSevType = config.get("show_unknown_server_type_icon");
                JsonElement mpTextAlign = config.get("modpack_screen_text_alignment");
                JsonElement checkFabVer = config.get("check_fabric_mod_version_compatibility");
                JsonElement allowImg =  config.get("allow_modpack_image");
                JsonElement overrides = config.get("server_type_overrides");
                JsonElement modpack = config.get("modpack");

                if (compatLights != null && !compatLights.getAsBoolean()) showCompatibilityLights = false;
                if (unkSevType != null && !unkSevType.getAsBoolean()) showUnknownServerTypeIcon = false;
                if (checkFabVer != null && !checkFabVer.getAsBoolean()) checkFabricModVersionCompatibility = false;

                if (mpTextAlign != null) modpackScreenTextAlignment = gson.fromJson(mpTextAlign, TextAlignment.class);
                if (modpackScreenTextAlignment == null) modpackScreenTextAlignment = TextAlignment.CENTER;

                if (allowImg != null) allowModpackImage = gson.fromJson(allowImg, AllowModpackImage.class);
                if (allowModpackImage == null) allowModpackImage = AllowModpackImage.ONCE;

                if (overrides != null)
                    serverTypeOverrides = gson.fromJson(overrides, new TypeToken<Map<String,ServerTypeOverride>>(){}.getType());
                if (serverTypeOverrides == null) serverTypeOverrides = new HashMap<>();

                if (modpack != null) {
                    JsonElement mp_name = ((JsonObject)modpack).get("name");
                    JsonElement mp_version = ((JsonObject)modpack).get("version");
                    if (mp_name != null) modpack_name = mp_name.getAsString();
                    if (mp_version != null) modpack_version = mp_version.getAsString();
                }
            }
        }
        catch (FileNotFoundException e) {
            LOGGER.error(MODNAME + ERR_COLOR + ": Couldn't load config; reverting to defaults");
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        LOGGER.info(MODNAME + ": Saving config.");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject config = new JsonObject();
        config.addProperty("show_compatibility_lights", showCompatibilityLights);
        config.addProperty("show_unknown_server_type_icon", showUnknownServerTypeIcon);
        config.addProperty("check_fabric_mod_version_compatibility", checkFabricModVersionCompatibility);
        config.add("modpack_screen_text_alignment", gson.toJsonTree(modpackScreenTextAlignment, TextAlignment.class));
        config.add("allow_modpack_image", gson.toJsonTree(allowModpackImage, AllowModpackImage.class));
        config.add("server_type_overrides", gson.toJsonTree(serverTypeOverrides, new TypeToken<Map<String,ServerTypeOverride>>(){}.getType()));

        try (FileWriter fileWriter = new FileWriter(configFile)) {
            fileWriter.write(gson.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
