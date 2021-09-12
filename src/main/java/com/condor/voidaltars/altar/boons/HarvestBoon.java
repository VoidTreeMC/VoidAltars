package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;

public class HarvestBoon extends Boon {

  private static final String NAME = "Blessing of the Harvest";
  private static ArrayList<String> loreList = new ArrayList<>();
  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  static {
    loreList.add("Additional crops from farming");

    triggerList.add(BlockBreakEvent.class);
  }

  public HarvestBoon() {
    super(NAME, triggerList, BoonType.HARVEST_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(NAME);
    meta.setLore(loreList);
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    // TODO: Method stub

    return ret;
  }

  public void execute(Event event) {
    // TODO: Method stub
  }
}
