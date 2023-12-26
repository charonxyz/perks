package dev.charon.perks.listener;

import com.google.common.collect.Lists;
import dev.charon.perks.Constants;
import dev.charon.perks.Perks;
import dev.charon.perks.manager.PerkManager;
import dev.charon.perks.player.PerkPlayer;
import dev.charon.perks.player.PerkPlayerInventory;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class PerkListener implements Listener {

    private final PerkManager perkManager;

    public PerkListener(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    // This Event is for telekinesis perk and auto_smelt
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SURVIVAL) {
            Block block = event.getBlock();
            Collection<ItemStack> blockDrops = block.getDrops();

            PerkPlayer perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());
            if (hasPerks(perkPlayer)) {
                boolean auto_smelt = perkManager.isEnabled(player.getUniqueId(), Perks.AUTO_SMELT.getPerk());
                if (auto_smelt)
                    if (!blockDrops.isEmpty()) {
                        Collection<ItemStack> newDrops = new ArrayList<>(blockDrops);
                        blockDrops.clear();
                        block.setType(Material.AIR);
                        smeltOre(newDrops, block.getLocation());
                    }

                boolean telekinesis = perkManager.isEnabled(player.getUniqueId(), Perks.TELEKINESIS.getPerk());
                if (telekinesis) {
                    block.setType(Material.AIR);
                    if (!blockDrops.isEmpty()) {
                        for (ItemStack drop : blockDrops) {
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), drop);
                                player.sendMessage(Constants.PREFIX + "§cDa dein Inventar voll ist, konnte das Item nicht aufgesammelt werden!");
                            } else player.getInventory().addItem(drop);
                        }
                    }
                }
            }
        }
    }

    private void smeltOre(Collection<ItemStack> drops, Location location) {
        for (ItemStack itemToSmelt : drops) {
            Material toSmelt = itemToSmelt.getType();
            switch (toSmelt) {
                case RAW_GOLD -> itemToSmelt.setType(Material.GOLD_INGOT);
                case RAW_IRON -> itemToSmelt.setType(Material.IRON_INGOT);
            }
            location.getWorld().dropItem(location, itemToSmelt);
        }

    }

    // This Event is for no_fall_damage perk
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            PerkPlayer perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());
            if (hasPerks(perkPlayer)) {
                boolean no_fall_damage = perkManager.isEnabled(player.getUniqueId(), Perks.NO_FALL_DAMAGE.getPerk());
                if (no_fall_damage) event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        PerkPlayer perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());
        if (hasPerks(perkPlayer)) {
            boolean keep_items_after_death = perkManager.isEnabled(player.getUniqueId(), Perks.ITEMS_AFTER_DEATH.getPerk());
            if (keep_items_after_death) {
                perkManager.getPlayerInventory().put(player.getUniqueId(),
                        new PerkPlayerInventory(player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getExp()));
                player.sendMessage(Constants.PREFIX + "Du hast deine Items nicht verloren!");
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PerkPlayer perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());

        if (hasPerks(perkPlayer)) {
            if (perkManager.getPlayerInventory().containsKey(player.getUniqueId())) {
                updatePlayerInventory(player);
                updateXp(player);
            }
        }
    }

    private void updatePlayerInventory(Player player) {
        PerkPlayerInventory perkPlayerInventory = perkManager.getPlayerInventory().remove(player.getUniqueId());
        if (perkPlayerInventory.getContents() != null && perkPlayerInventory.getArmorContents() != null) {
            player.getInventory().setContents(perkPlayerInventory.getContents());
            player.getInventory().setArmorContents(perkPlayerInventory.getArmorContents());
        }
    }

    private void updateXp(Player player) {
        PerkPlayerInventory perkPlayerInventory = perkManager.getPlayerInventory().remove(player.getUniqueId());
        if (perkManager.isEnabled(player.getUniqueId(), Perks.XP_AFTER_DEATH.getPerk())) {
            player.setExp(perkPlayerInventory.getXp());
        }
    }

    private boolean hasPerks(PerkPlayer perkPlayer) {
        return perkManager.hasPerks(perkPlayer);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().getName().equalsIgnoreCase("Plot")) {
            PerkPlayer perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());

            if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("varox.fly") ) {
                if (perkManager.hasPerks(perkPlayer)) {
                    if (perkManager.isEnabled(player.getUniqueId(), Perks.FLY.getPerk())) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().getName().equalsIgnoreCase("Plot")) {
            PerkPlayer perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());

            if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("varox.fly") ) {
                if (perkManager.hasPerks(perkPlayer)) {
                    if (perkManager.isEnabled(player.getUniqueId(), Perks.FLY.getPerk())) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                }
            }
        }
    }
}
