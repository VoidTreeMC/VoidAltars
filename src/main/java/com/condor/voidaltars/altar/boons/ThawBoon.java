package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.object.Town;

/**
 * A boon that makes it so water never freezes
 * and snow never falls while inside of the town
 */
public class ThawBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  static {
    triggerList.add(BlockFormEvent.class);
  }

  public ThawBoon() {
    super(StringConstants.THAW_BLESSING_NAME.get(), StringConstants.THAW_BLESSING_DESCRIPTION.get(), triggerList, BoonType.THAW_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.THAW_BLESSING_NAME.get());
    meta.setLore(StringListConstants.THAW_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof BlockFormEvent) {
      BlockFormEvent bfe = (BlockFormEvent) event;
      if (FreezeBoon.isIce(bfe.getNewState().getBlockData().getMaterial())) {
        Location loc = bfe.getBlock().getLocation();
        Town town = TownyFunctions.getTownFromLocation(loc);
        ret = this.registeredTowns.contains(town);
      }
    }

    return ret;
  }

  public void execute(Event event) {
    BlockFormEvent bfe = (BlockFormEvent) event;
    bfe.setCancelled(true);
  }
}
