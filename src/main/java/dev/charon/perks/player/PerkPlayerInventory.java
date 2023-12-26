package dev.charon.perks.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@AllArgsConstructor
public class PerkPlayerInventory {

    private ItemStack[] contents;
    private ItemStack[] armorContents;
    private float xp;

}
