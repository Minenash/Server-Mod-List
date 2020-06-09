package com.minenash.servermodlist.mixin.client;

import com.google.gson.*;
import com.minenash.servermodlist.client.ClientServerData;
import com.minenash.servermodlist.client.interfaces.ExtendedServerMetadata;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Environment(EnvType.CLIENT)
@Mixin(ServerMetadata.class)
public class ServerMetadataMixin implements ExtendedServerMetadata {

    @Unique
    private ClientServerData mods;
    public ClientServerData getData() {
        return mods;
    }
    public void setData(ClientServerData mods) {
        this.mods = mods;
    }

    @Environment(EnvType.CLIENT)
    @Mixin(ServerMetadata.Deserializer.class)
    static public class ServerMetadataDeserializerMixin {

        @Inject(method = "deserialize", at = @At("RETURN"))
        public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ServerMetadata> info) throws JsonParseException {
            JsonObject status = JsonHelper.asObject(jsonElement, "status");

            JsonObject obj = status.getAsJsonObject("mods");
            if (obj != null) {
                ((ExtendedServerMetadata) info.getReturnValue()).setData( ClientServerData.mods(obj) );
                return;
            }

            obj = status.getAsJsonObject("modpack");

            if (obj != null)
                ((ExtendedServerMetadata) info.getReturnValue()).setData(ClientServerData.modpack(obj));
        }

    }
}
