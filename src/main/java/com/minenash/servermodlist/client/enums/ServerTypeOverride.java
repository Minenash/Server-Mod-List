package com.minenash.servermodlist.client.enums;

public enum ServerTypeOverride {
    NA("N/A",null), VANILLA("Vanilla", ServerType.VANILLA), FABRIC("Fabric", ServerType.FABRIC);
    public String asString;
    public ServerType asServerType;
    ServerTypeOverride(String str, ServerType type) {asString = str; asServerType = type;}
}
