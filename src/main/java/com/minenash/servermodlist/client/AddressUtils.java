package com.minenash.servermodlist.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.ServerAddress;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class AddressUtils {

    public static final Pattern ADDRESS_PATTERN = Pattern.compile(".*\\..*\\.?\\/.*");
    public static Map<String, String> infoAddressCache = new HashMap<>();

    public static String getAddress(ServerInfo server)  {
        return getAddress(server.address);
    }

    public static String getAddress(String server)  {
        if (infoAddressCache.containsKey(server))
            return infoAddressCache.get(server);

        ServerAddress serverAddress = ServerAddress.parse(server);
        String address;
        try { address = new InetSocketAddress(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort()).toString(); }
        catch (UnknownHostException e) { return null; }

        if (ADDRESS_PATTERN.matcher(address).matches())
            address = address.substring(0,address.indexOf('/')) + address.substring(address.indexOf(':'));
        else
            address = address.substring(address.indexOf('/') +1);

        infoAddressCache.put(server, address);
        return address;

    }

    public static String getAddress(ClientConnection client) {
        String address = client.getAddress().toString();

        if (AddressUtils.ADDRESS_PATTERN.matcher(address).matches())
            return address.substring(0,address.indexOf('/')) + address.substring(address.indexOf(':'));
        else
            return address.substring(address.indexOf('/') +1);
    }

}
