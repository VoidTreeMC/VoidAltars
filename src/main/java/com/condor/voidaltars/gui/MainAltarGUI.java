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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.ChatColor;

import com.condor.voidaltars.altar.Boon;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.altar.BoonManager;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.Sacrifice;
import com.condor.voidaltars.sql.SQLLinker;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;

public class MainAltarGUI {

  private static Random rng = new Random();

  private static ItemStack INSTRUCTION_BOOK = new ItemStack(Material.BOOK);
  private static ItemStack SACRIFICE_SLOT_LOCKED = new ItemStack(Material.BARRIER);
  private static ItemStack BOON_SLOT_LOCKED = new ItemStack(Material.BARRIER);

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

    ItemMeta sacrificeLockedMeta = SACRIFICE_SLOT_LOCKED.getItemMeta();
    sacrificeLockedMeta.setDisplayName("Sacrifice Slot Locked");
    ArrayList<String> sacrificeLockedLore = new ArrayList<>();
    sacrificeLockedLore.add("To unlock this slot, level your altar up");
    sacrificeLockedMeta.setLore(sacrificeLockedLore);
    SACRIFICE_SLOT_LOCKED.setItemMeta(sacrificeLockedMeta);

    ItemMeta boonLockedMeta = BOON_SLOT_LOCKED.getItemMeta();
    boonLockedMeta.setDisplayName("Boon Slot Locked");
    ArrayList<String> boonLockedLore = new ArrayList<>();
    boonLockedLore.add("To unlock this slot, level your altar up");
    boonLockedMeta.setLore(boonLockedLore);
    BOON_SLOT_LOCKED.setItemMeta(boonLockedMeta);
  }

  public static boolean isBucket(Material type) {
    switch (type) {
      case BUCKET:
      case WATER_BUCKET:
      case LAVA_BUCKET:
      case POWDER_SNOW_BUCKET:
      case MILK_BUCKET:
      case PUFFERFISH_BUCKET:
      case SALMON_BUCKET:
      case COD_BUCKET:
      case TROPICAL_FISH_BUCKET:
      case AXOLOTL_BUCKET:
        return true;
      default:
        return false;
    }
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

    GuiItem sacrificeSlotLocked = new GuiItem(SACRIFICE_SLOT_LOCKED);
    GuiItem boonSlotLocked = new GuiItem(BOON_SLOT_LOCKED);

    ItemStack sacrificeProgressItem = new ItemStack(Material.HOPPER);
    ItemMeta hopperMeta = sacrificeProgressItem.getItemMeta();
    hopperMeta.setDisplayName("Sacrifice Progress");
    ArrayList<String> hopperLore = new ArrayList<>();
    hopperLore.add("Altar level: " + altarMeta.getLevel());
    int sacRemaining = altarMeta.getSacrificesRemaining();
    if (sacRemaining > 0) {
      hopperLore.add("Sacrifices remaining to please the gods: " + sacRemaining);
    } else {
      hopperLore.add("Sacrifices remaining to level up altar: " + altarMeta.getSacrificesNeededForLevelUp());
    }
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
      GuiItem sacrificeGuiItem = new GuiItem(SACRIFICE_SLOT_LOCKED);
      if (sacrificeItem != null) {
        sacrificeGuiItem = new GuiItem(sacrificeItem, event -> {
          Material type = sacrificeItem.getType();
          int amtWanted = sacrifice.getNumRemaining();
          int amtSacrificed = handleSacrificeClick(player, type, amtWanted);
          if (isBucket(type) && amtSacrificed > 0) {
            int num = amtSacrificed;
            while (num > 0) {
              if (num >= 16) {
                player.getInventory().addItem(new ItemStack(Material.BUCKET, 16));
                num -= 16;
              } else {
                player.getInventory().addItem(new ItemStack(Material.BUCKET, num));
                break;
              }
            }
          }
          sacrifice.addToSacrificed(amtSacrificed);
          if (sacrifice.isFinished()) {
            altarMeta.finishSacrifice(sacrifice);
          }
          SQLLinker.pushToDB(altarMeta);
          gui.close(player);
        });
      }
      gui.setItem(2, 2 + (2 * i), sacrificeGuiItem);
    }

    for (int i = altarMeta.getNumSacrificeSlots(); i < altarMeta.getMaxLevel(); i++) {
      gui.setItem(2, 2 + (2 * i), sacrificeSlotLocked);
    }

    for (int i = 0; i < altarMeta.getNumBoonSlots(); i++) {
      ItemStack boonItem = new ItemStack(Material.BEACON);
      Boon boon = altarMeta.getBoon(i);
      final int index = i;
      if (boon != null) {
        boonItem = boon.getIcon();
      }
      GuiItem boonGuiItem = new GuiItem(boonItem, event -> {
        if (event.getClick() == ClickType.RIGHT && boon != null) {
          player.sendMessage(ChatColor.GOLD + boon.getName() + ": " + ChatColor.YELLOW + boon.getDescription());
          gui.close(player);
        } else {
          SelectBoonGUI.displayBoonGUI(player, altarMeta, index);
        }
      });
      gui.setItem(5, 2 + (2 * i), boonGuiItem);
    }

    for (int i = altarMeta.getNumBoonSlots(); i < altarMeta.getMaxLevel(); i++) {
      gui.setItem(5, 2 + (2 * i), boonSlotLocked);
    }

    GuiItem bookItem = new GuiItem(INSTRUCTION_BOOK);
    GuiItem hopperItem = new GuiItem(sacrificeProgressItem);

    gui.setItem(1, 5, hopperItem);
    gui.setItem(6, 5, bookItem);

    gui.open(player);
  }
}
