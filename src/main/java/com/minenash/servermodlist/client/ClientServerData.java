package com.minenash.servermodlist.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientServerData {

    private List<ModEntry>[] mods;
    private ModPack modPack;
    private boolean isModPack;

    public static ClientServerData mods(JsonObject obj) {
        ClientServerData data = new ClientServerData();

        Gson gson = new Gson();
        data.mods = new List[4];
        data.mods[0] = getList(gson, obj, "required");
        data.mods[1] = getList(gson, obj, "recommended");
        data.mods[2] = getList(gson, obj, "supports");
        data.mods[3] = getList(gson, obj, "hidden");

        if (data.mods[0] == null) data.mods[0] = new ArrayList<>();
        if (data.mods[1] == null) data.mods[1] = new ArrayList<>();
        if (data.mods[2] == null) data.mods[2] = new ArrayList<>();
        if (data.mods[3] == null) data.mods[3] = new ArrayList<>();

        data.isModPack = false;
        return data;
    }

    public static ClientServerData modpack(JsonObject obj) {
        ClientServerData data = new ClientServerData();
        data.modPack = getModPack(obj);
        data.modPack.standardize();
        data.isModPack = true;
        return data;
    }

    public List<ModEntry>[] getMods() {
        return mods;
    }

    public ModPack getModPack() {
        return modPack;
    }

    public boolean isModPack() {
        return isModPack;
    }

    private static List<ModEntry> getList(Gson gson, JsonObject obj, String listName) {
        return gson.fromJson(obj.getAsJsonArray(listName), new TypeToken<List<ModEntry>>(){}.getType());
    }

    private static ModPack getModPack(JsonObject obj) {
        System.out.println(obj);
        return new Gson().fromJson(obj, new TypeToken<ModPack>(){}.getType());
    }
}
