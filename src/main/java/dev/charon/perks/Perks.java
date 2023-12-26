package dev.charon.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@AllArgsConstructor
public enum Perks {

    FLY(new Perk(1, "§eFly", 500000*4*3), Material.FEATHER, List.of("§7Fliege in der Plot-Welt")), //DONE
    HASTE(new Perk(2, "§eHaste", 300000*4*3), Material.DIAMOND_PICKAXE, List.of("§7Baue Blöcke schneller ab!")),//DONE
    SPEED(new Perk(3, "§eSpeed", 300000*4*3), Material.SUGAR, List.of("§7Renn schneller als andere")),//DONE
    NO_HUNGER(new Perk(4, "§eNo Hunger", 200000*4*3), Material.COOKED_BEEF, List.of("§7Werde niemals hungrig")),//DONE
    NO_FALL_DAMAGE(new Perk(5, "§eNo Fall Damage", 400000*4*3), Material.FEATHER, List.of("§7Bekomme nie wieder Fall-Schaden")),//DONE
    AUTO_SMELT(new Perk(6, "§eAuto Smelt", 350000*4*3), Material.FURNACE, List.of("§7Schmelzt automatisch Erze")),//DONE
    TELEKINESIS(new Perk(7, "§eTelekinesis", 400000*4*3), Material.ENDER_PEARL, List.of("§7Hebt automatisch Items auf, die man abbaut")),//DONE
    ITEMS_AFTER_DEATH(new Perk(8, "§eItems After Death", 500000*4*3), Material.CHEST, List.of("§7Behalte dein geliebtes Inventar nach dem Tod")), //DONE
    XP_AFTER_DEATH(new Perk(9, "§eXP After Death", 500000*4*3), Material.EXPERIENCE_BOTTLE, List.of("§7Behalte deine geliebten XP nach dem Tod")); //DONE

    private final Perk perk;
    private final Material material;
    private final List<String> description;

    public static Perk getPerkById(int id) {
        for (Perks perks : values()) {
            if (perks.getPerk().getId() == id) {
                return perks.getPerk();
            }
        }
        return null;
    }

    public static Perk getPerkByName(String name) {
        for (Perks perks : values()) {
            if (ChatColor.stripColor(perks.getPerk().getName()).equalsIgnoreCase(name)) {
                return perks.getPerk();
            }
        }
        return null;
    }

    public static Perk getPerkByMaterial(Material material) {
        for (Perks perks : values()) {
            if (perks.getMaterial() == material) {
                return perks.getPerk();
            }
        }
        return null;
    }

}
