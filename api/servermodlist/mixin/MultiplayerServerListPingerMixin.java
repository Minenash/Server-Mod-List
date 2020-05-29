package com.minenash.servermodlist.mixin;

import com.minenash.servermodlist.ClientAPIQueryPacketListener;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Mixin(MultiplayerServerListPinger.class)
public class MultiplayerServerListPingerMixin {


    @Shadow @Final private List<ClientConnection> clientConnections;

    /**
     * @author Minenash
     */
    @Overwrite
    public void add(ServerInfo entry) throws UnknownHostException {
        entry.label = I18n.translate("multiplayer.status.pinging");
        entry.ping = -1L;
        entry.playerListSummary = null;

        ServerAddress serverAddress = ServerAddress.parse(entry.address);
        ClientConnection connection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
        this.clientConnections.add(connection);
        connection.setPacketListener(new ClientAPIQueryPacketListener(connection, entry));
    }

}
