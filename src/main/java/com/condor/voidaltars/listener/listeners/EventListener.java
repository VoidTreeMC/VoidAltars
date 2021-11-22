package com.condor.voidaltars.listener.listeners;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Phantom;
import org.bukkit.Statistic;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.SoundCategory;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

import com.condor.voidaltars.listener.AltarListener;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.multiblock.structures.FarmAltarStructure;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.gui.MainAltarGUI;
import com.condor.voidaltars.altar.BoonManager;
import com.condor.voidaltars.constants.StringConstants;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Resident;

/**
 *
 * Listens for Minecraft Events
 *
 * @author iron-condor
 */
public class EventListener  extends AltarListener {
  private static final Random rng = new Random();

  @EventHandler
  public void onTownUnclaimEvent(TownUnclaimEvent event) {
    Town town = event.getTown();
    AltarMeta altar = AltarManager.getAltarFromTown(town);
    // If they don't have an altar, we don't care
    if (altar == null) {
      return;
    }
    // If they *do* have an altar, check if it's in the chunk that was just unclaimed
    Location altarLoc = altar.getLocation();
    TownBlock tb = TownyAPI.getInstance().getTownBlock(altarLoc);
    // If the altar's chunk is no longer a town block
    if (tb == null) {
      // Send a message to all town members telling them their altar was just unclaimed
      for (Resident resident : town.getResidents()) {
        Player player = resident.getPlayer();
        if (player != null) {
          player.sendMessage(StringConstants.TOWN_HAS_UNCLAIMED_ALTAR);
        }
      }
      // Disable the altar's boons
      altar.clearBoons();
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    if (AltarStructure.isPossibleInterfaceBlock(event.getClickedBlock().getType())) {
      Location loc = event.getClickedBlock().getLocation();
      AltarMeta altarMeta = AltarManager.getAltarFromLoc(loc, event.getPlayer());
      if (altarMeta != null) {
        // TODO: Check access here
        MainAltarGUI.displayAltarGUI(event.getPlayer(), altarMeta);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent event) {
    Block block = event.getBlock();
    if (AltarStructure.isPossibleInterfaceBlock(block.getType())) {
      AltarMeta altarMeta = AltarManager.getAltarFromLoc(block.getLocation(), event.getPlayer());
      if (altarMeta != null) {
        // TODO: Check access here
        event.getPlayer().sendMessage("If we had permissions implemented yet, you'd get a message here and the block wouldn't be broken.");
      }
    }
  }

  @EventHandler
  public void onPlayerHarvestBlockEvent(PlayerHarvestBlockEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onEntityDeathEvent(EntityDeathEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onEntityDamageEvent(EntityDamageEvent event) {
    BoonManager.parseEvent(event);
  }
}
