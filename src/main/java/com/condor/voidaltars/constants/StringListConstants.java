package com.condor.voidaltars.constants;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

/**
 * Contains a list of multi-line string constants
 * used internally by much of the GUI code and
 * user-interaction code
 */
public enum StringListConstants {
  END_BLESSING_LORE(new String[]{
    "Gaze into the void.",
    ChatColor.MAGIC + "123" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "IT GAZES BACK" + ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + "123" + ChatColor.RESET
  }),
  NETHER_BLESSING_LORE(new String[]{
    "Turn your back on the Overworld.",
    ChatColor.RED + "" + ChatColor.BOLD + "Welcome the fire."
  }),
  RANCHER_BLESSING_LORE(new String[]{
    "Your herds and flocks grow large",
    "and your silver and gold increase"
  }),
  SENTINEL_BLESSING_LORE(new String[]{
    "When the dog is awake, the shepherd may sleep."
  }),
  FORGE_BLESSING_LORE(new String[]{
    "From the ashes a fire shall be woken,",
    "A light from the shadows shall spring;",
    "Renewed shall be blade that was broken,",
    "The crownless again shall be king.",
    "― J.R.R. Tolkien, The Fellowship of the Ring"
  }),
  BEEKEEPER_BLESSING_LORE(new String[]{
    "Out of the eater came something to eat.",
    "Out of the strong came something sweet."
  }),
  FREEZE_BLESSING_LORE(new String[]{
    "I hold with those who favor fire.",
    "But if it had to perish twice,",
    "I think I know enough of hate",
    "To know that for destruction ice",
    "Is also great",
    "- Robert Frost"
  }),
  THAW_BLESSING_LORE(new String[]{
    "At last the spring thaw came",
    "and graves were laboriously",
    "prepared for the nine silent",
    "harvests of the grim reaper",
    "which waited in the tomb.",
    "— H.P. Lovecraft"
  }),
  PEACE_BLESSING_LORE(new String[]{
    "The wolf shall live with the lamb,",
    "the leopard shall lie down with the kid,",
    "the calf and the lion and the fatling together"
  }),
  PEST_CONTROL_BLESSING_LORE(new String[]{
    "We hate cats.",
    "We hate golems."
  });

  List<String> list;

  /**
   * Constructor for a StringListConstants enum
   * @param arr  A String array where each entry is its own line
   */
  StringListConstants(String[] arr) {
    this.list = Arrays.asList(arr);
  }

  public List<String> get() {
    return this.list;
  }

  public void set(String[] newList) {
    this.list = Arrays.asList(newList);
  }
}
