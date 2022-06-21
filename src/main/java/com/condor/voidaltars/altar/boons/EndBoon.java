package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

/**
 * A boon that gives all town members immunity to void damage. Instead,
 * they wake up in their bed (or failing that, their town spawn).
 * Works outside of the town.
 */
public class EndBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static HashMap<UUID, Long> cooldownMap = new HashMap<>();

  // Message cooldown duration
  private static final long MESSAGE_COOLDOWN_DURATION = 40 * 5;

  static {
    triggerList.add(EntityDamageEvent.class);
  }

  public EndBoon() {
    super(StringConstants.END_BLESSING_NAME.get(), StringConstants.END_BLESSING_DESCRIPTION.get(), triggerList, BoonType.END_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.END_BLESSING_NAME.get());
    meta.setLore(StringListConstants.END_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof EntityDamageEvent) {
      EntityDamageEvent ede = (EntityDamageEvent) event;
      Entity entity = ede.getEntity();
      if (entity.getType() == EntityType.PLAYER && ede.getCause() == DamageCause.VOID) {
        Player player = (Player) entity;
        try {
          Town town = TownyUniverse.getInstance().getResident(player.getUniqueId()).getTown();
          if (this.registeredTowns.contains(town)) {
            ret = true;
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
    if (ede.getEntity() instanceof Player) {
      ede.setDamage(0);
      ede.setCancelled(true);
      Player player = (Player) ede.getEntity();
      long currTime = System.currentTimeMillis();
      long lastTimeUsed = 0;
      if (cooldownMap.containsKey(player.getUniqueId())) {
        lastTimeUsed = cooldownMap.get(player.getUniqueId());
      }
      // If it's off cooldown
      if ((currTime - lastTimeUsed) >= MESSAGE_COOLDOWN_DURATION) {
        Location loc = (player.getBedSpawnLocation() != null) ? player.getBedSpawnLocation() : player.getLocation().getWorld().getSpawnLocation();
        player.setFallDistance(0);
        player.teleport(loc);
        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "You wake with memories of falling forever. How strange.");
        cooldownMap.put(player.getUniqueId(), currTime);
      }
    }
  }
}
