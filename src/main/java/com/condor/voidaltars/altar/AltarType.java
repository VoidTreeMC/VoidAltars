package com.condor.voidaltars.altar;

public enum AltarType {
  FARM_ALTAR("Agriculture Altar"),
  MINING_ALTAR("Mining Altar"),
  NETHER_ALTAR("Nether Altar"),
  OCEAN_ALTAR("Ocean Altar");

  String name = "";

  AltarType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public static AltarType getTypeFromString(String name) {
    for (AltarType type : AltarType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
