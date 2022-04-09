package com.condor.voidaltars.altar;

/**
 * Enumeration that represents a type of setting.
 * Used in tandem with the AltarSettings class
 */
public enum SettingsType {
  OUTSIDER_SACRIFICES;

  /**
   * Gets the type of setting associated with its name
   * @param  name               The name of the setting
   * @return                    The SettingsType associated with the name
   */
  public static SettingsType getTypeFromString(String name) {
    for (SettingsType type : SettingsType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
