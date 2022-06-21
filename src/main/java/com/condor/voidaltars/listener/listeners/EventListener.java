package com.condor.voidaltars.listener.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.BoonManager;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.transaction.DestroyTransaction;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.gui.MainAltarGUI;
import com.condor.voidaltars.listener.AltarListener;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import io.papermc.paper.event.block.PlayerShearBlockEvent;

/**
 *
 * Listens for Minecraft Events
 */
public class EventListener extends AltarListener {
  
  @EventHandler
  public void onBlockGrowEvent(BlockGrowEvent event) {
    BoonManager.parseEvent(event);
  }

  @EventHandler
  public void onTabCompleteEvent(TabCompleteEvent event) {
    CommandControl.parseOrigTabComplete(event);
  }

  @EventHandler
  public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
    BoonManager.parseEvent(event);
  }

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
