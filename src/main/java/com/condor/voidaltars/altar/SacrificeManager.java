package com.condor.voidaltars.altar;

import java.lang.Long;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

import com.palmergames.bukkit.towny.object.Town;

public class SacrificeManager {
  // Long: The time sacrificed | Sacrifice: The sacrifice

  // Sacrifices whose quota may be empty or in progress and are thus not completed
  private static HashMap<Long, Sacrifice> currentSacrifices = new HashMap<>();

  // Sacrifices whose quotas have already been satisfied and are thus completed
  private static HashMap<Long, Sacrifice> previousSacrifices = new HashMap<>();

  private static Random rng = new Random();

  private final static int SCALE = 500;
  private final static int STACK_SIZE_FACTOR = 16;
  private final static int RANDOM_ADDITIONAL_FACTOR = 10;

  public static Sacrifice getNewSacrifice(AltarMeta owner) {
    Material type = owner.getSacrificeType();
    double weight = owner.getSacrificeWeight(type);
    Town town = owner.getTown();
    ItemStack is = new ItemStack(type);
    int amt = calculateSacrificeAmount(weight, town, is.getMaxStackSize());
    Sacrifice sacrifice = new Sacrifice(type, amt, owner);
    return sacrifice;
  }

  public static int calculateSacrificeAmount(double weight, Town town, int maxStackAmount) {
    int amt = 0;

    if (weight == 0) {
      weight = 1;
    }

    double randVal = 1 + (rng.nextDouble() / 2);

    // amt = (int) (500 * ((0.5 * (1 / weight)) + (0.5 * town.getNumResidents())));
    // amt = (int) Math.floor(town.getNumResidents() * (SCALE * ((0.7 * (1 / weight))) * (maxStackAmount / 16)) + (randVal * maxStackAmount / 6.4));
    amt = (int) (Math.max(1, (int) Math.floor(SCALE * town.getNumResidents() * ((0.7 * 1 / weight)) * maxStackAmount / STACK_SIZE_FACTOR)) * randVal);

    if (amt <= 0) {
      amt = 1;
    }

    return amt;
  }
}
