package com.condor.voidaltars.altar;

import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.altars.*;
import com.condor.voidaltars.altar.multiblock.structures.*;
import com.condor.voidaltars.altar.exception.NotInATownException;

public class AltarManager {
  private static TreeMap<AltarType, AltarStructure> altarTypeMap = new TreeMap<>();
  // TODO: Find a better data structure for this. Maybe a wrapper for Location that extends Comparable
  // Use location's string for this (for now).
  private static TreeMap<String, AltarMeta> altarMap = new TreeMap<>();

  public AltarManager() {
    init();
  }

  private void init() {
    altarTypeMap.put(AltarType.FARM_ALTAR, new FarmAltarStructure());
    altarTypeMap.put(AltarType.NETHER_ALTAR, new NetherAltarStructure());
    altarTypeMap.put(AltarType.MINING_ALTAR, new MiningAltarStructure());
    altarTypeMap.put(AltarType.OCEAN_ALTAR, new OceanAltarStructure());
  }

  public static AltarStructure getAltarByType(AltarType type) {
    return altarTypeMap.get(type);
  }

  public static AltarMeta getAltarFromLoc(Location loc, PlayerInteractEvent event) {

    Player player = event.getPlayer();

    AltarMeta altarMeta = altarMap.get(loc.toString());

    AltarStructure struc = getStructureFromLoc(loc);

    // If there's no known altar at this location
    if (altarMeta == null) {
      // If it's not a valid altar, we return null.
      if (struc == null) {
        return null;
      // If it IS a valid altar but we dont know about it,
      // we create a new altar meta for it and add it to the map
      } else {
        try {
          switch (struc.getType()) {
            case FARM_ALTAR:
            default:
              altarMeta = new FarmAltar(loc);
              break;
          }
          altarMap.put(loc.toString(), altarMeta);
          return altarMeta;
        } catch (NotInATownException e) {
          // They're not in a town. Ignore it.
          player.sendMessage("You must be in a town for this altar to function.");
          return null;
        }
      }
    // If we found the altar, return it
    } else {
      return altarMeta;
    }
  }

  public static AltarStructure getStructureFromLoc(Location loc) {
    AltarStructure struc = null;
    for (AltarStructure altarStructure : altarTypeMap.values()) {
      if (altarStructure.meetsRequirements(loc)) {
        struc = altarStructure;
        break;
      }
    }
    return struc;
  }

  /**
   * Gets the number of sacrifices needed in order to keep an altar
   * at its current level, based on the level that the altar is
   * currently at.
   * @param  level The current level of the altar
   * @return       The number of sacrifices demanded, or -1 if there was an error
   */
  public static int getSacrificesNeededByLevel(int level) {
    switch (level) {
      case 1:
        return 10;
      case 2:
        return 20;
      case 3:
        return 40;
      case 4:
        return 80;
      default:
        return -1;
    }
  }
}
