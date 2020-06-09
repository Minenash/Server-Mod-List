package com.minenash.servermodlist.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ConfirmChatLinkScreen.class)
public class ConfirmChatLinkScreenMixin {

    @Shadow @Final private String link;

    @Inject(method = "copyToClipboard", at = @At("TAIL"))
    public void copyToClipboard(CallbackInfo _c) {
        MinecraftClient.getInstance().keyboard.setClipboard(this.link.replaceAll("§.", ""));
    }
}
