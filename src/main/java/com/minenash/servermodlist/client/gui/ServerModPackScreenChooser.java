package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ModPack;
import com.minenash.servermodlist.client.config.ConfigManager;
import com.minenash.servermodlist.client.enums.AllowModpackImage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

public class ServerModPackScreenChooser extends Screen {

    private final Screen parent;
    private final ModPack modpack;

    public ServerModPackScreenChooser(Screen parent, ModPack modpack) {
        super(new TranslatableText("sml.mp.modpackscreen.title"));
        this.parent = parent;
        this.modpack = modpack;
    }

    @Override
    protected void init() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (modpack.image == null)
            client.openScreen(new ServerModPackScreen(parent, modpack));

        switch (ConfigManager.allowModpackImage) {
            case ALWAYS: client.openScreen(new ServerModPackImageScreen(parent,modpack)); return;
            case NEVER: client.openScreen(new ServerModPackScreen(parent,modpack)); return;
        }

        this.addButton(new ButtonWidget(this.width / 2 - 50 - 105, (this.height/3)*2, 100, 20, "Always", (buttonWidget) -> {
            ConfigManager.allowModpackImage = AllowModpackImage.ALWAYS;
            ConfigManager.saveConfig();
            client.openScreen(new ServerModPackImageScreen(parent,modpack));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 50, (this.height/3)*2, 100, 20, "Once", (buttonWidget) -> {
            client.openScreen(new ServerModPackImageScreen(parent,modpack));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 50 + 105, (this.height/3)*2, 100, 20, "Never", (buttonWidget) -> {
            ConfigManager.allowModpackImage = AllowModpackImage.NEVER;
            ConfigManager.saveConfig();
            client.openScreen(new ServerModPackScreen(parent,modpack));
        }));

    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();

        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 40, 0xFFFFFF);
        this.drawCenteredString(this.font, "Do you want to show the image the server gave?", this.width / 2, 80, 16777215);
        this.drawCenteredString(this.font, "§o(Gets image from the web)", this.width / 2, 95, 0xAAAAAA);

        super.render(mouseX, mouseY, delta);
    }
}
