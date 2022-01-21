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
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;

import com.condor.voidaltars.listener.AltarListener;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.multiblock.structures.FarmAltarStructure;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.gui.MainAltarGUI;
import com.condor.voidaltars.altar.BoonManager;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.transaction.DestroyTransaction;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;

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
    TownAltarLink altarLink = AltarManager.getAltarLinkFromTown(town);
    // If they don't have any altars, we don't care
    if (altarLink == null) {
      return;
    }
    altarLink.handleChunkUnclaim();
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    BoonManager.parseEvent(event);
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
        Player player = event.getPlayer();
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        if (resident == null || (!resident.isMayor() && !resident.hasTownRank("high-priest") && !player.hasPermission("condor.altar.destroy"))) {
          event.getPlayer().sendMessage(StringConstants.NO_PERMISSIONS_TO_BREAK_ALTAR.get());
          event.setCancelled(true);
        } else {
          Location loc = block.getLocation();
          DestroyTransaction transac = new DestroyTransaction(altarMeta.getUniqueId(), altarMeta.getLink(), player.getUniqueId(),
                                                              UUID.randomUUID(), System.currentTimeMillis(), loc.getWorld().toString(),
                                                              (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
          altarMeta.getLink().getTransacCache().store(transac);
        }
      }
    }
  }

  @EventHandler
  public void onFurnaceStartSmeltEvent(FurnaceStartSmeltEvent event) {
    BoonManager.parseEvent(event);
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

  @EventHandler
  public void onEntityBreedEvent(EntityBreedEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onPlayerShearBlockEvent(PlayerShearBlockEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onBlockFadeEvent(BlockFadeEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onBlockFormEvent(BlockFormEvent event) {
    BoonManager.parseEvent(event);
  }
}
