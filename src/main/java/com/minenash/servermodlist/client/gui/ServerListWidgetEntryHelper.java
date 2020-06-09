package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ClientModList;
import com.minenash.servermodlist.client.ClientServerData;
import com.minenash.servermodlist.client.ModEntry;
import com.minenash.servermodlist.client.config.ConfigManager;
import com.minenash.servermodlist.client.enums.Compatibility;
import com.minenash.servermodlist.client.enums.ServerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ServerListWidgetEntryHelper {

    private static final Identifier ICONS = new Identifier("servermodlist","textures/icons.png");
    private static final Identifier FORGE = new Identifier("servermodlist","textures/forge_icon.png");

    private final Consumer<String> setTooltip;
    private final Consumer<Identifier> bindTexture;
    private final TextRenderer font;
    private MultiplayerIcon typeIcon;
    private MultiplayerIcon compatibilityIcon;
    private MultiplayerIcon compatibilityLight;
    private String tooltip;
    private boolean isForge;
    private boolean hasData;

    public ServerListWidgetEntryHelper(Consumer<Identifier> bindTexture, Consumer<String> setTooltip) {
        this.setTooltip = setTooltip;
        this.bindTexture = bindTexture;
        this.font = MinecraftClient.getInstance().textRenderer;
        hasData = false;
    }

    public boolean hasData() { return hasData; }

    public void setInfo(ServerType type, Compatibility compatibility, ClientServerData data, String version) {
        hasData = true;
        switch (type) {
            case SPIGOT: typeIcon = MultiplayerIcon.SPIGOT; break;
            case FABRIC: typeIcon = MultiplayerIcon.FABRIC; break;
            case FORGE: typeIcon = MultiplayerIcon.FORGE; break;
            case VANILLA: typeIcon = MultiplayerIcon.VANILLA; break;
            case UNKNOWN: typeIcon = MultiplayerIcon.UNKNOWN; break;
            default: typeIcon = null;
        }

        isForge = type == ServerType.FORGE;
        tooltip = generateTooltip(type, compatibility, data, version);

        System.out.println(type);
        System.out.println(compatibility);

        if (type == ServerType.FABRIC) {
            switch (compatibility) {
                case TRUE:
                    compatibilityIcon = MultiplayerIcon.CHECK;
                    compatibilityLight = MultiplayerIcon.COMPATIBLE_YES;
                    break;
                case UNKNOWN:
                    compatibilityIcon = MultiplayerIcon.UNKNOWN_SMALL;
                    compatibilityLight = MultiplayerIcon.COMPATIBLE_MAYBE;
                    break;
                case NO_INFO:
                    compatibilityIcon = null;
                    compatibilityLight = MultiplayerIcon.COMPATIBLE_MAYBE;
                    break;
                case FALSE:
                case FALSE_VERSION:
                    compatibilityIcon = MultiplayerIcon.CROSS;
                    compatibilityLight = MultiplayerIcon.COMPATIBLE_NO;
                    break;
                default:
                    compatibilityIcon = null;
                    compatibilityLight = MultiplayerIcon.COMPATIBLE_NO;
            }
        }
        else {
            compatibilityIcon = null;
            compatibilityLight = typeIcon == null ? MultiplayerIcon.COMPATIBLE_NO
                    : type == ServerType.VANILLA ? MultiplayerIcon.COMPATIBLE_YES
                    : MultiplayerIcon.COMPATIBLE_MAYBE;
        }

        if (!ConfigManager.showUnknownServerTypeIcon && type == ServerType.UNKNOWN)
            typeIcon = null;
    }


    public void render(int icon_x, int icon_y, int lights_x, int lights_y, int mx, int my) {

        if (typeIcon != null) {
            if (isForge) {
                bindTexture.accept(FORGE);
                typeIcon.render(icon_x, icon_y, mx, my, setTooltip, tooltip);
                bindTexture.accept((ICONS));
            } else {
                typeIcon.render(icon_x, icon_y, mx, my, setTooltip, tooltip);
            }
        }

        if (compatibilityIcon != null) {
            //System.out.println(compatibilityIcon.u + " " + compatibilityIcon.v);
            compatibilityIcon.render(icon_x, icon_y, mx, my, setTooltip, tooltip);
        }

        if (ConfigManager.showCompatibilityLights)
            compatibilityLight.render(lights_x, lights_y, mx, my, setTooltip, tooltip);

    }

    public void renderNoConnection(int x, int y, int mx, int my) {
        if (ConfigManager.showCompatibilityLights)
           MultiplayerIcon.COMPATIBLE_NO.render(x, y, mx, my, setTooltip, I18n.translate("sml.mp.tooltip.lights.incompatible.no_connection"));
    }

    public String generateTooltip(ServerType type, Compatibility compatibility, ClientServerData data, String version) {
        StringBuilder tooltip = new StringBuilder();

        if (type == ServerType.NO_INFO)
            return tooltip.append("§cNo Information Available").toString();

        tooltip.append("§3").append(type.asString()).append(" Server\n§9Version: §7").append(formatString(version));

        if (data != null && data.isModPack()) {
            switch (compatibility) {
                case TRUE: return tooltip.append("\n§aYou have the required modpack!").toString();
                case UNKNOWN:
                    boolean client_is_null = ConfigManager.modpack_version == null;
                    boolean server_is_null = data.getModPack().version.equals("No Version Provided");
                    return tooltip
                        .append("\n\n§eModpack version differs")
                        .append("\n§9Client: §7")
                        .append(formatString(client_is_null? "Unknown" : ConfigManager.modpack_version))
                        .append("\n§9Server: §7")
                        .append(formatString(server_is_null? "Unknown" : data.getModPack().version))
                        .toString();
                case FALSE:return tooltip
                        .append("\n\n§cRequires Modpack\n§cName §a")
                        .append(formatString(data.getModPack().name))
                        .append("\n§cVersion §a")
                        .append(formatString(data.getModPack().version)).toString();
            }
            return tooltip.append("\n§e???").toString();
        }

        switch (compatibility) {
            case TRUE: tooltip.append("\n§aCompatible"); break;
            case FALSE: tooltip.append("\n§cIncompatible"); break;
            case FALSE_VERSION: tooltip.append("\n§cIncompatible Version"); break;
            case UNKNOWN: tooltip.append("\n§eUnknown Compatibility"); break;
        }

        if (type != ServerType.FABRIC || data == null)
            return tooltip.toString();

        List<ModEntry> missingMods = data.getMods()[0].stream().filter((mod) -> ClientModList.isModCompatible(mod) != Compatibility.TRUE).collect(Collectors.toList());

        if (missingMods.isEmpty())
            return tooltip.append("\n§aYou have all required mods!").toString();

        tooltip.append("\n§cMissing Mods:");
        for (ModEntry mod : missingMods)
            tooltip.append("\n§c- ").append(formatString(mod.getModname()));

        return tooltip.toString();
    }

    private String formatString(String str) {
        if (font.getStringWidth(str) <= 250) return str;
        return font.trimToWidth(str, 250 - font.getStringWidth("...")) + "...";
    }


}
