package com.condor.voidaltars.altar;

import java.util.ArrayList;

import org.bukkit.Material;

public class Sacrifice {
  Material type;
  int sacrificed;
  int total;

  /**
   * Handles a sacrifice
   * @param  sacrifice An ItemStack representing the items being sacrificed
   * @return           The number of sacrifices remaining in the quota. If negative, the quota has been met.
   */
  public int handleSacrifice(ItemStack sacrifice) {
    int count = sacrifice.getAmount();
    this.sacrificed += count;
    return this.total - this.sacrificed;
  }

  public Sacrifice(Material type, int total) {
    this.type = type;
    this.total = total;
  }

  public Material getType() {
    return this.type;
  }
}
