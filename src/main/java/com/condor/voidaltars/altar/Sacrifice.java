package com.condor.voidaltars.altar;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Sacrifice {
  Material type;
  int sacrificed;
  int total;
  AltarMeta owner;

  public Sacrifice(Material type, int total, AltarMeta owner) {
    this.type = type;
    this.total = total;
    this.owner = owner;
  }

  /**
   * Handles a sacrifice
   * @param  sacrifice An ItemStack representing the items being sacrificed
   * @return The number of sacrifices remaining in the quota. If negative, the quota has been met.
   */
  public int handleSacrifice(ItemStack sacrifice) {
    int count = sacrifice.getAmount();
    this.sacrificed += count;
    return this.total - this.sacrificed;
  }

  public int getNumRemaining() {
    return total - sacrificed;
  }

  public int getTotal() {
    return total;
  }

  public int getNumSacrificed() {
    return sacrificed;
  }


  public Material getType() {
    return this.type;
  }

  public AltarMeta getOwner() {
    return this.owner;
  }
}
