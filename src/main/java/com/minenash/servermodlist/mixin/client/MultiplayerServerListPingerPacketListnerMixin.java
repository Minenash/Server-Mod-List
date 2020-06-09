package com.minenash.servermodlist.mixin.client;

import com.minenash.servermodlist.client.ClientServerData;
import com.minenash.servermodlist.client.interfaces.ExtendedServerMetadata;
import com.minenash.servermodlist.client.ExtendedServerList;
import com.minenash.servermodlist.client.AddressUtils;
import com.minenash.servermodlist.client.enums.ServerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/client/network/MultiplayerServerListPinger$1")
public abstract class MultiplayerServerListPingerPacketListnerMixin {

    @Shadow public abstract ClientConnection getConnection();

    @Inject(method = "method_12667", at = @At("TAIL"))
    public void onResponse(QueryResponseS2CPacket packet, CallbackInfo info) {
        String address = AddressUtils.getAddress(getConnection());
        ServerMetadata meta = packet.getServerMetadata();
        ClientServerData data = ((ExtendedServerMetadata) meta).getData();

        ExtendedServerList.setData(address, data);

        String version = meta.getVersion().getGameVersion();

        if (version.startsWith("Spigot") || version.startsWith("Requires MC") || version.startsWith("Paper"))
            ExtendedServerList.setServerType(address, ServerType.SPIGOT);
        else if (version.startsWith("Forge") || version.startsWith("HexaCord"))
            ExtendedServerList.setServerType(address, ServerType.FORGE);
        else if (version.startsWith("Fabric") || data != null )
            ExtendedServerList.setServerType(address, ServerType.FABRIC);
        else
            ExtendedServerList.setServerType(address, ServerType.UNKNOWN);

        logMods(address, data, version);

    }

    private void logMods(String address, ClientServerData data, String version) {
        Logger logger = LogManager.getLogger("ServerModList");
        logger.info("Address: " + address);
        logger.info("Data: " + (data == null ? "None" : data.isModPack()? data.getModPack() : data.getMods()));
        logger.info("Version: " + version);
        logger.info("Server Type: " + ExtendedServerList.getServerType(address));
        logger.info("");
    }

}
