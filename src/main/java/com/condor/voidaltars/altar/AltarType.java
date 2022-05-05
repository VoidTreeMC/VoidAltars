package com.condor.voidaltars.altar;

import com.condor.voidaltars.constants.StringConstants;

/**
 * Represents a type of altar
 */
public enum AltarType {
  FARM_ALTAR,
  MINING_ALTAR,
  NETHER_ALTAR,
  OCEAN_ALTAR,
  HUNTING_ALTAR;

  /**
   * Returns the type of altar associated with
   * the name provided
   * @param  name               The name of the altar type
   * @return                    The altar type
   */
  public static AltarType getTypeFromString(String name) {
    for (AltarType type : AltarType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
