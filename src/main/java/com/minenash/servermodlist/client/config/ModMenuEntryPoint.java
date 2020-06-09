package com.minenash.servermodlist.client.config;

import com.minenash.servermodlist.client.enums.AllowModpackImage;
import com.minenash.servermodlist.client.enums.TextAlignment;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ModMenuEntryPoint implements ModMenuApi {

    @Override
    public String getModId() {
        return "servermodlist";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }



    private static class ConfigScreen extends Screen {

        private final Screen parent;
        private final MinecraftClient client;
        private final TextRenderer font;
        private static final int OPTION_START = 32+13, OPTION_BUFFER = 30;

        boolean newLights = ConfigManager.showCompatibilityLights;
        boolean newUnkSerType = ConfigManager.showUnknownServerTypeIcon;
        boolean newCheckVerCompat = ConfigManager.checkFabricModVersionCompatibility;
        TextAlignment newTextAlign = ConfigManager.modpackScreenTextAlignment;
        AllowModpackImage allowImg = ConfigManager.allowModpackImage;

        protected ConfigScreen(Screen parent) {
            super(new TranslatableText("sml.config.screen.title"));
            this.parent = parent;
            this.client = MinecraftClient.getInstance();
            this.font = client.textRenderer;
        }

        @Override
        protected void init() {

            int buttonWidth = 75;
            int buttonX = this.width - buttonWidth - 10;

            this.addButton(new ButtonWidget(buttonX,OPTION_START,buttonWidth,20,
                    getShowHideLabel(newLights), (button) -> {
                newLights = !newLights;
                button.setMessage(getShowHideLabel(newLights));
            }));

            this.addButton(new ButtonWidget(buttonX,OPTION_START + OPTION_BUFFER,buttonWidth,20,
                    getShowHideLabel(newUnkSerType), (button) -> {
                newUnkSerType = !newUnkSerType;
                button.setMessage(getShowHideLabel(newUnkSerType));
            }));

            this.addButton(new ButtonWidget(buttonX,OPTION_START + OPTION_BUFFER*2,buttonWidth,20,
                    getTextAlignmentLabel(newTextAlign), (button) -> {
                newTextAlign = newTextAlign.getNext();
                button.setMessage(getTextAlignmentLabel(newTextAlign));
            }));

            this.addButton(new ButtonWidget(buttonX,OPTION_START + OPTION_BUFFER*3,buttonWidth,20,
                    getEnableDisableLabel(newCheckVerCompat), (button) -> {
                newCheckVerCompat = !newCheckVerCompat;
                button.setMessage(getEnableDisableLabel(newCheckVerCompat));
            }));

            this.addButton(new ButtonWidget(buttonX,OPTION_START + OPTION_BUFFER*4,buttonWidth,20,
                    getAllowModPackImageLabel(allowImg), (button) -> {
                allowImg = allowImg.getNext();
                button.setMessage(getAllowModPackImageLabel(allowImg));
            }));

            this.addButton(new ButtonWidget(this.width/2 - 100,this.height - 28,200,20,
                    I18n.translate("gui.done"), (button) -> onClose()));

        }

        private String getShowHideLabel(boolean value) {
            return I18n.translate(value ? "sml.config.screen.show" : "sml.config.screen.hide");
        }

        private String getEnableDisableLabel(boolean value) {
            return I18n.translate(value ? "sml.config.screen.enabled" : "sml.config.screen.disabled");
        }

        private String getTextAlignmentLabel(TextAlignment alignment) {
            switch (alignment) {
                case CENTER: return I18n.translate("sml.config.screen.center");
                case LEFT: return I18n.translate("sml.config.screen.left");
                case RIGHT: return I18n.translate("sml.config.screen.right");
            }
            return "";
        }

        private String getAllowModPackImageLabel(AllowModpackImage allowImg) {
            switch (allowImg) {
                case ALWAYS: return I18n.translate("sml.config.screen.always");
                case ONCE: return I18n.translate("sml.config.screen.once");
                case NEVER: return I18n.translate("sml.config.screen.never");
            }
            return "";
        }

        @Override
        public void onClose() {
            ConfigManager.showCompatibilityLights = newLights;
            ConfigManager.showUnknownServerTypeIcon = newUnkSerType;
            ConfigManager.modpackScreenTextAlignment = newTextAlign;
            ConfigManager.checkFabricModVersionCompatibility = newCheckVerCompat;
            ConfigManager.saveConfig();
            client.openScreen(parent);
        }

        @Override
        public void render(int mouseX, int mouseY, float delta) {
            this.renderBackground();

            overlayBackground(0, 36, this.width, this.height - 30-9);

            int x = 20;

            drawCenteredString(font, I18n.translate("sml.config.screen.title"), this.width / 2, 13, 0xFFFFFF);
            drawString(font, I18n.translate("sml.config.screen.compatibility_lights"), x, OPTION_START + 5, 0xFFFFFF);
            drawString(font, I18n.translate("sml.config.screen.unknown_server_type_icon"), x, OPTION_START + OPTION_BUFFER + 5, 0xFFFFFF);
            drawString(font, I18n.translate("sml.config.screen.modpack_screen_text_alignment"), x, OPTION_START + OPTION_BUFFER*2 + 5, 0xFFFFFF);
            drawString(font, I18n.translate("sml.config.screen.check_fabric_mod_version_compatibility"), x, OPTION_START + OPTION_BUFFER*3 + 5, 0xFFFFFF);
            drawString(font, I18n.translate("sml.config.screen.allow_modpack_image"), x, OPTION_START + OPTION_BUFFER*4, 0xFFFFFF);

            super.render(mouseX, mouseY, delta);
        }

        static void overlayBackground(int x1, int y1, int x2, int y2) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            Objects.requireNonNull(MinecraftClient.getInstance()).getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(x1, y2, 0.0D).texture(x1 / 32.0F, y2 / 32.0F).color(32, 32, 32, 255).next();
            buffer.vertex(x2, y2, 0.0D).texture(x2 / 32.0F, y2 / 32.0F).color(32, 32, 32, 255).next();
            buffer.vertex(x2, y1, 0.0D).texture(x2 / 32.0F, y1 / 32.0F).color(32, 32, 32, 255).next();
            buffer.vertex(x1, y1, 0.0D).texture(x1 / 32.0F, y1 / 32.0F).color(32, 32, 32, 255).next();
            tessellator.draw();
        }
    }

}
