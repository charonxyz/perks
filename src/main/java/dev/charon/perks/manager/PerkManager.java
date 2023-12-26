package dev.charon.perks.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.charon.lib.config.ServerConfig;
import dev.charon.lib.util.Maths;
import dev.charon.perks.Constants;
import dev.charon.perks.Perk;
import dev.charon.perks.Perks;
import dev.charon.perks.player.PerkPlayer;
import dev.charon.perks.player.PerkPlayerInventory;
import dev.charon.perks.plugin.PerkPlugin;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class PerkManager {

    private final List<PerkPlayer> perkPlayers;
    private final HashMap<UUID, PerkPlayerInventory> playerInventory;
    private final Economy economy;

    public PerkManager(Economy economy) {
        perkPlayers = Lists.newArrayList();
        playerInventory = Maps.newHashMap();
        this.economy = economy;
    }

    public PerkPlayer loadPerkPlayer(UUID uuid) {
        ServerConfig serverConfig = new ServerConfig(PerkPlugin.getInstance().getDataFolder(), "perks", uuid.toString(), false);
        JsonObject jsonObject = serverConfig.load();

        if (jsonObject == null) {
            PerkPlayer perkPlayer = createPerkPlayer(uuid);
            jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", uuid.toString());
            jsonObject.add("perks", serverConfig.getGson().toJsonTree(perkPlayer.getPerks()));
            serverConfig.save(jsonObject);
            return perkPlayer;
        }
        if (!jsonObject.has("uuid")) {
            PerkPlayer perkPlayer = createPerkPlayer(uuid);
            jsonObject.addProperty("uuid", uuid.toString());
            jsonObject.add("perks", serverConfig.getGson().toJsonTree(perkPlayer.getPerks()));
            serverConfig.save(jsonObject);
        }

        TypeToken<Map<Integer, Boolean>> mapType = new TypeToken<Map<Integer, Boolean>>(){};
        JsonElement json = jsonObject.get("perks");
        Map<Integer, Boolean> perkMap = serverConfig.getGson().fromJson(json, mapType.getType());
        PerkPlayer perkPlayer = new PerkPlayer(UUID.fromString(jsonObject.get("uuid").getAsString()), (HashMap<Integer, Boolean>) perkMap);
        addPerkPlayer(perkPlayer);
        return perkPlayer;
    }

    public void savePerkPlayer(PerkPlayer perkPlayer) {
        ServerConfig serverConfig = new ServerConfig(PerkPlugin.getInstance().getDataFolder(), "perks", perkPlayer.getUuid().toString(), false);
        JsonObject jsonObject = serverConfig.load();
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        if (!jsonObject.has("uuid")) {
            jsonObject.addProperty("uuid", perkPlayer.getUuid().toString());
        }

        jsonObject.add("perks", serverConfig.getGson().toJsonTree(perkPlayer.getPerks(), HashMap.class));
        serverConfig.save(jsonObject);
    }

    public PerkPlayer createPerkPlayer(UUID uuid) {
        return new PerkPlayer(uuid, new HashMap<>());
    }

    public PerkPlayer getPerkPlayer(UUID uuid) {
        return perkPlayers.stream().filter(perkPlayer -> perkPlayer.getUuid().equals(uuid)).findFirst().orElse(loadPerkPlayer(uuid));
    }

    public void addPerkPlayer(PerkPlayer perkPlayer) {
        if (perkPlayers.contains(perkPlayer)) return;
        perkPlayers.add(perkPlayer);
    }

    public void removePerkPlayer(PerkPlayer perkPlayer) {
        perkPlayers.remove(perkPlayer);
    }

    public boolean hasPerk(UUID uuid, int perk) {
        return getPerkPlayer(uuid).getPerks().containsKey(perk);
    }

    public boolean isEnabled(UUID uuid, Perk perk) {
        return getPerkPlayer(uuid).getPerks().getOrDefault(perk.getId(), false);
    }

    public boolean hasPerks(PerkPlayer perkPlayer) {
        return !perkPlayer.getPerks().isEmpty();
    }

    public void setPerk(UUID uuid, int perk, boolean value) {
        PerkPlayer perkPlayer = getPerkPlayer(uuid);
        perkPlayer.getPerks().put(perk, value);
        savePerkPlayer(perkPlayer);
    }

    public void togglePerk(UUID uuid, int perkId) {
        Perk perk = Perks.getPerkById(perkId);
        if (perk == null) return;

        PerkPlayer perkPlayer = getPerkPlayer(uuid);
        perkPlayer.getPerks().put(perkId, !perkPlayer.getPerks().getOrDefault(perkId, false));

        Player bukkitPlayer = Bukkit.getPlayer(uuid);
        if (bukkitPlayer != null) {
            boolean value = perkPlayer.getPerks().get(perkId);

            if (perk == Perks.FLY.getPerk()) {
                bukkitPlayer.setAllowFlight(false);
                bukkitPlayer.setFlying(false);
            }

            bukkitPlayer.sendMessage(Constants.PREFIX + "Du hast das Perk " + perk.getName()
                    + " §7erfolgreich " + (value ? "§aaktiviert" : "§cdeaktiviert") + "§8.");
        }
        savePerkPlayer(perkPlayer);
    }

    public void removePerk(UUID uuid, int perk) {
        PerkPlayer perkPlayer = getPerkPlayer(uuid);
        perkPlayer.getPerks().remove(perk);
        savePerkPlayer(perkPlayer);
    }

    public void clearPerks(UUID uuid) {
        PerkPlayer perkPlayer = getPerkPlayer(uuid);
        perkPlayer.getPerks().clear();
        savePerkPlayer(perkPlayer);
    }

    public boolean canBuyPerk(UUID uuid, Perk perk) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return economy.getBalance(player) >= perk.getPrice();
    }

    public void buyPerk(Player player, Perk perk) {
        if (!canBuyPerk(player.getUniqueId(), perk)) {
            player.sendMessage(Constants.PREFIX + "§cDu hast nicht genügend Geld.");
            return;
        }
        EconomyResponse economyResponse = economy.withdrawPlayer(player, perk.getPrice());
        if (economyResponse.transactionSuccess()) {
            player.sendMessage(Constants.PREFIX + "§7Du hast dir das Perk " + perk.getName() + " §7für §a" + Maths.asString(perk.getPrice()) + "€ §7gekauft.");
            togglePerk(player.getUniqueId(), perk.getId());
        } else {
            player.sendMessage(Constants.PREFIX + "§cDu hast nicht genügend Geld.");
        }
    }



}
