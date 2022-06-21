package com.condor.voidaltars.altar;

import java.util.List;
import java.util.HashMap;


import org.bukkit.inventory.ItemStack;

import com.condor.voidaltars.altar.settings.OutsiderSacrificeSetting;

/**
 * Represents a setting tied to a
 * TownAltarLink
 */
public abstract class AltarSettings {

  SettingsType type;
  String name;
  String columnName;
  ItemStack icon;
  List<String> description;

  /**
   * Constructs an AltarSettings object
   * @param type         An enum: the type of the altar setting
   * @param columnName   The SQL column name used for the setting
   * @param icon         An ItemStack representing the icon (not named or described)
   * @param name         The name of the setting
   * @param description  The description of the setting, to be shown on the icon
   */
  public AltarSettings(SettingsType type, String columnName, ItemStack icon, String name, List<String> description) {
    this.type = type;
    this.columnName = columnName;
    this.icon = icon;
    this.name = name;
    this.description = description;
  }

  /**
   * Returns a map of the default settings
   * for a town
   * @return A map containing default settings
   */
  public static HashMap<SettingsType, AltarSettings> getDefaultSettings() {
    HashMap<SettingsType, AltarSettings> ret = new HashMap<>();

    ret.put(SettingsType.OUTSIDER_SACRIFICES, new OutsiderSacrificeSetting(true));

    return ret;
  }

  /**
   * Returns the current state of the setting
   * @return The current state of the setting
   */
  public abstract Object getState();

  /**
   * Cycles the setting between
   * true/false, 0-n, etc.
   */
  public abstract void cycle();

  /**
   * Gets a string representing the
   * state of the setting. To be used
   * in the icon name
   * @return The state as a string
   */
  public abstract String getStateString();

  /**
   * Gets an enum representing the
   * type of the setting
   * @return The setting type
   */
  public SettingsType getType() {
    return this.type;
  }

  /**
   * Gets the name of the column in
   * the SQL table
   * @return The name of the column for this setting in the SQL table
   */
  public String getColumnName() {
    return this.columnName;
  }

  /**
   * Gets an item icon for the setting
   * @return An item icon for the setting
   */
  public ItemStack getIcon() {
    return icon;
  }

  /**
   * Gets the name of the setting
   * @return The name of the setting
   */
  public String getName() {
    return name;
  }

  /**
   * Gets a description of the setting,
   * to be used in the item icon
   * @return A list description of the settings
   */
  public List<String> getDescription() {
    return description;
  }
}
