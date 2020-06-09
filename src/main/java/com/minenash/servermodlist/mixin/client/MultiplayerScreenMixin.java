package com.minenash.servermodlist.mixin.client;

import com.minenash.servermodlist.client.ClientServerData;
import com.minenash.servermodlist.client.ExtendedServerList;
import com.minenash.servermodlist.client.AddressUtils;
import com.minenash.servermodlist.client.config.ConfigManager;
import com.minenash.servermodlist.client.enums.ServerTypeOverride;
import com.minenash.servermodlist.client.gui.*;
import com.minenash.servermodlist.client.interfaces.ExtendedMultiplayerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen implements ExtendedMultiplayerScreen {

    @Shadow protected MultiplayerServerListWidget serverListWidget;
    @Shadow private ButtonWidget buttonDelete;
    @Shadow private ServerInfo selectedEntry;
    @Shadow protected abstract void editEntry(boolean confirmedAction);
    @Shadow protected abstract void removeEntry(boolean confirmedAction);

    protected MultiplayerScreenMixin(Text title) { super(title); }

    @Override
    public void removeEntryE(boolean confirmedAction) {
        if (confirmedAction)
            removeEntry(true);
        else
            this.minecraft.openScreen(new EditServerScreen(this, this::editEntry, this.selectedEntry));
    }

    @Override
    public MultiplayerServerListWidget.Entry getSelected() {
        return serverListWidget.getSelected();
    }

    @Redirect(method = "init", at = @At(value = "NEW", ordinal = 4, target = "net/minecraft/client/gui/widget/ButtonWidget"))
    public ButtonWidget modsButton(int x, int y, int width, int height, String label, ButtonWidget.PressAction action) {
        return new ButtonWidget(x, y, width, height, I18n.translate("sml.mp.button.mods"), (buttonWidget) -> {
            String address = AddressUtils.getAddress(((MultiplayerServerListWidget.ServerEntry) this.serverListWidget.getSelected()).getServer());
            ClientServerData data = ExtendedServerList.getData(address);
            if (data.isModPack())
                this.minecraft.openScreen(new ServerModPackScreenChooser(this, data.getModPack()));
            else
                this.minecraft.openScreen(new ServerModsScreen(this, address));
        });
    }

    @Redirect(method = "init", at = @At(value = "NEW", ordinal = 3, target = "net/minecraft/client/gui/widget/ButtonWidget"))
    public ButtonWidget editButton(int x, int y, int width, int height, String label, ButtonWidget.PressAction action) {
        return new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit"), (buttonWidget) -> {
            MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
                this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
                this.selectedEntry.copyFrom(serverInfo);
                this.minecraft.openScreen(new EditServerScreen(this, this::editEntry, this.selectedEntry));
            }

        });
    }

    @Inject(method = "updateButtonActivationStates", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void updateButtonActivationStates(CallbackInfo _c, MultiplayerServerListWidget.Entry entry) {
        if(entry instanceof MultiplayerServerListWidget.ServerEntry) {
            String address = AddressUtils.getAddress(((MultiplayerServerListWidget.ServerEntry) entry).getServer());
            ClientServerData data = ExtendedServerList.getData(address);
            this.buttonDelete.active = data != null && ConfigManager.serverTypeOverrides.get(address) != ServerTypeOverride.VANILLA;
            if (this.buttonDelete.active)
                this.buttonDelete.setMessage(data.isModPack() ? I18n.translate("sml.mp.button.modpack") : I18n.translate("sml.mp.button.mods"));
            else
                this.buttonDelete.setMessage(I18n.translate("sml.mp.button.mods"));
        }
    }

}
