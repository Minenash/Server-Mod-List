package com.minenash.servermodlist.client.gui;

import com.minenash.servermodlist.client.ModEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ServerModListWidget extends EntryListWidget<ServerModListWidgetEntry> {

    ServerModsScreen screen;
    private boolean scrolling;
    public final boolean isEmpty;

    public ServerModListWidget(MinecraftClient client, int width, int height, int y1, int y2, int x, List<ModEntry> mods, ServerModsScreen screen) {
        super(client, width, height, y1, y2, 37);
        this.setLeftPos(x);
        this.screen = screen;
        isEmpty = mods.isEmpty();

        for (ModEntry mod : mods)
            this.addEntry(new ServerModListWidgetEntry(mod,screen));

    }

    public void setEntries(List<ModEntry> mods) {
        this.clearEntries();
        for (ModEntry mod : mods)
            this.addEntry(new ServerModListWidgetEntry(mod,screen));
    }

    @Override
    public int getRowWidth() {
        return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
    }

    @Override
    protected int getRowLeft() {
        return left + 6;
    }

    @Override
    protected int getScrollbarPosition() {
        return left + this.width - 6;
    }

    @Override
    protected void updateScrollingState(double double_1, double double_2, int int_1) {
        super.updateScrollingState(double_1, double_2, int_1);
        this.scrolling = int_1 == 0 && double_1 >= (double) this.getScrollbarPosition() && double_1 < (double) (this.getScrollbarPosition() + 6);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && this.scrolling) {
            if (mouseY < (double)this.top) {
                this.setScrollAmount(0.0D);
            } else if (mouseY > (double)this.bottom) {
                this.setScrollAmount(this.getMaxScroll());
            } else {
                double d = Math.max(1, this.getMaxScroll());
                int i = this.bottom - this.top;
                int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
                double e = Math.max(1.0D, d / (double)(i - j));
                this.setScrollAmount(this.getScrollAmount() + deltaY * e);

            }
            return true;
        } else
            return false;
    }

    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        this.updateScrollingState(double_1, double_2, int_1);
        if (!this.isMouseOver(double_1, double_2)) {
            return false;
        } else if (int_1 == 0) {
            this.clickedHeader((int) (double_1 - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)), (int) (double_2 - (double) this.top) + (int) this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;

    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        updateScrollingState(mouseX, mouseY, button);
        return false;
    }

    @Override
    protected int getMaxPosition() {
        return super.getMaxPosition() + 4;
    }

}
