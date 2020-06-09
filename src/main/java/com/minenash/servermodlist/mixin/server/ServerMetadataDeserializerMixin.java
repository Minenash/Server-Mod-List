package com.minenash.servermodlist.mixin.server;

import com.google.gson.*;
import com.minenash.servermodlist.server.ServerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Environment(EnvType.SERVER)
@Mixin(ServerMetadata.Deserializer.class)
public class ServerMetadataDeserializerMixin {

    @Inject(method = "serialize", at = @At("RETURN"))
    public void serialize(ServerMetadata serverMetadata, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> info) throws JsonParseException {
        ((JsonObject) info.getReturnValue()).add(ServerData.isIsModPack() ? "modpack" : "mods", ServerData.getData());
    }

}