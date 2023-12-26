package dev.charon.perks.listener;

import dev.charon.perks.manager.PerkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PlayerJoinListener implements Listener {

    private final PerkManager perkManager;

    public PlayerJoinListener(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        perkManager.loadPerkPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        perkManager.savePerkPlayer(perkManager.getPerkPlayer(player.getUniqueId()));
    }

}
