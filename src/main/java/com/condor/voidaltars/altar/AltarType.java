package com.condor.voidaltars.altar;

import com.condor.voidaltars.constants.StringConstants;

public enum AltarType {
  FARM_ALTAR(StringConstants.FARM_ALTAR_NAME),
  MINING_ALTAR(StringConstants.MINING_ALTAR_NAME),
  NETHER_ALTAR(StringConstants.NETHER_ALTAR_NAME),
  OCEAN_ALTAR(StringConstants.OCEAN_ALTAR_NAME);

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
