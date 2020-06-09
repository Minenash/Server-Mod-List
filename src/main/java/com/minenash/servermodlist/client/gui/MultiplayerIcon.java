package com.minenash.servermodlist.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class MultiplayerIcon {
    
    public static final MultiplayerIcon FABRIC  = MultiplayerIcon.simple(0,0,1,1);
    public static final MultiplayerIcon SPIGOT  = MultiplayerIcon.simple(1,0,1,1);
    public static final MultiplayerIcon VANILLA  = MultiplayerIcon.simple(2,0,1,1,2,5,3);
    public static final MultiplayerIcon UNKNOWN = MultiplayerIcon.simple(6,0,2,3,5.33F, 6, 3);
    public static final MultiplayerIcon CHECK   = MultiplayerIcon.simple(0,1,1,1,2,-1,0);
    public static final MultiplayerIcon CROSS   = MultiplayerIcon.simple(1,1,1,1,2,-2,-2);
    public static final MultiplayerIcon UNKNOWN_SMALL = MultiplayerIcon.simple(6,0,2,3,8F,1,0);

    public static final MultiplayerIcon COMPATIBLE_YES   = MultiplayerIcon.raw(0,32,10,28,128);
    public static final MultiplayerIcon COMPATIBLE_MAYBE = MultiplayerIcon.raw(16,32,10,28,128);
    public static final MultiplayerIcon COMPATIBLE_NO    = MultiplayerIcon.raw(32,32,10,28,128);
    public static final MultiplayerIcon COMPATIBLE_NULL  = MultiplayerIcon.raw(48,32,10,28,128);

    public static final MultiplayerIcon FORGE = MultiplayerIcon.raw(0,0,16,16,16);
    
    
    public int u, v, width, height, tex, offset_x, offset_y;

    private MultiplayerIcon(int u, int v, int width, int height, int tex, int offset_x, int offset_y) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.tex = tex;
        this.offset_x = offset_x;
        this.offset_y = offset_y;

    }
    private static MultiplayerIcon raw(int u, int v, int width, int height, int tex) {
        return new MultiplayerIcon(u,v,width,height,tex,0,0);
    }
    private static MultiplayerIcon simple(int u, int v, int usize, int vsize) {
        return new MultiplayerIcon(u*16, v*16, usize*16, vsize*16, 128,0,0);
    }
    private static MultiplayerIcon simple(int u, int v, int usize, int vsize, float scale, int offset_x, int offset_y) {
        int scaleFactor = (int)(16 * (1/scale));
        return new MultiplayerIcon(u * scaleFactor, v * scaleFactor, usize * scaleFactor,
                vsize * scaleFactor, (int)(128*(1/scale)), offset_x, offset_y);
    }

    public void render(int x, int y, int mx, int my, Consumer<String> setTooltip, String tooltip) {
        x += offset_x;
        y += offset_y;
        DrawableHelper.blit(x,y, u, v, width, height, tex, tex);
        if (mx >= x && mx <= x+width && my >= y && my <= y+height)
            setTooltip.accept(tooltip);
    }



}
