package com.minenash.servermodlist.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ModEntry {
    private final String modname;
    private final String modid;
    private final String version;
    private final String description;
    private final List<String> authors;

    public ModEntry(String modname, String modid, String version, String description, List<String> authors) {
        this.modname = modname;
        this.modid = modid;
        this.version = version;
        this.description = description;
        this.authors = authors;
    }

    public String getModname() {return modname;}
    public String getModid  () {return modid  ;}
    public String getVersion() {return version;}
    public String getDescription() {return description;}
    public List<String> getAuthors() {return authors;}

    @Override
    public String toString() {
        return modname;
    }

}
