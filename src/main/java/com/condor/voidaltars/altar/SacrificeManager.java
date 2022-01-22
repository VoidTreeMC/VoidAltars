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

/**
 * Utility class for getting new sacrifices
 * for a specific altar, and calculating the correct
 * amount to demand for a sacrifice
 */
public class SacrificeManager {

  private static Random rng = new Random();

  // The global scaling factor for all sacrifices
  private final static int SCALE = 100;
  // The scaling factor that penalizes sacrifice amounts for items with smaller stack caps
  private final static int STACK_SIZE_FACTOR = 16;

  /**
   * Gets a new sacrifice for the specified altar
   * @param  owner               The altar that wants a new sacrifice
   * @return                     The new sacrifice
   */
  public static Sacrifice getNewSacrifice(AltarMeta owner) {
    Material type = owner.getSacrificeType();
    double weight = owner.getSacrificeWeight(type);
    Town town = owner.getTown();
    ItemStack is = new ItemStack(type);
    int amt = calculateSacrificeAmount(weight, town, is.getMaxStackSize());
    Sacrifice sacrifice = new Sacrifice(type, amt, owner);
    return sacrifice;
  }

  /**
   * Calculates the amount to demand for a specific sacrifice
   * @param  weight                       The weight of the item
   * @param  town                         The town for whom the sacrifice is being generated
   * @param  maxStackAmount               The maximum amount of items that a stack of the item can hold
   * @return                              The amount to demand for the sacrifice
   */
  public static int calculateSacrificeAmount(double weight, Town town, int maxStackAmount) {
    int amt = 0;

    if (weight == 0) {
      weight = 1;
    }

    double randVal = 1 + (rng.nextDouble() / 2);

    // amt = (int) (500 * ((0.5 * (1 / weight)) + (0.5 * town.getNumResidents())));
    // amt = (int) Math.floor(town.getNumResidents() * (SCALE * ((0.7 * (1 / weight))) * (maxStackAmount / 16)) + (randVal * maxStackAmount / 6.4));
    int numResidents = town.getNumResidents();
    if (numResidents > 1) {
      amt = (int) (Math.max(1, (int) Math.floor(SCALE * numResidents * ((0.5 * 1 / weight)) * maxStackAmount / STACK_SIZE_FACTOR)) * randVal);
    } else {
      amt = (int) (Math.max(1, (int) Math.floor(SCALE * (numResidents * 0.75) * ((0.7 * 1 / weight)) * maxStackAmount / STACK_SIZE_FACTOR)) * randVal);
    }

    if (amt <= 0) {
      amt = 1;
    }

    return amt;
  }
}
