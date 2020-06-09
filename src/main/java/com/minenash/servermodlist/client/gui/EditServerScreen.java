package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.AddressUtils;
import com.minenash.servermodlist.client.config.ConfigManager;
import com.minenash.servermodlist.client.enums.ServerTypeOverride;
import com.minenash.servermodlist.client.interfaces.ExtendedMultiplayerScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;

import java.net.IDN;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class EditServerScreen extends Screen {

    private static final Identifier TRASH_ICON = new Identifier("servermodlist","textures/trash.png");

    private ButtonWidget buttonAdd;
    private ButtonWidget buttonSTO;
    private final BooleanConsumer callback;
    private final ServerInfo server;
    private TextFieldWidget addressField;
    private TextFieldWidget serverNameField;
    private ButtonWidget resourcePackOptionButton;
    private final Screen parent;
    private final Predicate<String> addressTextFilter = (string) -> {
        if (ChatUtil.isEmpty(string))
            return true;
        else {
            String[] strings = string.split(":");
            if (strings.length == 0)
                return true;
            else
                try {
                    IDN.toASCII(strings[0]);
                    return true;
                } catch (IllegalArgumentException var3) {
                    return false;
                }
        }
    };

    public EditServerScreen(Screen parent, BooleanConsumer callback, ServerInfo server) {
        super(new TranslatableText("addServer.title"));
        this.parent = parent;
        this.callback = callback;
        this.server = server;
    }

    public void tick() {
        this.serverNameField.tick();
        this.addressField.tick();
    }

    private ServerTypeOverride override;
    private String address;

    protected void init() {
        assert this.minecraft != null;
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.serverNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 66, 200, 20, I18n.translate("addServer.enterName"));
        this.serverNameField.setSelected(true);
        this.serverNameField.setText(this.server.name);
        this.serverNameField.setChangedListener(this::onClose);
        this.children.add(this.serverNameField);
        this.addressField = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, I18n.translate("addServer.enterIp"));
        this.addressField.setMaxLength(128);
        this.addressField.setText(this.server.address);
        this.addressField.setTextPredicate(this.addressTextFilter);
        this.addressField.setChangedListener(this::onClose);
        this.children.add(this.addressField);
        this.resourcePackOptionButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString(), (buttonWidget) -> {
            this.server.setResourcePackState(ServerInfo.ResourcePackState.values()[(this.server.getResourcePack().ordinal() + 1) % ServerInfo.ResourcePackState.values().length]);
            this.resourcePackOptionButton.setMessage(I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePack().getName().asFormattedString());
        }));

        address = AddressUtils.getAddress(server);
        override = ConfigManager.serverTypeOverrides.getOrDefault(address, ServerTypeOverride.NA);

        buttonSTO = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 24, 200, 20, "Server Type Override: " + override.asString, (buttonWidget) -> {
            switch (override) {
                case NA: override = ServerTypeOverride.VANILLA; break;
                case VANILLA: override = ServerTypeOverride.FABRIC; break;
                case FABRIC: override = ServerTypeOverride.NA; break;
            }
            buttonSTO.setMessage("Server Type Override: " + override.asString);
        }));

        this.buttonAdd = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 88, 20, I18n.translate("addServer.add"), (buttonWidget) -> this.addAndClose()));
        this.addButton(new ButtonWidget(this.width / 2 + 12, this.height / 4 + 120 + 18, 88, 20, I18n.translate("gui.cancel"), (buttonWidget) -> this.callback.accept(false)));

        this.addButton(new TexturedButtonWidget(this.width / 2 + - 10, this.height / 4 + 120 + 18, 20, 20, 0, 0, 20, TRASH_ICON, 32, 64, (buttonWidget) -> {
            ExtendedMultiplayerScreen mp = (ExtendedMultiplayerScreen) this.parent;
            if (mp.getSelected() instanceof MultiplayerServerListWidget.ServerEntry) {
                String string = ((MultiplayerServerListWidget.ServerEntry)mp.getSelected()).getServer().name;
                Text text = new TranslatableText("selectServer.deleteQuestion");
                Text text2 = new TranslatableText("selectServer.deleteWarning", string);
                String string2 = I18n.translate("selectServer.deleteButton");
                String string3 = I18n.translate("gui.cancel");
                this.minecraft.openScreen(new ConfirmScreen(mp::removeEntryE, text, text2, string2, string3));
            }
        }, I18n.translate("narrator.button.delete")));
        this.updateButtonActiveState();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.addressField.getText();
        String string2 = this.serverNameField.getText();
        this.init(client, width, height);
        this.addressField.setText(string);
        this.serverNameField.setText(string2);
    }

    private void onClose(String text) {
        this.updateButtonActiveState();
    }

    public void removed() {
        assert this.minecraft != null;
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    private void addAndClose() {
        if (!this.server.address.equals(this.addressField.getText()))
            ConfigManager.serverTypeOverrides.remove(address);
        if (override == ServerTypeOverride.NA)
            ConfigManager.serverTypeOverrides.remove(address);
        else
            ConfigManager.serverTypeOverrides.put(AddressUtils.getAddress(this.addressField.getText()), override);
        ConfigManager.saveConfig();

        this.server.name = this.serverNameField.getText();
        this.server.address = this.addressField.getText();
        this.callback.accept(true);
    }

    public void onClose() {
        this.updateButtonActiveState();
        assert this.minecraft != null;
        this.minecraft.openScreen(this.parent);
    }

    private void updateButtonActiveState() {
        String string = this.addressField.getText();
        boolean bl = !string.isEmpty() && string.split(":").length > 0 && string.indexOf(32) == -1;
        this.buttonAdd.active = bl && !this.serverNameField.getText().isEmpty();
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 17, 16777215);
        this.drawString(this.font, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.font, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
        this.serverNameField.render(mouseX, mouseY, delta);
        this.addressField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}