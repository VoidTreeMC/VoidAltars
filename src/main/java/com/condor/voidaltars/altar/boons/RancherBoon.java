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

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;

public class RancherBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  static {
    triggerList.add(EntityDeathEvent.class);
  }

  public RancherBoon() {
    super(StringConstants.RANCHER_BLESSING_NAME.get(), StringConstants.RANCHER_BLESSING_DESCRIPTION.get(), triggerList, BoonType.RANCHER_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.RANCHER_BLESSING_NAME.get());
    meta.setLore(StringListConstants.RANCHER_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof EntityDeathEvent) {
      EntityDeathEvent ede = (EntityDeathEvent) event;
      Location loc = ede.getEntity().getLocation();
      Town town = TownyFunctions.getTownFromLocation(loc);
      if (this.registeredTowns.contains(town)) {
        if (isLivestock(ede.getEntity().getType())) {
          ret = true;
        }
      }
    }

    return ret;
  }

  public void execute(Event event) {
    EntityDeathEvent ede = (EntityDeathEvent) event;
    for (ItemStack itemStack : ede.getDrops()) {
      int maxAmt = itemStack.getMaxStackSize();
      int amt = itemStack.getAmount() + (rng.nextInt(3) + 1);
      amt = (amt > maxAmt) ? maxAmt : amt;
      itemStack.setAmount(amt);
    }
  }

  public boolean isLivestock(EntityType type) {
    switch (type) {
      case BEE:
      case CAT:
      case COW:
      case CHICKEN:
      case DONKEY:
      case FOX:
      case GOAT:
      case HOGLIN:
      case HORSE:
      case LLAMA:
      case MULE:
      case MUSHROOM_COW:
      case OCELOT:
      case PANDA:
      case PARROT:
      case PIG:
      case POLAR_BEAR:
      case RABBIT:
      case SHEEP:
      case TRADER_LLAMA:
      case TURTLE:
      case WOLF:
      case ZOGLIN:
        return true;
      default:
        return false;
    }
  }
}
