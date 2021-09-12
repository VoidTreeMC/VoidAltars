package com.condor.voidaltars.altar;

import java.util.ArrayList;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;

public abstract class Altar {
  // TODO: Figure out something with its ID here
  AltarType type;
  Town town;
  Location interfaceLoc;
  AltarStructure structure;
  ArrayList<Boon> boons = new ArrayList<>();
  ArrayList<Sacrifice> sacrifices = new ArrayList<>();
  int totalRecentSacrifices;
  int sacrificesWanted;
  int level;
  int totalSacrificesMade;
  // Might end up being unused. To be used later, when statistics are
  // being implemented.
  // ArrayList<Sacrifice> sacrificeHistory = new ArrayList<>();


  /**
   * Builds a new Altar
   * @param type          The type of the altar
   * @param interfaceLoc  The location of the interface block
   */
  public AltarMeta(AltarType type, Location interfaceLoc, AltarStructure structure) {
    this.type = type;
    this.structure = structure;
    TownBlock tb = TownyAPI.getInstance().getTownBlock(interfaceLoc);
    if (tb != null) {
      this.town = tb.getTown();
    } else {
      throw new NotInATownException(interfaceLoc);
      return;
    }
    this.totalRecentSacrifices = 0;
    this.level = 1;
    this.sacrificesWanted = AltarManager.getSacrificesNeededByLevel(this.level);
  }
}
