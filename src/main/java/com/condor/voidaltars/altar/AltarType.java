package com.condor.voidaltars.altar;

public enum AltarType {
  FARM_ALTAR("Farm Altar"),
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
}
