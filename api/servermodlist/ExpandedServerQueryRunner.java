package com.minenash.servermodlist;

import com.google.gson.JsonObject;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;

public interface ExpandedServerQueryRunner {

    void run(ServerInfo info, JsonObject data);
}
