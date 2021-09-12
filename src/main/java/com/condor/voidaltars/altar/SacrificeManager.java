package com.condor.voidaltars.altar;

import java.lang.Long;
import java.util.TreeMap;
import java.util.ArrayList;

import org.bukkit.Material;

import com.palmergames.bukkit.towny.object.Town;

public class SacrificeManager {
  // Long: The time sacrificed | Sacrifice: The sacrifice

  // Sacrifices whose quota may be empty or in progress and are thus not completed
  private static TreeMap<Long, Sacrifice> currentSacrifices = new TreeMap<>();

  // Sacrifices whose quotas have already been satisfied and are thus completed
  private static TreeMap<Long, Sacrifice> previousSacrifices = new TreeMap<>();


  public static Sacrifice getNewSacrifice(AltarMeta owner) {
    Material type = owner.getSacrificeType();
    double weight = owner.getSacrificeWeight(type);
    Town town = owner.getTown();
    int amt = calculateSacrificeAmount(weight, town);
    Sacrifice sacrifice = new Sacrifice(type, amt, owner);
    return sacrifice;
  }

  public static int calculateSacrificeAmount(double weight, Town town) {
    int amt = 0;

    if (weight == 0) {
      weight = 1;
    }

    amt = (int) (500 * ((0.5 * (1 / weight)) + (0.5 * town.getNumResidents())));

    if (amt <= 0) {
      amt = 1;
    }

    return amt;
  }
}
