package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.object.Town;

public class HarvestBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();
  private static final double CHANCE = 0.3;

  static {
    triggerList.add(BlockGrowEvent.class);
  }

  public HarvestBoon() {
    super(StringConstants.HARVEST_BLESSING_NAME.get(), StringConstants.HARVEST_BLESSING_DESCRIPTION.get(), triggerList, BoonType.HARVEST_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.HARVEST_BLESSING_NAME.get());
    meta.setLore(StringListConstants.HARVEST_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;
    if (event instanceof BlockGrowEvent) {
      BlockGrowEvent bge = (BlockGrowEvent) event;
      Block block = bge.getBlock();
      Location loc = block.getLocation();
      ret = isCrop(block.getType()) || isTall(loc.add(0, -1, 0).getBlock().getType());
      Town town = TownyFunctions.getTownFromLocation(loc);
      ret = ret && this.registeredTowns.contains(town);
    }

    return ret;
  }

  public void execute(Event event) {
    BlockGrowEvent bge = (BlockGrowEvent) event;
    BlockState state = bge.getNewState();
    BlockData data = state.getBlockData();
    Location loc = bge.getBlock().getLocation();
    if (data instanceof Ageable) {
      Ageable ageData = (Ageable) data;
      int age = ageData.getAge();
      int maxAge = ageData.getMaximumAge();
      if (age < maxAge && (rng.nextDouble() <= CHANCE)) {
        ageData.setAge(age + 1);
        state.setBlockData(ageData);
      }
    }
    Block blockBelow = loc.clone().add(0, -1, 0).getBlock();
    Block blockAbove = loc.clone().add(0, 1, 0).getBlock();
    if (isTall(blockBelow.getType()) && (rng.nextDouble() <= CHANCE)) {
      if (blockAbove.getType() == Material.AIR) {
        blockAbove.setType(blockBelow.getType());
      }
    }
  }

  public boolean isTall(Material mat) {
    switch (mat) {
      case CACTUS:
      case SUGAR_CANE:
      case BAMBOO:
      case KELP:
        return true;
      default:
        return false;
    }
  }

  public boolean isCrop(Material mat) {
    switch (mat) {
      case WHEAT:
      case BEETROOTS:
      case CARROTS:
      case POTATOES:
      case MELON_STEM:
      case ATTACHED_MELON_STEM:
      case PUMPKIN_STEM:
      case ATTACHED_PUMPKIN_STEM:
      case SUGAR_CANE:
      case BAMBOO:
      case COCOA_BEANS:
      case CACTUS:
      case KELP:
      case NETHER_WART:
        return true;
      default:
        return false;
    }
  }
}
