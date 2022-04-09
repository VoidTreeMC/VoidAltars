package com.condor.voidaltars.gui;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.UUID;

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
import com.condor.voidaltars.altar.SacrificeManager;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.transaction.SacrificeTransaction;
import com.condor.voidaltars.leaderboard.LeaderboardParser;
import com.condor.voidaltars.altar.SettingsType;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

/**
 * The main altar GUI in which players can
 * select sacrifices and boon slots
 */
public class MainAltarGUI {

  private static Random rng = new Random();

  private static ItemStack INSTRUCTION_BOOK = new ItemStack(Material.BOOK);
  private static ItemStack SACRIFICE_SLOT_LOCKED = new ItemStack(Material.BARRIER);
  private static ItemStack BOON_SLOT_LOCKED = new ItemStack(Material.BARRIER);

  static {
    ItemMeta bookMeta = INSTRUCTION_BOOK.getItemMeta();
    bookMeta.setDisplayName("Instructions");
    ArrayList<String> bookLore = new ArrayList<>();
    bookLore.add("Click on me");
    bookLore.add("for more information");
    bookLore.add("about VoidTree altars");
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

  /**
   * Returns true if the item is a bucket of something,
   * false otherwise
   * @param  type               The item type to evaluate
   * @return                    True if the item is a bucket, false otherwise
   */
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

  /**
   * Returns true if the item is a bowl of something,
   * false otherwise
   * @param  type               The item type to evaluate
   * @return                    True if the item is a bowl, false otherwise
   */
  public static boolean isBowl(Material type) {
    switch (type) {
      case MUSHROOM_STEW:
      case BEETROOT_SOUP:
      case RABBIT_STEW:
      case SUSPICIOUS_STEW:
        return true;
      default:
        return false;
    }
  }

  /**
   * Returns true if the item is a bottle of something,
   * false otherwise
   * @param  type               The item type to evaluate
   * @return                    True if the item is a bottle, false otherwise
   */
  public static boolean isBottle(Material type) {
    switch (type) {
      case HONEY_BOTTLE:
      // Water bottles are considered potions. Who'dathunkit?
      case POTION:
      case DRAGON_BREATH:
        return true;
      default:
        return false;
    }
  }

  /**
   * Handles a sacrifice click. Takes the specified amount away
   * from the player's inventory, and returns the amount
   * taken.
   * @param  player               The player that is sacrificing
   * @param  type                 The type of the item to sacrifice
   * @param  amt                  The amount of the item to sacrifice
   * @return                      The amount of the item taken for the sacrifice
   */
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

  private static boolean shouldRestrictSacrifices(AltarMeta meta) {
    return ((meta.getLevel() == meta.getMaxLevel()) && meta.getLink().getSacrificesRemaining() <= 0);
  }

  /**
   * Displays the main altar GUI to the player
   * @param player     The player that is viewing the GUI
   * @param altarMeta  The altar whose GUI is to be shown
   */
  public static void displayAltarGUI(Player player, AltarMeta altarMeta) {
    Gui gui = new Gui(6, altarMeta.getType().getName());
  	gui.setDefaultClickAction(event -> {
      event.setCancelled(true);
  	});

    gui.getFiller().fill(new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
    GuiItem air = new GuiItem(new ItemStack(Material.AIR));


    GuiItem sacrificeSlotLocked = new GuiItem(SACRIFICE_SLOT_LOCKED);
    GuiItem boonSlotLocked = new GuiItem(BOON_SLOT_LOCKED);

    ItemStack sacrificeProgressItem = new ItemStack(Material.HOPPER);
    ItemMeta hopperMeta = sacrificeProgressItem.getItemMeta();
    hopperMeta.setDisplayName("Sacrifice Progress");
    ArrayList<String> hopperLore = new ArrayList<>();
    hopperLore.add("Altar level: " + altarMeta.getLevel());
    int sacRemaining = altarMeta.getLink().getSacrificesRemaining();
    if (sacRemaining > 0) {
      hopperLore.add(StringConstants.SACRIFICES_REMAINING_TO_PLEASE.get() + sacRemaining);
    } else {
      hopperLore.add(StringConstants.GODS_ARE_PLEASED.get());
      if (altarMeta.getLink().getLevel() < altarMeta.getLink().getMaxLevel()) {
        hopperLore.add(StringConstants.SACRIFICES_REMAINING_TO_LEVEL.get() + (altarMeta.getLink().getSacrificesNeededForLevelUp() + 1));
      } else {
        hopperLore.add(StringConstants.ALTAR_MAX_LEVEL.get());
      }
    }
    hopperMeta.setLore(hopperLore);
    sacrificeProgressItem.setItemMeta(hopperMeta);

    ItemStack altarRankItem = new ItemStack(Material.SWEET_BERRIES);
    ItemMeta altarRankMeta = altarRankItem.getItemMeta();
    int altarRank = LeaderboardParser.getCurrentPosition(altarMeta.getLink().getTown().getUUID());
    switch (altarRank) {
      case 1:
        altarRankItem.setType(Material.NETHERITE_INGOT);
        break;
      case 2:
        altarRankItem.setType(Material.DIAMOND);
        break;
      case 3:
        altarRankItem.setType(Material.GOLD_INGOT);
        break;
      case 4:
        altarRankItem.setType(Material.IRON_INGOT);
        break;
      case 5:
        altarRankItem.setType(Material.COPPER_INGOT);
        break;
      default:
        altarRankItem.setType(Material.GRASS_BLOCK);
        break;
    }
    ArrayList<String> rankLore = new ArrayList<>();
    if (altarRank != -1) {
      altarRankMeta.setDisplayName(ChatColor.BOLD + "Altar rank " + altarRank + " on " + StringConstants.SERVER_NAME.get());
      rankLore.add("Complete more sacrifices to");
      if (altarRank != 1) {
        rankLore.add("increase your altar rank.");
      } else {
        rankLore.add("keep your first-place position.");
      }
    } else {
      altarRankMeta.setDisplayName("Altar rank: Unranked");
      rankLore.add("Complete sacrifices to ");
      rankLore.add("get an altar rank");
    }
    altarRankMeta.setLore(rankLore);
    altarRankItem.setItemMeta(altarRankMeta);

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
          if (!((Boolean) (altarMeta.getLink().getSetting(SettingsType.OUTSIDER_SACRIFICES).getState()))) {
            try {
              Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
              if (resident.getTown() == null || !(resident.getTown().equals(altarMeta.getLink().getTown()))) {
                player.sendMessage(StringConstants.SACRIFICE_PERMS_DENIED.get());
              }
            } catch (NotRegisteredException e) {
              e.printStackTrace();
            }
            gui.close(player);
          }
          if (shouldRestrictSacrifices(altarMeta)) {
            player.sendMessage(StringConstants.NO_MORE_SACRIFICES.get());
            gui.close(player);
            return;
          }
          Material type = sacrificeItem.getType();
          int amtWanted = sacrifice.getNumRemaining();
          int amtSacrificed = handleSacrificeClick(player, type, amtWanted);
          if ((isBucket(type) || isBowl(type) || isBottle(type)) && amtSacrificed > 0) {
            int num = amtSacrificed;
            while (num > 0) {
              Material containerMat = (isBucket(type)) ? Material.BUCKET : ((isBowl(type)) ? Material.BOWL : Material.GLASS_BOTTLE);
              if (num >= 16) {
                player.getInventory().addItem(new ItemStack(containerMat, 16));
                num -= 16;
              } else {
                player.getInventory().addItem(new ItemStack(containerMat, num));
                break;
              }
            }
          }
          sacrifice.addToSacrificed(amtSacrificed);
          if (sacrifice.isFinished()) {
            altarMeta.finishSacrifice(sacrifice, player);
            LeaderboardParser.parseSacrifice(sacrifice);
          }
          if (amtSacrificed > 0) {
            Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
              @Override
              public void run() {
                SQLLinker.pushToDB(altarMeta);
                SQLLinker.pushToDB(altarMeta.getLink());
                SacrificeTransaction transac = new SacrificeTransaction(altarMeta.getUniqueId(), altarMeta.getLink(), player.getUniqueId(),
                                                                        UUID.randomUUID(), System.currentTimeMillis(), altarMeta.getType(), altarMeta.getLevel(),
                                                                        sacrifice.getType().toString(), amtSacrificed, amtWanted - amtSacrificed);
                altarMeta.getLink().getTransacCache().store(transac);
              }
            });
          }
          gui.close(player);
        });
      }
      gui.setItem(2, 2 + (2 * i), sacrificeGuiItem);
    }

    for (int i = altarMeta.getNumSacrificeSlots(); i < altarMeta.getMaxLevel(); i++) {
      gui.setItem(2, 2 + (2 * i), sacrificeSlotLocked);
    }

    for (int i = 0; i < altarMeta.getLink().getNumBoonSlots(); i++) {
      ItemStack boonItem = new ItemStack(Material.BEACON);
      Boon boon = altarMeta.getLink().getBoon(i);
      final int index = i;
      if (boon != null) {
        boonItem = boon.getIcon();
      }
      GuiItem boonGuiItem = new GuiItem(boonItem, event -> {
        if (event.getClick() == ClickType.RIGHT && boon != null) {
          player.sendMessage(ChatColor.GOLD + boon.getName() + ": " + ChatColor.YELLOW + boon.getDescription());
          gui.close(player);
        } else {
          Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
          try {
            // If they're in the same town and are a mayor/high priest, OR they have the staff permission node to override any boon
            if ((resident.getTown() != null && resident.getTown().getUUID().equals(altarMeta.getTown().getUUID())) ||
                 player.hasPermission("condor.altar.override-boon")) {
              if (resident.isMayor() || resident.hasTownRank("high-priest") || player.hasPermission("condor.altar.override-boon")) {
                SelectBoonGUI.displayBoonGUI(player, altarMeta, index);
              } else {
                player.sendMessage(StringConstants.NO_PERMISSIONS_TO_CHANGE_BOONS.get());
              }
            } else {
              player.sendMessage(StringConstants.NOT_MEMBER_OF_ALTARS_TOWN.get());
            }
          } catch (NotRegisteredException e) {
            // Not registered. Ignore and do nothing.
          }
        }
      });
      gui.setItem(5, 2 + (2 * i), boonGuiItem);
    }

    for (int i = altarMeta.getLink().getNumBoonSlots(); i < altarMeta.getMaxLevel(); i++) {
      gui.setItem(5, 2 + (2 * i), boonSlotLocked);
    }

    GuiItem bookItem = new GuiItem(INSTRUCTION_BOOK, event -> {
      player.sendMessage(ChatColor.YELLOW + "More altar information is available at the following webpage");
      player.sendMessage(StringConstants.ALTAR_HELP_WEBPAGE.get());
    });
    GuiItem hopperItem = new GuiItem(sacrificeProgressItem);
    GuiItem altarRankGUIItem = new GuiItem(altarRankItem);

    gui.setItem(1, 5, hopperItem);
    gui.setItem(6, 5, bookItem);
    gui.setItem(1, 9, altarRankGUIItem);

    gui.open(player);
  }
}
