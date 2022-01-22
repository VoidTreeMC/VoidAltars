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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.util.TownyFunctions;

/**
 * A boon that makes it so all creatures inside of the town
 * are immune to fire damage, lava damage, and hot floors.
 */
public class NetherBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static final int EFFECT_DURATION = 20 * 10;

  private static Random rng = new Random();

  static {
    triggerList.add(EntityDamageEvent.class);
  }

  public NetherBoon() {
    super(StringConstants.NETHER_BLESSING_NAME.get(), StringConstants.NETHER_BLESSING_DESCRIPTION.get(), triggerList, BoonType.NETHER_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.NETHER_BLESSING_NAME.get());
    meta.setLore(StringListConstants.NETHER_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof EntityDamageEvent) {
      EntityDamageEvent ede = (EntityDamageEvent) event;
      Location loc = ede.getEntity().getLocation();
      Town town = TownyFunctions.getTownFromLocation(loc);
      if (this.registeredTowns.contains(town)) {
        if (ede.getCause() == DamageCause.LAVA || ede.getCause() == DamageCause.FIRE ||
            ede.getCause() == DamageCause.FIRE_TICK || ede.getCause() == DamageCause.HOT_FLOOR) {
          ret = true;
        }
      }
    }

    return ret;
  }

  public void execute(Event event) {
    EntityDamageEvent ede = (EntityDamageEvent) event;
    if (ede.getEntity() instanceof LivingEntity) {
      LivingEntity le = (LivingEntity) ede.getEntity();
      ede.setDamage(0);
      ede.setCancelled(true);
      PotionEffect fireRes = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, EFFECT_DURATION, 0, true, false, false);
      le.addPotionEffect(fireRes);
    }
  }
}
