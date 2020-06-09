package com.minenash.servermodlist.mixin.client;

import com.minenash.servermodlist.client.*;
import com.minenash.servermodlist.client.enums.Compatibility;
import com.minenash.servermodlist.client.enums.ServerType;
import com.minenash.servermodlist.client.gui.ServerListWidgetEntryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MultiplayerServerListWidgetServerEntryMixin {

    private static final Identifier ICONS = new Identifier("servermodlist","textures/icons.png");

    private String address;
    private ServerListWidgetEntryHelper helper;

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private ServerInfo server;

    @Shadow @Final private MultiplayerScreen screen;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo _c) {
        address = AddressUtils.getAddress(server);
        helper = new ServerListWidgetEntryHelper(client.getTextureManager()::bindTexture, screen::setTooltip);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(int _i, int j, int k, int l, int _m, int n, int o, boolean _bl, float _f, CallbackInfo _c) {
        this.client.getTextureManager().bindTexture(ICONS);

        if (server.ping < 0) {
            helper.renderNoConnection(k - 18, j, n, o);
            return;
        }

        if (!helper.hasData()) {
            ServerType type = ExtendedServerList.getServerType(address);
            helper.setInfo(type, Compatibility.getCompatibility(this.server, type, address), ExtendedServerList.getData(address), server.version);
        }

        helper.render(k+l-19, j+12, k-18, j, n, o);

        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
    }

}
