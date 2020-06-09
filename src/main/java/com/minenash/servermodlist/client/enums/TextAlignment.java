package com.minenash.servermodlist.client.enums;

public enum TextAlignment {
    CENTER, LEFT, RIGHT;

    public TextAlignment getNext() {
        switch (this) {
            case CENTER: return LEFT;
            case LEFT: return RIGHT;
        }
        return CENTER;
    }
}
