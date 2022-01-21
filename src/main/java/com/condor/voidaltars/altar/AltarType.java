package com.condor.voidaltars.altar;

import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.altar.altars.FarmAltar;
import com.condor.voidaltars.altar.altars.MiningAltar;
import com.condor.voidaltars.altar.altars.NetherAltar;
import com.condor.voidaltars.altar.altars.OceanAltar;

public enum AltarType {
  FARM_ALTAR(StringConstants.FARM_ALTAR_NAME.get()),
  MINING_ALTAR(StringConstants.MINING_ALTAR_NAME.get()),
  NETHER_ALTAR(StringConstants.NETHER_ALTAR_NAME.get()),
  OCEAN_ALTAR(StringConstants.OCEAN_ALTAR_NAME.get());

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

  public static Class getMetaFromType(AltarType type) {
    switch (type) {
      case FARM_ALTAR:
        return FarmAltar.class;
      case MINING_ALTAR:
        return MiningAltar.class;
      case NETHER_ALTAR:
        return NetherAltar.class;
      case OCEAN_ALTAR:
        return OceanAltar.class;
      default:
        return null;
    }
  }
}
