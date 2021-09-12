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

    ItemStack sacrificeOneItem = null;
    ItemStack sacrificeTwoItem = null;
    ItemStack sacrificeThreeItem = null;
    ItemStack sacrificeFourItem = null;

    switch (altarMeta.getNumSacrificeSlots()) {
      case 4:
        sacrificeFourItem = new ItemStack(altarMeta.getSacrifice(3).getType());
      case 3:
        sacrificeThreeItem = new ItemStack(altarMeta.getSacrifice(2).getType());
      case 2:
        sacrificeTwoItem = new ItemStack(altarMeta.getSacrifice(1).getType());
      case 1:
        sacrificeOneItem = new ItemStack(altarMeta.getSacrifice(0).getType());
    }

    GuiItem sacrificeOne = new GuiItem((sacrificeOneItem != null) ? sacrificeOneItem : SLOT_LOCKED);
    GuiItem sacrificeTwo = new GuiItem((sacrificeTwoItem != null) ? sacrificeTwoItem : SLOT_LOCKED);
    GuiItem sacrificeThree = new GuiItem((sacrificeThreeItem != null) ? sacrificeThreeItem : SLOT_LOCKED);
    GuiItem sacrificeFour = new GuiItem((sacrificeFourItem != null) ? sacrificeFourItem : SLOT_LOCKED);

    ItemStack boonOneItem = (altarMeta.getNumBoonSlots() >= 1) ? (new ItemStack(Material.BEACON)) : SLOT_LOCKED;
    ItemStack boonTwoItem = (altarMeta.getNumBoonSlots() >= 2) ? (new ItemStack(Material.BEACON)) : SLOT_LOCKED;
    ItemStack boonThreeItem = (altarMeta.getNumBoonSlots() >= 3) ? (new ItemStack(Material.BEACON)) : SLOT_LOCKED;
    ItemStack boonFourItem = (altarMeta.getNumBoonSlots() >= 4) ? (new ItemStack(Material.BEACON)) : SLOT_LOCKED;

    GuiItem boonOne = new GuiItem(boonOneItem);
    GuiItem boonTwo = new GuiItem((boonTwoItem != null) ? boonTwoItem : SLOT_LOCKED);
    GuiItem boonThree = new GuiItem((boonThreeItem != null) ? boonThreeItem : SLOT_LOCKED);
    GuiItem boonFour = new GuiItem((boonFourItem != null) ? boonFourItem : SLOT_LOCKED);

    GuiItem bookItem = new GuiItem(INSTRUCTION_BOOK);

    gui.setItem(6, 5, bookItem);
    
    gui.setItem(2, 2, sacrificeOne);
    gui.setItem(2, 4, sacrificeTwo);
    gui.setItem(2, 6, sacrificeThree);
    gui.setItem(2, 8, sacrificeFour);

    gui.setItem(5, 2, boonOne);
    gui.setItem(5, 4, boonTwo);
    gui.setItem(5, 6, boonThree);
    gui.setItem(5, 8, boonFour);

  	gui.open(player);
  }
}
