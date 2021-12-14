package com.condor.voidaltars.gui;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.Collection;

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

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.builder.item.ItemBuilder;

public class SelectBoonGUI {

  private static Random rng = new Random();

  private static ItemStack BACK_BUTTON = new ItemStack(Material.BARRIER);

  static {
    ItemMeta backButtonMeta = BACK_BUTTON.getItemMeta();
    backButtonMeta.setDisplayName("Back");
    BACK_BUTTON.setItemMeta(backButtonMeta);
  }

  public static void displayBoonGUI(Player player, AltarMeta altarMeta, int index) {
    PaginatedGui gui = Gui.paginated().title(Component.text("Select Boon")).rows(6).pageSize(45).create();
  	gui.setDefaultClickAction(event -> {
      event.setCancelled(true);
  	});

    GuiItem backButton = new GuiItem(BACK_BUTTON, event -> {
      MainAltarGUI.displayAltarGUI(player, altarMeta);
    });

    Collection<Boon> boonsList = BoonManager.getBoons();

    for (Boon boon : boonsList) {
      GuiItem boonItem = new GuiItem(boon.getIcon(), event -> {
        if (event.getClick() == ClickType.RIGHT) {
          player.sendMessage(ChatColor.GOLD + boon.getName() + ": " + ChatColor.YELLOW + boon.getDescription());
          gui.close(player);
        } else {
          altarMeta.getLink().setBoon(boon, index);
          Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
            @Override
            public void run() {
              SQLLinker.pushToDB(altarMeta.getLink());
            }
          });
          MainAltarGUI.displayAltarGUI(player, altarMeta);
        }
      });
      gui.addItem(boonItem);
    }

    gui.setItem(6, 9, backButton);

    // Previous item
    gui.setItem(6, 4, ItemBuilder.from(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName("Previous").asGuiItem(event -> gui.previous()));
    // Page number
    gui.setItem(6, 5, ItemBuilder.from(Material.COMPASS).setName(ChatColor.GRAY + "Page: " + gui.getCurrentPageNum() + "/" + gui.getPagesNum()).asGuiItem());
    // Next item
    gui.setItem(6, 6, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName("Next").asGuiItem(event -> gui.next()));

    gui.open(player);
  }
}
