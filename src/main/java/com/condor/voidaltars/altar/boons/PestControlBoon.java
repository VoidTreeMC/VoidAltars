package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.object.Town;

/**
 * A boon that prevents cats, iron golems,
 * and other undesirable creatures from spawning
 * in the town
 */
public class PestControlBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  static {
    triggerList.add(CreatureSpawnEvent.class);
  }

  public PestControlBoon() {
    super(StringConstants.PEST_CONTROL_BLESSING_NAME.get(), StringConstants.PEST_CONTROL_BLESSING_DESCRIPTION.get(), triggerList, BoonType.ANTIPEST_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.PEST_CONTROL_BLESSING_NAME.get());
    meta.setLore(StringListConstants.PEST_CONTROL_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof CreatureSpawnEvent) {
      CreatureSpawnEvent cse = (CreatureSpawnEvent) event;
      ret = cse.getSpawnReason() == SpawnReason.DEFAULT || cse.getSpawnReason() == SpawnReason.VILLAGE_DEFENSE;
      ret = ret && isPest(cse.getEntityType());
      if (ret) {
        Location loc = cse.getEntity().getLocation();
        Town town = TownyFunctions.getTownFromLocation(loc);
        ret = this.registeredTowns.contains(town);
      }
    }

    return ret;
  }

  public void execute(Event event) {
    CreatureSpawnEvent cse = (CreatureSpawnEvent) event;
    cse.setCancelled(true);
  }

  public boolean isPest(EntityType type) {
    switch (type) {
      case IRON_GOLEM:
      case CAT:
        return true;
      default:
        return false;
    }
  }

  public boolean isPredator(EntityType type) {
    switch (type) {
      case FOX:
      case CAT:
      case WOLF:
      case AXOLOTL:
      case SQUID:
        return true;
      default:
        return false;
    }
  }
}
