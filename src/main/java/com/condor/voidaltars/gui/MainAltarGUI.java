package com.condor.voidaltars.gui;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
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


  private static int handleSacrificeClick(Player player, Material type, int amt) {
    int amtCharged = 0;

    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && item.getType() == type) {
        if ((amt - amtCharged) < item.getAmount()) {
          int tempAmtCharged = amtCharged;
          amtCharged += (amt - amtCharged);
          item.setAmount(item.getAmount() - (amt - tempAmtCharged));
          break;
        } else {
          amtCharged += item.getAmount();
          item.setAmount(0);
        }
      }
    }

    return amtCharged;
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

    ItemStack sacrificeProgressItem = new ItemStack(Material.HOPPER);
    ItemMeta hopperMeta = sacrificeProgressItem.getItemMeta();
    hopperMeta.setDisplayName("Sacrifice Progress");
    ArrayList<String> hopperLore = new ArrayList<>();
    hopperLore.add("Altar level: " + altarMeta.getLevel());
    hopperLore.add("Sacrifices remaining to please the gods: " + altarMeta.getSacrificesRemaining());
    hopperMeta.setLore(hopperLore);
    sacrificeProgressItem.setItemMeta(hopperMeta);


    for (int i = 0; i < altarMeta.getNumSacrificeSlots(); i++) {
      Sacrifice sacrifice = altarMeta.getSacrifice(i);
      ItemStack sacrificeItem = new ItemStack(sacrifice.getType());
      ItemMeta sacrificeItemMeta = sacrificeItem.getItemMeta();
      ArrayList<String> lore = new ArrayList<>();
      lore.add("Click to sacrifice");
      lore.add("up to " + sacrifice.getNumRemaining());
      lore.add("of this item");
      sacrificeItemMeta.setLore(lore);
      sacrificeItem.setItemMeta(sacrificeItemMeta);
      GuiItem sacrificeGuiItem = new GuiItem(SLOT_LOCKED);
      if (sacrificeItem != null) {
        sacrificeGuiItem = new GuiItem(sacrificeItem, event -> {
          Material type = sacrificeItem.getType();
          int amtWanted = sacrifice.getNumRemaining();
          int amtSacrificed = handleSacrificeClick(player, type, amtWanted);
          sacrifice.addToSacrificed(amtSacrificed);
          Bukkit.getLogger().log(Level.INFO, "Amt wanted: " + amtWanted);
          Bukkit.getLogger().log(Level.INFO, "Amt sacrificed: " + amtSacrificed);
          Bukkit.getLogger().log(Level.INFO, "Is finished: " + sacrifice.isFinished());
          if (sacrifice.isFinished()) {
            altarMeta.finishSacrifice(sacrifice);
          }
          gui.close(player);
        });
      }
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
    GuiItem hopperItem = new GuiItem(sacrificeProgressItem);

    gui.setItem(1, 5, hopperItem);
    gui.setItem(6, 5, bookItem);

    gui.open(player);
  }
}
