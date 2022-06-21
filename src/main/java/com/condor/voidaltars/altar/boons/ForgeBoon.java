package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.object.Town;

/**
 * A boon that makes items inside of furnaces smelt faster,
 * thus saving time and fuel.
 */
public class ForgeBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static final double MULTIPLIER = 0.90;

  static {
    triggerList.add(FurnaceStartSmeltEvent.class);
  }

  public ForgeBoon() {
    super(StringConstants.FORGE_BLESSING_NAME.get(), StringConstants.FORGE_BLESSING_DESCRIPTION.get(), triggerList, BoonType.FORGE_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.FORGE_BLESSING_NAME.get());
    meta.setLore(StringListConstants.FORGE_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof FurnaceStartSmeltEvent) {
      FurnaceStartSmeltEvent fsse = (FurnaceStartSmeltEvent) event;
      Location loc = fsse.getBlock().getLocation();
      Town town = TownyFunctions.getTownFromLocation(loc);
      ret = this.registeredTowns.contains(town);
    }

    return ret;
  }

  public void execute(Event event) {
    FurnaceStartSmeltEvent fsse = (FurnaceStartSmeltEvent) event;
    fsse.setTotalCookTime((int) (MULTIPLIER * fsse.getTotalCookTime()));
  }
}
