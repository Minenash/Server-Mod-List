package com.minenash.servermodlist.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ModPack {
    public String name, version, description, link, image;

    private static final Pattern VALID_LINK = Pattern.compile("https:\\/\\/www.curseforge.com\\/minecraft\\/modpacks\\/[a-z0-9_-]+");

    public void standardize() {
        if (link != null && !VALID_LINK.matcher(link.replaceAll("§.", "")).matches())
            link = null;
        if (name == null || name.isEmpty())
            name = "No Name Provided";
        if (description == null || description.isEmpty())
            description = "No Description Provided";
        if (version == null || version.isEmpty())
            version = "No Version Provided";
    }

    public String toString() {
        return name;
    }
}
