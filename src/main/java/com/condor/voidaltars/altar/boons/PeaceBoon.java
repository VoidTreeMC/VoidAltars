package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.TownyUniverse;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.util.TownyFunctions;

public class PeaceBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  private static Random rng = new Random();

  private static final String ATTACKED_KEY = "void_altars_ATTACKED";

  static {
    triggerList.add(EntityTargetLivingEntityEvent.class);
    triggerList.add(EntityDamageByEntityEvent.class);
  }

  public PeaceBoon() {
    super(StringConstants.PEACE_BLESSING_NAME.get(), StringConstants.PEACE_BLESSING_DESCRIPTION.get(), triggerList, BoonType.PEACE_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.PEACE_BLESSING_NAME.get());
    meta.setLore(StringListConstants.PEACE_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof EntityTargetLivingEntityEvent) {
      EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
      if (etlee.getTarget() != null && etlee.getEntity() != null) {
        if (!etlee.getEntity().hasMetadata(ATTACKED_KEY)) {
          Location loc = etlee.getTarget().getLocation();
          Town town = TownyFunctions.getTownFromLocation(loc);
          ret = this.registeredTowns.contains(town);
        }
      }
    } else if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
      if (edbee.getDamager().getType() == EntityType.PLAYER) {
        Player player = (Player) edbee.getDamager();
        // If the player is in the town
        Location loc = player.getLocation();
        Town town = TownyFunctions.getTownFromLocation(loc);
        ret = this.registeredTowns.contains(town);
      }
    }
    return ret;
  }

  public void execute(Event event) {
    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
      Entity entity = edbee.getEntity();
      entity.setMetadata(ATTACKED_KEY, new FixedMetadataValue(AltarMain.getPlugin(), true));
    } else if (event instanceof EntityTargetLivingEntityEvent) {
      EntityTargetLivingEntityEvent etlee = (EntityTargetLivingEntityEvent) event;
      etlee.setCancelled(true);
    }
  }
}
