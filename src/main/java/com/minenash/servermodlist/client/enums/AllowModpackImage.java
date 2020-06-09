package com.minenash.servermodlist.client.enums;

public enum AllowModpackImage {
    ALWAYS, ONCE, NEVER;

    public AllowModpackImage getNext() {
        switch (this) {
            case ONCE: return ALWAYS;
            case ALWAYS: return NEVER;
        }
        return ONCE;
    }
}
