package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.object.Town;

import io.papermc.paper.event.block.PlayerShearBlockEvent;

/**
 * A boon that makes bees in a town passive, adds a chance
 * for two bees to be spawned from breeding, and adds a chance
 * for extra bee products when harvested by hand
 */
public class BeekeeperBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();
  private static final double DOUBLE_BEE_CHANCE = 0.25;
  private static final double EXTRA_DROPS_CHANCE = 0.25;

  static {
    triggerList.add(EntityTargetLivingEntityEvent.class);
    triggerList.add(EntityBreedEvent.class);
    triggerList.add(PlayerShearBlockEvent.class);
    triggerList.add(PlayerInteractEvent.class);
  }

  public BeekeeperBoon() {
    super(StringConstants.BEEKEEPER_BLESSING_NAME.get(), StringConstants.BEEKEEPER_BLESSING_DESCRIPTION.get(), triggerList, BoonType.BEEKEEPER_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.BEEKEEPER_BLESSING_NAME.get());
    meta.setLore(StringListConstants.BEEKEEPER_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;
    Location loc = null;
    if (event instanceof EntityTargetLivingEntityEvent) {
      EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
      if (etlee.getEntity().getType() != EntityType.BEE) {
        ret = false;
      } else if (etlee.getTarget() != null && etlee.getReason() != TargetReason.TEMPT) {
        loc = etlee.getTarget().getLocation();
        ret = true;
      }
    } else if (event instanceof EntityBreedEvent) {
      EntityBreedEvent ebe = (EntityBreedEvent) event;
      if (ebe.getEntity().getType() != EntityType.BEE) {
        ret = false;
      } else {
        loc = ebe.getEntity().getLocation();
        ret = true;
      }
    } else if (event instanceof PlayerShearBlockEvent) {
      PlayerShearBlockEvent psbe = (PlayerShearBlockEvent) event;
      Material blockType = psbe.getBlock().getType();
      if (blockType == Material.BEEHIVE || blockType == Material.BEE_NEST) {
        loc = psbe.getBlock().getLocation();
        ret = true;
      }
    } else if (event instanceof PlayerInteractEvent) {
      PlayerInteractEvent pie = (PlayerInteractEvent) event;
      if (pie.hasBlock() && pie.getMaterial() == Material.GLASS_BOTTLE) {
        Block block = pie.getClickedBlock();
        if (block.getType() == Material.BEEHIVE || block.getType() == Material.BEE_NEST) {
          Beehive hive = (Beehive) block.getBlockData();
          if (hive.getHoneyLevel() == hive.getMaximumHoneyLevel()) {
            ret = true;
            loc = block.getLocation();
          }
        }
      }
    }

    if (loc != null) {
      Town town = TownyFunctions.getTownFromLocation(loc);
      ret = ret && this.registeredTowns.contains(town);
    }

    return ret;
  }

  public void execute(Event event) {
    if (event instanceof EntityTargetLivingEntityEvent) {
      EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
      etlee.setCancelled(true);
    } else if (event instanceof EntityBreedEvent) {
      EntityBreedEvent ebe = (EntityBreedEvent) event;
      if (rng.nextDouble() < DOUBLE_BEE_CHANCE) {
        Location loc = ebe.getEntity().getLocation();
        Bee bee = (Bee) loc.getWorld().spawnEntity(loc, EntityType.BEE);
        bee.setBaby();
      }
    } else if (event instanceof PlayerShearBlockEvent) {
      PlayerShearBlockEvent psbe = (PlayerShearBlockEvent) event;
      if (rng.nextDouble() < EXTRA_DROPS_CHANCE) {
        List<ItemStack> drops = psbe.getDrops();
        for (ItemStack is : drops) {
          if (is.getType() == Material.HONEYCOMB || is.getType() == Material.HONEY_BOTTLE){
            is.setAmount(is.getAmount() + 1);
          }
        }
      }
    } else if (event instanceof PlayerInteractEvent) {
      PlayerInteractEvent pie = (PlayerInteractEvent) event;
      Player player = pie.getPlayer();
      if (rng.nextDouble() < EXTRA_DROPS_CHANCE) {
        int amtBottles = 0;
        for (ItemStack item : player.getInventory().getContents()) {
          if (item != null && item.getType() == Material.GLASS_BOTTLE) {
            amtBottles += item.getAmount();
          }
        }
        if (amtBottles >= 2) {
          pie.getPlayer().getInventory().addItem(new ItemStack(Material.HONEY_BOTTLE));
          for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GLASS_BOTTLE) {
              item.setAmount(item.getAmount() - 1);
              break;
            }
          }
        }
      }
    }
  }
}
