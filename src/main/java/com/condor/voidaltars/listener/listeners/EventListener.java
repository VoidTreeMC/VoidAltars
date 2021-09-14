package com.condor.voidaltars.listener.listeners;

import java.util.Random;
import java.util.UUID;

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

import com.condor.voidaltars.listener.AltarListener;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.multiblock.structures.FarmAltarStructure;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.gui.MainAltarGUI;

/**
 *
 * Listens for Minecraft Events
 *
 * @author iron-condor
 */
public class EventListener  extends AltarListener {
  private static final Random rng = new Random();

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    if (event.getHand() != EquipmentSlot.HAND || event.getClickedBlock() == null) {
      return;
    }
    if (AltarStructure.isPossibleInterfaceBlock(event.getClickedBlock().getType())) {
      Location loc = event.getClickedBlock().getLocation();
      AltarMeta altarMeta = AltarManager.getAltarFromLoc(loc, event);
      if (altarMeta != null) {
        // TODO: Check access here
        MainAltarGUI.displayAltarGUI(event.getPlayer(), altarMeta);
        event.setCancelled(true);
      } else {
        event.getPlayer().sendMessage("This is not an altar.");
      }
    }
  }
}
