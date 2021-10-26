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

public class NetherBoon extends Boon {

  private static final String NAME = "Blessing of the Nether";
  private static ArrayList<String> loreList = new ArrayList<>();
  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static final int EFFECT_DURATION = 20 * 10;

  private static final String DESCRIPTION = "The gods protect everyone in your town from the flame. No players or mobs in your town will take damage from fire, lava or hot floors.";

  private static Random rng = new Random();

  static {
    loreList.add("Turn your back on the Overworld.");
    loreList.add(ChatColor.RED + "" + ChatColor.BOLD + "Welcome the fire.");

    triggerList.add(EntityDamageEvent.class);
  }

  public NetherBoon() {
    super(NAME, DESCRIPTION, triggerList, BoonType.NETHER_BOON);
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

    if (event instanceof EntityDamageEvent) {
      EntityDamageEvent ede = (EntityDamageEvent) event;
      Location loc = ede.getEntity().getLocation();
      TownBlock tb = TownyAPI.getInstance().getTownBlock(loc);
      if (tb != null) {
        try {
          Town town = tb.getTown();
          if (this.registeredTowns.contains(town)) {
            if (ede.getCause() == DamageCause.LAVA || ede.getCause() == DamageCause.FIRE ||
                ede.getCause() == DamageCause.FIRE_TICK || ede.getCause() == DamageCause.HOT_FLOOR) {
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
