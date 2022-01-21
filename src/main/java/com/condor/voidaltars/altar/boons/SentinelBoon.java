package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;

public class SentinelBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  static {
    triggerList.add(EntityTargetLivingEntityEvent.class);
  }

  public SentinelBoon() {
    super(StringConstants.SENTINEL_BLESSING_NAME.get(), StringConstants.SENTINEL_BLESSING_DESCRIPTION.get(), triggerList, BoonType.SENTINEL_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.SENTINEL_BLESSING_NAME.get());
    meta.setLore(StringListConstants.SENTINEL_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof EntityTargetLivingEntityEvent) {
      EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
      if (etlee.getTarget() != null) {
        Location loc = etlee.getTarget().getLocation();
        Town town = TownyFunctions.getTownFromLocation(loc);
        if (this.registeredTowns.contains(town)) {
          if (isPrey(etlee.getTarget().getType()) && isPredator(etlee.getEntity().getType())) {
            ret = true;
          }
        }
      }
    }

    return ret;
  }

  public void execute(Event event) {
    EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
    etlee.setCancelled(true);
  }

  public boolean isPrey(EntityType type) {
    switch (type) {
      case CHICKEN:
      case RABBIT:
      case SHEEP:
      case TURTLE:
      case COD:
      case SALMON:
      case TROPICAL_FISH:
      case GLOW_SQUID:
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
