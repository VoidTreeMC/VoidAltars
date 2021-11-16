package com.condor.voidaltars.altar;

import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.Sound;

import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.altars.*;
import com.condor.voidaltars.altar.multiblock.structures.*;
import com.condor.voidaltars.altar.exception.NotInATownException;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.runnable.PlaySparkleEffect;
import com.condor.voidaltars.main.AltarMain;

import com.palmergames.bukkit.towny.object.Town;

public class AltarManager {
  private static TreeMap<AltarType, AltarStructure> altarTypeMap = new TreeMap<>();
  private static TreeMap<UUID, AltarMeta> altarMap = new TreeMap<>();

  public AltarManager() {
    init();
  }

  private void init() {
    altarTypeMap.put(AltarType.FARM_ALTAR, new FarmAltarStructure());
    altarTypeMap.put(AltarType.NETHER_ALTAR, new NetherAltarStructure());
    altarTypeMap.put(AltarType.MINING_ALTAR, new MiningAltarStructure());
    altarTypeMap.put(AltarType.OCEAN_ALTAR, new OceanAltarStructure());
  }

  public static void addAltar(AltarMeta altar) {
    altarMap.put(altar.getUniqueId(), altar);
  }

  public static void clearAltars() {
    altarMap.clear();
  }

  public static AltarStructure getAltarByType(AltarType type) {
    return altarTypeMap.get(type);
  }

  public static AltarMeta getAltarFromTown(Town town) {
    for (AltarMeta altar : altarMap.values()) {
      if (altar.getTown().equals(town)) {
        return altar;
      }
    }
    return null;
  }

  public static AltarMeta getAltar(UUID uuid) {
    return altarMap.get(uuid);
  }

  public static AltarMeta getAltarFromLoc(Location loc, Player player) {
    AltarMeta altarMeta = null;

    for (AltarMeta meta : altarMap.values()) {
      // if (locsAreEqual(meta.getLocation(), loc)) {
      if (meta.getLocation().equals(loc)) {
        // Return true if it's still a valid altar, false otherwise
        return (getStructureFromLoc(loc) != null);
        // altarMeta = meta;
        // break;
      }
    }

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
              altarMeta = new FarmAltar(loc, UUID.randomUUID());
              break;
          }
          altarMap.put(altarMeta.getUniqueId(), altarMeta);
          altarMeta.doEffect();
          // TODO: Make this run on another thread
          SQLLinker.pushToDB(altarMeta);
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
      case 0:
        return 1;
      case 1:
        return 5;
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
