package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;

public class ForgeBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

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
