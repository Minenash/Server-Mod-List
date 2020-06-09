package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ExtendedServerList;
import com.minenash.servermodlist.client.ModEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ServerModsScreen extends Screen {

    private final Screen parent;
    private final String address;
    private AbstractButtonWidget debugButtonOff, debugButtonOn;
    private ServerModListWidget required, recommended, list3;
    private int req_x, recom_x, ls3_x, vert_center;
    private static Boolean debug = false;
    public MinecraftClient client;

    public List<String> toolTip;



    private static final Identifier DEBUG_OFF = new Identifier("servermodlist","textures/debug.png");
    private static final Identifier DEBUG_ON = new Identifier("servermodlist","textures/debug_on.png");

    public ServerModsScreen(Screen previous, String address) {
        super(new TranslatableText("Hello"));
        this.parent = previous;
        this.address = address;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void init() {
        Objects.requireNonNull(this.minecraft).keyboard.enableRepeatEvents(true);

        int iwidth = Math.min(this.width, 618);
        int offset = (this.width - iwidth) / 2;
        int frame_width = (iwidth - 18) / 3;

        req_x = offset + iwidth / 2 - iwidth / 3;
        recom_x = offset + iwidth / 2;
        ls3_x = offset + iwidth / 2 + iwidth / 3;
        vert_center = 42 + (this.height - 32 - 42)/2;

        this.addButton(new ButtonWidget(this.width / 2 - 100 , this.height - 26, 200, 20, I18n.translate("gui.done"), (buttonWidget) -> onClose()));

        List<ModEntry>[] mods = ExtendedServerList.getData(address).getMods();

        debugButtonOff = this.addButton(new TexturedButtonWidget(width / 2 - width / 3 - (width - 16) / 3 / 2, this.height - 26, 20, 20, 0, 0, 20, DEBUG_OFF, 32, 64, (buttonWidget) -> {
            debug = true;
            debugButtonOff.visible = false;
            debugButtonOn.visible = true;
            list3.setEntries(mods[3]);
            list3.setScrollAmount(-30000);
        }, I18n.translate("sml.mp.modscreen.button.debug.on")));

        debugButtonOn = this.addButton(new TexturedButtonWidget(width / 2 - width / 3 - (width - 16) / 3 / 2, this.height - 26, 20, 20, 0, 0, 20, DEBUG_ON, 32, 64, (buttonWidget) -> {
            debug = false;
            debugButtonOff.visible = true;
            debugButtonOn.visible = false;
            list3.setEntries(mods[2]);
            list3.setScrollAmount(-30000);
        }, I18n.translate("sml.mp.modscreen.button.debug.off")));

        required = new ServerModListWidget(minecraft, frame_width, this.height, 42, this.height - 32 ,req_x - frame_width / 2, mods[0], this);
        recommended = new ServerModListWidget(minecraft, frame_width, this.height, 42, this.height - 32 ,recom_x - frame_width / 2, mods[1], this);

        if (!debug) {
            list3 = new ServerModListWidget(minecraft, frame_width, this.height, 42, this.height - 32 , ls3_x - frame_width / 2, mods[2], this);
            debugButtonOn.visible = false;
        }
        else {
            debugButtonOff.visible = false;
            list3 = new ServerModListWidget(minecraft, frame_width, this.height, 42, this.height - 32 , ls3_x - frame_width / 2, mods[3], this);
        }

    }


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        toolTip = null;

        required.render(mouseX, mouseY, delta);
        recommended.render(mouseX, mouseY, delta);
        list3.render(mouseX, mouseY, delta);

        if (required.isEmpty)
            this.drawCenteredString(this.font, I18n.translate("sml.mp.modscreen.none"), req_x, vert_center, 0x555555);

        if (recommended.isEmpty)
           this.drawCenteredString(this.font, I18n.translate("sml.mp.modscreen.none"), recom_x, vert_center, 0x555555);

        if (list3.isEmpty)
            this.drawCenteredString(this.font, I18n.translate("sml.mp.modscreen.none"), ls3_x, vert_center, 0x555555);


        this.drawCenteredString(this.font, I18n.translate("sml.mp.modscreen.title"),recom_x, 10, 0xFFFFFF);
        this.drawCenteredString(this.font, I18n.translate("sml.mp.modscreen.required"),req_x, 30, 0x888888);
        this.drawCenteredString(this.font, I18n.translate("sml.mp.modscreen.recommended"),recom_x, 30, 0x888888);
        this.drawCenteredString(this.font, debug? I18n.translate("sml.mp.modscreen.hidden") : I18n.translate("sml.mp.modscreen.supports"), ls3_x, 30, 0x888888);

        super.render(mouseX, mouseY, delta);
        if (toolTip != null)
            renderTooltip(toolTip, mouseX, mouseY);
    }

    @Override
    public void onClose() {
        debug = false;
        assert this.minecraft != null;
        this.minecraft.openScreen(this.parent);
    }

    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        return (required.isMouseOver(double_1, double_2) && required.mouseScrolled(double_1, double_2, double_3))
            || (recommended.isMouseOver(double_1, double_2) && recommended.mouseScrolled(double_1, double_2, double_3))
            || (list3.isMouseOver(double_1, double_2) && list3.mouseScrolled(double_1, double_2, double_3));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return required.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
            || recommended.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
            || list3.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
            || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button)
            || (required.isMouseOver(mouseX, mouseY) && required.mouseClicked(mouseX, mouseY, button))
            || (recommended.isMouseOver(mouseX, mouseY) && recommended.mouseClicked(mouseX, mouseY, button))
            || (list3.isMouseOver(mouseX, mouseY) && list3.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return required.mouseReleased(mouseX, mouseY, button)
            || recommended.mouseReleased(mouseX, mouseY, button)
            || list3.mouseReleased(mouseX, mouseY, button)
            || super.mouseReleased(mouseX, mouseY, button);
    }


}
