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

public class SentinelBoon extends Boon {

  private static final String NAME = "Blessing of the Sentinel";
  private static ArrayList<String> loreList = new ArrayList<>();
  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  private static final String DESCRIPTION = "The gods protect your small livestock. Foxes and cats are friendly to chickens and rabbits in your town and do not eat them.";

  static {
    loreList.add("When the dog is awake, the shepherd may sleep.");

    triggerList.add(EntityTargetLivingEntityEvent.class);
  }

  public SentinelBoon() {
    super(NAME, DESCRIPTION, triggerList, BoonType.SENTINEL_BOON);
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

    if (event instanceof EntityTargetLivingEntityEvent) {
      EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
      if (etlee.getTarget() != null) {
        Location loc = etlee.getTarget().getLocation();
        TownBlock tb = TownyAPI.getInstance().getTownBlock(loc);
        if (tb != null) {
          try {
            Town town = tb.getTown();
            if (this.registeredTowns.contains(town)) {
              if (isChickenOrRabbit(etlee.getTarget().getType()) && isFoxOrCat(etlee.getEntity().getType())) {
                ret = true;
              }
            }
          } catch (NotRegisteredException e) {
            ret = false;
          }
        } else {
          ret = false;
        }
      }
    }

    return ret;
  }

  public void execute(Event event) {
    EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
    etlee.setCancelled(true);
  }

  public boolean isChickenOrRabbit(EntityType type) {
    switch (type) {
      case CHICKEN:
      case RABBIT:
        return true;
      default:
        return false;
    }
  }

  public boolean isFoxOrCat(EntityType type) {
    switch (type) {
      case FOX:
      case CAT:
        return true;
      default:
        return false;
    }
  }
}
