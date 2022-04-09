package com.condor.voidaltars.gui;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.ClickType;

import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.Sacrifice;
import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonManager;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.transaction.BoonTransaction;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.AltarSettings;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;

/**
 * The GUI that allows players to change
 * their altar settings
 */
public class AltarSettingsGUI {

  /**
   * Displays the altar settings GUI
   * @param player  The player that is seeing the GUI
   * @param link    The player's town-altar-link
   */
  public static void displaySettingsGUI(Player player, TownAltarLink link) {
    Gui gui = new Gui(1, "Settings");
  	gui.setDefaultClickAction(event -> {
      event.setCancelled(true);
  	});


    Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
    boolean highPriestOrMayor = (resident != null && (resident.isMayor() || resident.hasTownRank("high-priest")));

    for (AltarSettings setting : link.getSettings().values()) {
      ItemStack icon = setting.getIcon();
      ItemMeta meta = icon.getItemMeta();
      meta.setDisplayName(ChatColor.LIGHT_PURPLE + setting.getName() + " " + setting.getStateString());
      meta.setLore(setting.getDescription());
      icon.setItemMeta(meta);
      GuiItem settingItem = new GuiItem(icon, event -> {
        if (highPriestOrMayor) {
          setting.cycle();
          displaySettingsGUI(player, link);
          Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
            @Override
            public void run() {
              SQLLinker.pushToDB(link, link.getSettings());
            }
          });
        } else {
          player.sendMessage(StringConstants.NO_PERMISSIONS_TO_CHANGE_SETTINGS.get());
          gui.close(player);
        }
      });
      gui.addItem(settingItem);
    }

    gui.open(player);
  }
}
