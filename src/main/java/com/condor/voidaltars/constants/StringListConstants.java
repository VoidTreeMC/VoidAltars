package com.condor.voidaltars.constants;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

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
  });

  List<String> list;

  StringListConstants(String[] arr) {
    this.list = Arrays.asList(arr);
  }

  public List<String> get() {
    return this.list;
  }
}
