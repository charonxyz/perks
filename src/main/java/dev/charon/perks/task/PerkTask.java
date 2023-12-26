package dev.charon.perks.task;

import dev.charon.perks.Perk;
import dev.charon.perks.Perks;
import dev.charon.perks.manager.PerkManager;
import dev.charon.perks.player.PerkPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PerkTask implements Runnable {

    private final PerkManager perkManager;

    public PerkTask(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    @Override
    public void run() {
        if (perkManager.getPerkPlayers().isEmpty()) return;

        for (PerkPlayer perkPlayer : perkManager.getPerkPlayers()) {
            if (!perkPlayer.getPerks().isEmpty()) {
                for (var perkSet : perkPlayer.getPerks().entrySet()) {
                    String id = String.valueOf(perkSet.getKey());
                    Perk perk = Perks.getPerkById(Integer.parseInt(id));
                    if (perk == null) return;
                    if (perkSet.getValue()) {
                        togglePerk(perkPlayer, perk);
                    }
                }
            }
        }
    }

    private void togglePerk(PerkPlayer perkPlayer, Perk perk) {
        Player bukkitPlayer = Bukkit.getPlayer(perkPlayer.getUuid());
        if (bukkitPlayer == null) return;

        if (perk == Perks.FLY.getPerk()) {
            if (bukkitPlayer.getWorld().getName().equalsIgnoreCase("Plot")) {
                if (!bukkitPlayer.getAllowFlight()) bukkitPlayer.setAllowFlight(true);
                if (!bukkitPlayer.isFlying()) bukkitPlayer.setFlying(true);
            }
        }

        if (perk == Perks.NO_HUNGER.getPerk()) bukkitPlayer.setFoodLevel(20);
        if (perk == Perks.HASTE.getPerk()) bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 1));
        if (perk == Perks.SPEED.getPerk()) bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));

    }
}
