package com.condor.voidaltars.gui;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.Sacrifice;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;

public class MainAltarGUI {

  private static Random rng = new Random();

  private static ItemStack INSTRUCTION_BOOK = new ItemStack(Material.BOOK);
  private static ItemStack SLOT_LOCKED = new ItemStack(Material.BARRIER);

  static {
    ItemMeta bookMeta = INSTRUCTION_BOOK.getItemMeta();
    bookMeta.setDisplayName("Instructions");
    ArrayList<String> bookLore = new ArrayList<>();
    bookLore.add("Blah blah");
    bookLore.add("Blah blah");
    bookLore.add("Blah blah");
    bookLore.add("Blah blah");
    bookLore.add("Blah blah");
    bookMeta.setLore(bookLore);
    INSTRUCTION_BOOK.setItemMeta(bookMeta);
  }

  public static void displayAltarGUI(Player player, AltarMeta altarMeta) {
    Gui gui = new Gui(6, altarMeta.getType().getName());
  	gui.setDefaultClickAction(event -> {
      event.setCancelled(true);
  	});

    gui.getFiller().fill(new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
    GuiItem air = new GuiItem(new ItemStack(Material.AIR));

    // GuiItem confirmPane = new GuiItem(CONFIRM_PANE_RED, event -> {
    //
    // });

    GuiItem slotLocked = new GuiItem(SLOT_LOCKED);

    for (int i = 0; i < altarMeta.getNumSacrificeSlots(); i++) {
      Sacrifice sacrifice = altarMeta.getSacrifice(i);
      ItemStack sacrificeItem = new ItemStack(sacrifice.getType());
      GuiItem sacrificeGuiItem = new GuiItem((sacrificeItem != null) ? sacrificeItem : SLOT_LOCKED);
      gui.setItem(2, 2 + (2 * i), sacrificeGuiItem);
    }

    for (int i = altarMeta.getNumSacrificeSlots(); i < altarMeta.getMaxLevel(); i++) {
      gui.setItem(2, 2 + (2 * i), slotLocked);
    }

    for (int i = 0; i < altarMeta.getNumBoonSlots(); i++) {
      ItemStack boonItem = new ItemStack(Material.BEACON);
      GuiItem boonGuiItem = new GuiItem(boonItem);
      gui.setItem(5, 2 + (2 * i), boonGuiItem);
    }

    for (int i = altarMeta.getNumBoonSlots(); i < altarMeta.getMaxLevel(); i++) {
      gui.setItem(5, 2 + (2 * i), slotLocked);
    }

    GuiItem bookItem = new GuiItem(INSTRUCTION_BOOK);

    gui.setItem(6, 5, bookItem);

    gui.open(player);
  }
}
