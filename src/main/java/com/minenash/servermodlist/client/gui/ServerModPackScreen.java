package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ModPack;
import com.minenash.servermodlist.client.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ServerModPackScreen extends Screen {

    private final Screen parent;
    private final TextRenderer font;
    private final ModPack modpack;
    private int center_x;


    public ServerModPackScreen(Screen parent, ModPack modpack) {
        super(new TranslatableText("sml.mp.modpackscreen.title"));
        this.parent = parent;
        this.font = MinecraftClient.getInstance().textRenderer;
        this.modpack = modpack;

    }

    @Override
    protected void init() {
        Objects.requireNonNull(this.minecraft).keyboard.enableRepeatEvents(true);
        center_x = this.width / 2;

        this.addButton(new ButtonWidget(center_x - 100 , this.height - 26, 200, 20, I18n.translate("gui.done"), (buttonWidget) -> onClose()));

        if (modpack.link != null)
            this.addButton(new ButtonWidget(center_x - 100 , this.height - 26 - 26, 200, 20, I18n.translate("sml.mp.modpackscreen.button.link"), (buttonWidget) ->
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

    private static final Pattern EMPTY_LINE = Pattern.compile("\\s+");

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();

        int max_width = Math.min(width-10, 400);

        this.drawCenteredString(font, formatString(modpack.name, max_width), center_x, 26, 0xFFFFFF);
        this.drawCenteredString(font, formatString(modpack.version, max_width), center_x, 36, 0x555555);

        int area = this.height - 26 - 26 - 26 - 26 - 20;
        int capacity = area / 10;
        if (capacity < 1) return;

        List<String> lines = getLines(capacity,max_width);
        int line_y = 26 + 30 + (lines.size() + 6 <= capacity ? 28 : (area-lines.size()*10)/2);

        for (String line : lines) {
            drawDescriptionLine(font,line,line_y, max_width);
            line_y += 10;
        }

        super.render(mouseX, mouseY, delta);
    }

    public String formatString(String str, int width) {
        if (font.getStringWidth(str) <= width) return str;
        return font.trimToWidth(str, width - font.getStringWidth("...")) + "...";
    }

    public List<String> getLines(int capacity, int max_width) {
        List<String> lines = new ArrayList<>();
        for (String line : modpack.description.split("\n"))
            lines.addAll(font.wrapStringToWidthAsList(line, 300));

        if (lines.size() > capacity) {
            lines.removeIf( (line) -> line.isEmpty() || EMPTY_LINE.matcher(line).matches());
            if (lines.size() > capacity) {
                StringBuilder description = new StringBuilder();
                for (String line : lines)
                    description.append(line).append("  ");
                lines.clear();
                lines.addAll(font.wrapStringToWidthAsList(description.toString(), max_width));

                if (lines.size() > capacity)
                    lines.set(capacity-1, "...");
                while (capacity < lines.size()) {
                    lines.remove(capacity);
                }
            }
        }

        return lines;
    }

    private void drawDescriptionLine(TextRenderer font, String line, int y, int max_width) {
        switch (ConfigManager.modpackScreenTextAlignment) {
            case CENTER: drawCenteredString(font, line, center_x, y, 0xAAAAAA); break;
            case LEFT: drawString(font, line, center_x - max_width/2, y, 0xAAAAAA); break;
            case RIGHT: drawRightAlignedString(font, line, center_x + max_width/2, y, 0xAAAAAA); break;
        }
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.openScreen(this.parent);
    }

}
