package com.minenash.servermodlist.client.interfaces;

import com.minenash.servermodlist.client.ClientServerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ExtendedServerMetadata {
    ClientServerData getData();
    void setData(ClientServerData data);
}
