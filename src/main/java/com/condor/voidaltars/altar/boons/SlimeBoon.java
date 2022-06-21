package com.condor.voidaltars.altar.boons;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
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
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.object.Town;

/**
 * A boon that makes players bounce
 * instead of taking fall damage in their town
 */
public class SlimeBoon extends Boon {

  private static ArrayList<Class> triggerList = new ArrayList<>();

  static {
    triggerList.add(EntityDamageEvent.class);
  }

  public SlimeBoon() {
    super(StringConstants.SLIME_BLESSING_NAME.get(), StringConstants.SLIME_BLESSING_DESCRIPTION.get(), triggerList, BoonType.SLIME_BOON);
  }

  public ItemStack getIcon() {
    ItemStack is = new ItemStack(Material.BEACON, 1);
    ItemMeta meta = is.getItemMeta();
    meta.setDisplayName(StringConstants.SLIME_BLESSING_NAME.get());
    meta.setLore(StringListConstants.SLIME_BLESSING_LORE.get());
    is.setItemMeta(meta);
    return is;
  }

  public boolean isNecessary(Event event) {
    boolean ret = false;

    if (event instanceof EntityDamageEvent) {
      EntityDamageEvent ede = (EntityDamageEvent) event;
      ret = ede.getCause() == DamageCause.FALL;
      ret = ret && (ede.getEntity().getType() == EntityType.PLAYER);
      if (ret) {
        Player player = (Player) ede.getEntity();
        ret = !player.isSneaking();
        Location loc = ede.getEntity().getLocation();
        Town town = TownyFunctions.getTownFromLocation(loc);
        ret = ret && this.registeredTowns.contains(town);
      }
    }

    return ret;
  }

  public void execute(Event event) {
    EntityDamageEvent ede = (EntityDamageEvent) event;
    Player player = (Player) ede.getEntity();
    double damage = Math.min(8, ede.getDamage());
    double newY = Math.max(0.50, (damage / 6));
    player.setVelocity(player.getVelocity().setY(newY));
    ede.setCancelled(true);
  }
}
