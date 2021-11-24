package com.condor.voidaltars.altar;

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
  PEACE_BOON;

  public static BoonType getTypeFromString(String name) {
    for (BoonType type : BoonType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
