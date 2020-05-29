package com.minenash.servermodlist;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ClientAPIQueryPacketListener implements ClientQueryPacketListener {

    private ClientConnection connection;
    private ServerInfo entry;

    public ClientAPIQueryPacketListener(ClientConnection connection, ServerInfo entry) {
        this.connection = connection;
        this.entry = entry;
    }

    private boolean sentQuery;
    private boolean received;
    private long startTime;

    @Override
    public void onResponse(QueryResponseS2CPacket packet) {
        if (this.received) {
            connection.disconnect(new TranslatableText("multiplayer.status.unrequested"));
            return;
        }
        this.received = true;

        ServerMetadata meta = packet.getServerMetadata();

        entry.label = meta.getDescription() == null ? "" : meta.getDescription().asFormattedString();

        if (meta.getVersion() != null) {
            entry.version = meta.getVersion().getGameVersion();
            entry.protocolVersion = meta.getVersion().getProtocolVersion();
        } else {
            entry.version = I18n.translate("multiplayer.status.old");
            entry.protocolVersion = 0;
        }

        if (meta.getPlayers() == null) {
            entry.playerCountLabel = Formatting.DARK_GRAY + I18n.translate("multiplayer.status.unknown");
        }
    }

    @Override
    public void onPong(QueryPongS2CPacket packet) {

    }

    @Override
    public void onDisconnected(Text reason) {

    }

    @Override
    public ClientConnection getConnection() {
        return null;
    }


}
