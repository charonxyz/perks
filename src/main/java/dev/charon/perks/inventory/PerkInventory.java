package dev.charon.perks.inventory;

import dev.charon.inventory.gui.listener.ItemListener;
import dev.charon.inventory.gui.menu.Gui;
import dev.charon.lib.util.ItemBuilder;
import dev.charon.lib.util.Maths;
import dev.charon.perks.Constants;
import dev.charon.perks.Perk;
import dev.charon.perks.Perks;
import dev.charon.perks.manager.PerkManager;
import dev.charon.perks.player.PerkPlayer;
import dev.charon.perks.plugin.PerkPlugin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class PerkInventory extends Gui {

    private final Player player;
    private final PerkPlayer perkPlayer;

    private final PerkManager perkManager = PerkPlugin.getInstance().getPerkManager();

    public PerkInventory(Player player) {
        super(9 * 4, Constants.INV_TITLE);

        this.player = player;

        fillBorder(BACKGROUND);

        int slot = 10;

        perkPlayer = perkManager.getPerkPlayer(player.getUniqueId());

        for (Perks perks : Perks.values()) {

            List<String> lore = new ArrayList<>(perks.getDescription());
            lore.add(" ");
            if (!perkPlayer.getPerks().containsKey(perks.getPerk().getId())) {
                lore.add("§7Kosten§8: §e" + Maths.asString(perks.getPerk().getPrice()));
                lore.add(" ");
                lore.add("§7§oKlicke um dieses Perk zu kaufen");
            } else {
                lore.add("§7§oKlicke um dieses Perk zu " + (perkPlayer.getPerks().get(perks.getPerk().getId()) ? "§cdeaktivieren" : "§aaktivieren"));
            }

            setItem(slot, new ItemBuilder(perks.getMaterial())
                    .name(perks.getPerk().getName())
                    .lore(lore)
                    .build(), new ItemListener() {
                @Override
                public void onClick(Player player, int slot, ItemStack itemStack, ClickType clickType) {
                    if (!perkManager.hasPerk(player.getUniqueId(), perks.getPerk().getId())) {
                        buyPerk(perks.getPerk());
                    } else tooglePerk(perks.getPerk());

                    if (getPerkManager().hasPerk(player.getUniqueId(), perks.getPerk().getId())) {
                        List<String> lore = new ArrayList<>(perks.getDescription());
                        lore.add(" ");
                        lore.add("§7§oKlicke um dieses Perk zu " + (perkPlayer.getPerks().get(perks.getPerk().getId()) ? "§cdeaktivieren" : "§aaktivieren"));
                        updateItem(slot, new ItemBuilder(perks.getMaterial())
                                .name(perks.getPerk().getName())
                                .lore(lore)
                                .build());
                    }
                }
            });

            slot++;
            if (slot > 16 && slot < 19) {
                slot = 19;
            } else if (slot > 25 && slot < 28) {
                slot = 28;
            }
        }
        show(player);
    }

    private void buyPerk(Perk perk) {
        perkManager.buyPerk(player, perk);
    }

    private void tooglePerk(Perk perk) {
        perkManager.togglePerk(player.getUniqueId(), perk.getId());
    }
}
