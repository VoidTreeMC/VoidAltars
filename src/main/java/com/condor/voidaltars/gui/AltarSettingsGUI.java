package com.condor.voidaltars.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.condor.voidaltars.altar.AltarSettings;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.sql.SQLLinker;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;

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
