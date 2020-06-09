package com.minenash.servermodlist.client.enums;

import com.minenash.servermodlist.client.ClientModList;
import com.minenash.servermodlist.client.ExtendedServerList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.ServerInfo;

@Environment(EnvType.CLIENT)
public enum Compatibility {
    TRUE, FALSE, FALSE_VERSION, NO_INFO, UNKNOWN;

    public static Compatibility getCompatibility(ServerInfo server, ServerType type, String address) {
        if (type == ServerType.NO_INFO) return Compatibility.NO_INFO;

        else if (server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion()
                || server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion())
            return Compatibility.FALSE_VERSION;

        switch (type) {
            case VANILLA:
            case SPIGOT: return Compatibility.TRUE;
            case FORGE:  return Compatibility.FALSE;
            case FABRIC: return ExtendedServerList.getData(address) == null ? NO_INFO : ClientModList.checkModCompatibility(address);
            default:     return Compatibility.UNKNOWN;
        }
    }

}
