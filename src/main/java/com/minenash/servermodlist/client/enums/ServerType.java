package com.minenash.servermodlist.client.enums;

public enum ServerType {
    VANILLA("Vanilla"), SPIGOT("Spigot/Bukkit"), FORGE("Forge"), FABRIC("Fabric"), UNKNOWN("Unknown"), NO_INFO("No Info");

    String asString;
    ServerType(String str) {
        asString = str;
    }
    public String asString() {return asString;}
}