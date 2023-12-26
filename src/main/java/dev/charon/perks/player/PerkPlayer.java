package dev.charon.perks.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
@AllArgsConstructor
public class PerkPlayer {

    private final UUID uuid;
    private HashMap<Integer, Boolean> perks;

}
