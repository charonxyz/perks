package dev.charon.perks.plugin;

import dev.charon.inventory.gui.menu.InventoryListener;
import dev.charon.perks.Perk;
import dev.charon.perks.command.PerkCommand;
import dev.charon.perks.listener.PerkListener;
import dev.charon.perks.listener.PlayerJoinListener;
import dev.charon.perks.manager.PerkManager;
import dev.charon.perks.task.PerkTask;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class PerkPlugin extends JavaPlugin {

    @Getter
    private static PerkPlugin instance;

    private Economy economy;
    private PerkManager perkManager;

    @Override
    public void onEnable() {
        instance = this;
        setupEconomy();
        perkManager = new PerkManager(economy);

        Bukkit.getScheduler().runTaskTimer(this, new PerkTask(perkManager), 0, 20*15);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(perkManager), this);
        getServer().getPluginManager().registerEvents(new PerkListener(perkManager), this);

        getCommand("perk").setExecutor(new PerkCommand());
        InventoryListener.register(this);
    }

    @Override
    public void onDisable() {
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }
}
