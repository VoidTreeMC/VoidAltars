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
import org.bukkit.event.block.BlockFadeEvent;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;

public class FreezeBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  static {
    triggerList.add(BlockFadeEvent.class);
  }

  public FreezeBoon() {
    super(StringConstants.FREEZE_BLESSING_NAME.get(), StringConstants.FREEZE_BLESSING_DESCRIPTION.get(), triggerList, BoonType.FREEZE_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.FREEZE_BLESSING_NAME.get());
    meta.setLore(StringListConstants.FREEZE_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof BlockFadeEvent) {
      BlockFadeEvent bfe = (BlockFadeEvent) event;
      if (isIce(bfe.getBlock().getType())) {
        Location loc = bfe.getBlock().getLocation();
        TownBlock tb = TownyAPI.getInstance().getTownBlock(loc);
        if (tb != null) {
          try {
            Town town = tb.getTown();
            ret = this.registeredTowns.contains(town);
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
    BlockFadeEvent bfe = (BlockFadeEvent) event;
    bfe.setCancelled(true);
  }

  public static boolean isIce(Material mat) {
    // Not sure if all of these can melt, but it doesn't hurt to add them
    switch (mat) {
      case ICE:
      case BLUE_ICE:
      case FROSTED_ICE:
      case PACKED_ICE:
      case POWDER_SNOW:
      case SNOW:
      case SNOW_BLOCK:
      case POWDER_SNOW_CAULDRON:
        return true;
      default:
        return false;
    }
  }
}
