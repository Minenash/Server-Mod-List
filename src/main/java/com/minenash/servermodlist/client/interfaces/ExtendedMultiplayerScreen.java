package com.minenash.servermodlist.client.interfaces;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;

public interface ExtendedMultiplayerScreen {
    void removeEntryE(boolean confirmedAction);
    MultiplayerServerListWidget.Entry getSelected();
}
