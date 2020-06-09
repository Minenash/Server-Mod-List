package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ExtendedServerList;
import com.minenash.servermodlist.client.ModPack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class ServerModPackImageScreen extends Screen {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Screen previous;
    private final ModPack modpack;
    private double ratio;

    public ServerModPackImageScreen(Screen previous, ModPack modpack) {
        super(new TranslatableText("sml.mp.modpackscreen.title"));
        this.previous = previous;
        this.modpack = modpack;
    }

    @Override
    protected void init() {
        try(InputStream in = new URL(modpack.image).openStream()) {
            NativeImageBackedTexture image = new NativeImageBackedTexture(NativeImage.read(in));
            client.getTextureManager().registerTexture(new Identifier("test"), image);
            ratio = image.getImage().getWidth()/(double)image.getImage().getHeight();
        } catch (IOException e) {
            e.printStackTrace();
            client.openScreen(new ServerModPackScreen(previous, modpack));
        }

        Objects.requireNonNull(this.minecraft).keyboard.enableRepeatEvents(true);
        int center_x = this.width / 2;

        this.addButton(new ButtonWidget(center_x - 100 , this.height - 26, 200, 20,
                I18n.translate("gui.done"), (buttonWidget) -> onClose()));

        if (modpack.link != null)
            this.addButton(new ButtonWidget(center_x - 100 , this.height - 26 - 26, 200, 20,
                I18n.translate("sml.mp.modpackscreen.button.link"), (buttonWidget) ->
                    this.minecraft.openScreen(new ConfirmChatLinkScreen((bl) -> {
                        if (bl) {
                            Util.getOperatingSystem().open(modpack.link.replaceAll("§.", ""));
                            System.out.println(modpack.link.replaceAll("§.", ""));
                        }
                        this.minecraft.openScreen(this);
                    },
                modpack.link, true))));
        else {
            ButtonWidget linkButton = this.addButton(new ButtonWidget(center_x - 100 , this.height - 26 - 26, 200, 20, I18n.translate("sml.mp.modpackscreen.button.link"), null));
            linkButton.active = false;
        }

    }

    private boolean firstRender = true;

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        client.getTextureManager().bindTexture(new Identifier("test"));

        if (firstRender) firstRender();

        int w = (int)(ratio*height);

        DrawableHelper.blit(width/2 - w/2, 0, 0.0F, 0.0F, w, height, w, height);

        super.render(mouseX, mouseY, delta);
    }

    private void firstRender() {
        firstRender = false;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0, 0, 0).texture(0,0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(width, 0, 0).texture(0,0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(width, height, 0).texture(0,0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0, height, 0).texture(0,0).color(0, 0, 0, 255).next();
        tessellator.draw();
    }
}
