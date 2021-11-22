package com.condor.voidaltars.constants;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class StringConstants {
  // Boons
  public static final String END_BLESSING_NAME = "Blessing of the End";
  public static final String END_BLESSING_DESCRIPTION = "The gods protect everyone in your town from the void. If you fall into the void, you will wake up in bed.";
  public static ArrayList<String> END_BLESSING_LORE = new ArrayList<>();
  static {
    END_BLESSING_LORE.add("Gaze into the void.");
    END_BLESSING_LORE.add(ChatColor.MAGIC + "123" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "IT GAZES BACK" + ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + "123" + ChatColor.RESET);
  }

  public static final String NETHER_BLESSING_NAME = "Blessing of the Nether";
  public static final String NETHER_BLESSING_DESCRIPTION = "The gods protect everyone in your town from the flame. No players or mobs in your town will take damage from fire, lava or hot floors.";
  public static ArrayList<String> NETHER_BLESSING_LORE = new ArrayList<>();
  static {
    NETHER_BLESSING_LORE.add("Turn your back on the Overworld.");
    NETHER_BLESSING_LORE.add(ChatColor.RED + "" + ChatColor.BOLD + "Welcome the fire.");
  }

  public static final String RANCHER_BLESSING_NAME = "Blessing of the Rancher";
  public static final String RANCHER_BLESSING_DESCRIPTION = "The gods bless your herds. Whenever a livestock mob dies, you get extra drops.";
  public static ArrayList<String> RANCHER_BLESSING_LORE = new ArrayList<>();
  static {
    RANCHER_BLESSING_LORE.add("Your herds and flocks grow large");
    RANCHER_BLESSING_LORE.add("and your silver and gold increase");
  }

  public static final String SENTINEL_BLESSING_NAME = "Blessing of the Sentinel";
  public static final String SENTINEL_BLESSING_DESCRIPTION = "The gods protect your small livestock. Foxes and cats are friendly to chickens and rabbits in your town and do not eat them.";
  public static ArrayList<String> SENTINEL_BLESSING_LORE = new ArrayList<>();
  static {
    SENTINEL_BLESSING_LORE.add("When the dog is awake, the shepherd may sleep.");
  }

  // User-visible error messages
  public static final String ALTAR_NO_LONGER_VALID = "This is no longer a valid altar. The gods are not pleased.";
  public static final String MUST_BE_IN_TOWN_TO_CREATE_ALTAR_THERE = "You can only create altars in a town that you own.";
  public static final String NO_PERMISSIONS_TO_CREATE_ALTAR = "You must be a mayor or a high-priest of this town to create an altar here.";
  public static final String NO_PERMISSIONS_TO_CHANGE_BOONS = "You must be a mayor or a high-priest to change your town's altar boons.";
  public static final String NO_DUPLICATE_ALTARS = "Your town already has an altar. You cannot create another one.";
  public static final String NO_ALTARS_IN_THE_WILD = "You must be in a town for this altar to function.";
  public static final String NOT_MEMBER_OF_ALTARS_TOWN = "You cannot change the boons of another town's altar.";
  public static final String TOWN_HAS_UNCLAIMED_ALTAR = "Your town has just unclaimed its altar! Claim it back to get the boons again.";

  // Altar names
  public static final String FARM_ALTAR_NAME = "Agriculture Altar";
  public static final String MINING_ALTAR_NAME = "Mining Altar";
  public static final String OCEAN_ALTAR_NAME = "Ocean Altar";
  public static final String NETHER_ALTAR_NAME = "Nether Altar";

  // URLs
  public static final String ALTAR_HELP_WEBPAGE = "" + ChatColor.GOLD + ChatColor.UNDERLINE + "https://www.voidtreemc.com/voidtree-altar-help";

  // GUI strings
  public static final String SACRIFICES_REMAINING_TO_PLEASE = "Sacrifices remaining to please the gods: ";
  public static final String SACRIFICES_REMAINING_TO_LEVEL = "Sacrifices remaining to level up altar: ";
}
