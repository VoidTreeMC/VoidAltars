package com.condor.voidaltars.altar;

/**
 * Represents the different types of boons
 */
public enum BoonType {
  HARVEST_BOON,
  RANCHER_BOON,
  SENTINEL_BOON,
  NETHER_BOON,
  END_BOON,
  FORGE_BOON,
  BEEKEEPER_BOON,
  FREEZE_BOON,
  THAW_BOON,
  PEACE_BOON,
  ANTIPEST_BOON,
  SLIME_BOON;

  /**
   * Gets the type of boon associated with its name
   * @param  name               The name of the boon
   * @return                    The BoonType associated with the name
   */
  public static BoonType getTypeFromString(String name) {
    for (BoonType type : BoonType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
