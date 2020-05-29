//package com.minenash.servermodlist.mixin;
//
//import com.google.gson.*;
//import com.minenash.servermodlist.add_methods.GetMods;
//import net.minecraft.server.ServerMetadata;
//import net.minecraft.util.JsonHelper;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.lang.reflect.Type;
//
//@Mixin(ServerMetadata.class)
//public class ServerMetadataMixin implements GetMods {
//
//    private JsonArray mods;
//
//    public JsonArray getMods() {
//        return mods;
//    }
//
//    public void setMods(JsonArray mods) {
//        this.mods = mods;
//    }
//
//    @Mixin(ServerMetadata.Deserializer.class)
//    static  public class ServerMetadataDeserializerMixin {
//
//        @Inject(method = "deserialize", at = @At("RETURN"))
//        public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ServerMetadata> info) throws JsonParseException {
//            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "status");
//            if (jsonObject.has("mods"))
//                ((GetMods) info.getReturnValue()).setMods(jsonObject.getAsJsonArray("mods"));
//        }
//        @Inject(method = "serialize", at = @At("RETURN"))
//        public void serialize(ServerMetadata serverMetadata, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> info) throws JsonParseException {
//            GetMods smm = (GetMods) serverMetadata;
//            if (smm.getMods() != null)
//                ((JsonObject) info.getReturnValue()).add("mods", smm.getMods());
//        }
//
//    }
//
//}
