package com.condor.voidaltars.altar.boons;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.UUID;

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
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;

public class EndBoon extends Boon {

  private static final String NAME = "Blessing of the End";
  private static ArrayList<String> loreList = new ArrayList<>();
  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  private static TreeMap<UUID, Long> cooldownMap = new TreeMap<>();

  // Message cooldown duration
  private static final long MESSAGE_COOLDOWN_DURATION = 40 * 5;

  static {
    loreList.add("Gaze into the void.");
    loreList.add(ChatColor.MAGIC + "123" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "IT GAZES BACK" + ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + "123" + ChatColor.RESET);

    triggerList.add(EntityDamageEvent.class);
  }

  public EndBoon() {
    super(NAME, triggerList, BoonType.END_BOON);
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
