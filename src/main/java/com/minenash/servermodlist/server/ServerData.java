package com.minenash.servermodlist.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Environment(EnvType.SERVER)
public class ServerData {

    private static JsonObject data = new JsonObject();
    private static boolean isModPack = false;

    public static JsonObject getData() { return data;}
    public static boolean isIsModPack() { return isModPack; }


    private static final Pattern FABRIC_PATTERN = Pattern.compile("^fabric-.*(-v\\d+)$");

    public static boolean generate(JsonObject config) {
        if (config.get("auto") != null && config.get("auto").getAsBoolean())
            generateModListNoConfig();
        if (config.get("modpack") != null)
            generateModPack(config.get("modpack").getAsJsonObject());
        else if (config.get("mods") != null)
            generateModList(config.get("mods").getAsJsonObject());
        else {
            generateModListNoConfig();
            return false;
        }
        return true;
    }

    private static List<String> getList(Gson gson, JsonObject config, String listName) {
        List<String> list = gson.fromJson(config.getAsJsonArray(listName), new TypeToken<List<String>>(){}.getType());
        return list != null ? list : new ArrayList<>();
    }

    private static void generateModPack(JsonObject config) {
        JsonObject modpack = new JsonObject();
        modpack.addProperty("name", config.get("name") == null ? null : config.get("name").getAsString());
        modpack.addProperty("version", config.get("version") == null ? null : config.get("version").getAsString());
        modpack.addProperty("description", config.get("description") == null ? null : config.get("description").getAsString());
        modpack.addProperty("link", config.get("link") == null ? null : config.get("link").getAsString());
        modpack.addProperty("image", config.get("image") == null ? null : config.get("image").getAsString());
        data = modpack;
        isModPack = true;
    }

    private static void generateModListNoConfig() {

        JsonArray jrequired = new JsonArray();
        JsonArray jhidden = new JsonArray();
        JsonArray jsupports = new JsonArray();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata meta = mod.getMetadata();
            String id = meta.getId();

            if (id.equals("minecraft") || id.equals("fabricloader") || id.equals("fabric-api-base") || FABRIC_PATTERN.matcher(id).matches())
                continue;

            JsonObject entry = new JsonObject();
            entry.addProperty("modname", meta.getName());
            entry.addProperty("modid", id);
            entry.addProperty("version", meta.getVersion().getFriendlyString());
            entry.addProperty("description", meta.getDescription());

            JsonArray authors = new JsonArray();
            meta.getAuthors().forEach( (p) -> authors.add(p.getName()));
            entry.add("authors", authors);

            if (id.equals("fabric"))
                jhidden.add(entry);
            else if (id.equals("servermodlist"))
                jsupports.add(entry);
            else if ( (meta.containsCustomValue("modmenu:api") && meta.getCustomValue("modmenu:api").getAsBoolean())
              || (meta.containsCustomValue("modmenu:parent") && meta.getCustomValue("modmenu:parent").getAsBoolean()))
                jhidden.add(entry);
            else
                jrequired.add(entry);
        }


        JsonObject mods = new JsonObject();
        mods.addProperty("auto", true);
        mods.add("required", jrequired);
        mods.add("supports", jsupports);
        mods.add("hidden", jhidden);
        ServerData.data = mods;
    }

    private static void generateModList(JsonObject config) {
        Gson gson = new Gson();
        List<String> required = getList(gson,config,"required");
        List<String> recommended = getList(gson,config,"recommended");
        List<String> supports = getList(gson,config,"supports");
        List<String> secret = getList(gson,config,"secret");

        JsonArray jrequired = new JsonArray();
        JsonArray jrecommended = new JsonArray();
        JsonArray jsupports = new JsonArray();
        JsonArray jhidden = new JsonArray();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata meta = mod.getMetadata();
            String id = meta.getId();

            if (id.equals("minecraft") || id.equals("fabricloader") || id.equals("fabric-api-base") || FABRIC_PATTERN.matcher(id).matches())
                continue;

            JsonObject entry = new JsonObject();
            entry.addProperty("modname", meta.getName());
            entry.addProperty("modid", id);
            entry.addProperty("version", meta.getVersion().getFriendlyString());
            entry.addProperty("description", meta.getDescription());

            JsonArray authors = new JsonArray();
            meta.getAuthors().forEach( (p) -> authors.add(p.getName()));
            entry.add("authors", authors);

            if (id.equals("fabric"))
                jhidden.add(entry);
            else if (id.equals("servermodlist"))
                jsupports.add(entry);
            else if (required.contains(id))
                jrequired.add(entry);
            else if (recommended.contains(id))
                jrecommended.add(entry);
            else if (supports.contains(id))
                jsupports.add(entry);
            else if (!secret.contains(id))
                jhidden.add(entry);
        }

        JsonObject mods = new JsonObject();
        mods.add("required", jrequired);
        mods.add("recommended", jrecommended);
        mods.add("supports", jsupports);
        mods.add("hidden", jhidden);
        ServerData.data = mods;
    }

}
