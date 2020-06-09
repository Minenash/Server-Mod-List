package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ClientModList;
import com.minenash.servermodlist.client.ModEntry;
import com.minenash.servermodlist.client.enums.Compatibility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;


public class ServerModListWidgetEntry extends EntryListWidget.Entry<ServerModListWidgetEntry> {

    private static final Identifier ICONS = new Identifier("servermodlist","textures/icons.png");

    private final ServerModsScreen screen;
    private final ModEntry mod;
    private final TextRenderer font;
    private final List<String> tooltip;
    private final List<String> compatibilityTooltip;
    private final Compatibility status;

    public ServerModListWidgetEntry(ModEntry mod, ServerModsScreen screen) {
        this.mod = mod;
        this.screen = screen;
        font = MinecraftClient.getInstance().textRenderer;

        int nameSize = font.getStringWidth(mod.getModname() + " ");
        int idSize = font.getStringWidth(mod.getModid());
        int verSize = font.getStringWidth(mod.getVersion());
        int maxWidth = Math.max(Math.max(Math.max(200, nameSize), idSize), verSize);

        tooltip = new ArrayList<>();
        tooltip.add(mod.getModname());
        tooltip.add("§8§o" + mod.getModid() + "");
        tooltip.add("§7" + mod.getVersion());
        tooltip.add("");
        for (String row : font.wrapStringToWidthAsList(mod.getDescription(), maxWidth))
            tooltip.add("§7" + row);

        tooltip.add("");

        String authorList = mod.getAuthors().toString();
        List<String> authors = font.wrapStringToWidthAsList("By: " + authorList.substring(1,authorList.length()-1), maxWidth);
        for (String row : authors)
            tooltip.add("§8" + row);

        compatibilityTooltip = new ArrayList<>();

        status = ClientModList.isModCompatible(mod);

        switch (status) {
            case TRUE: compatibilityTooltip.add("§aYou have this mod!"); break;
            case FALSE: compatibilityTooltip.add("§cYou don't have this mod!"); break;
            case UNKNOWN:
                compatibilityTooltip.add("§eYou have a different version of this mod");
                compatibilityTooltip.add("§9Client: §7" + ClientModList.getVersion(mod.getModid()));
                compatibilityTooltip.add("§9Server: §7" + mod.getVersion());
                break;
        }
    }

    @Override
    public void render(int index, int y, int x, int width, int height, int mx, int my, boolean hovering, float delta) {
        font.draw(formatString(mod.getModname(),width-4), x, y+2, 0xFFFFFF);
        font.draw(formatString(mod.getDescription(),width+2), x, y+11+2, 0xAAAAAA);
        font.draw(formatString(mod.getVersion(),width+2), x, y+22+2, 0x888888);

        screen.client.getTextureManager().bindTexture(ICONS);

        drawCompatibilityIcon(x+width-6, y+2);
        if (mx >= x+width-6 && mx <= x+width && my >= y && my <= y+10)
            screen.toolTip =compatibilityTooltip;
        else if (hovering)
            screen.toolTip = tooltip;

    }

    private String formatString(String str, int width) {
        if (font.getStringWidth(str) <= width) return str;
        return font.trimToWidth(str, width - font.getStringWidth("...")) + "...";
    }

    private void drawCompatibilityIcon(int x, int y) {
        switch (status) {
            case TRUE: DrawableHelper.blit(x,y,0,8,8,8, 64, 64); break;
            case FALSE: DrawableHelper.blit(x,y,8,8,8,8, 64, 64); break;
            case UNKNOWN: DrawableHelper.blit(x,y-1,18,0,6,9, 24, 24); break;
        }
    }
}
