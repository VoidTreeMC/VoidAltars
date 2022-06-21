package com.condor.voidaltars.altar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.condor.voidaltars.altar.exception.NotInATownException;
import com.condor.voidaltars.altar.exception.WrongTownException;
import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.transaction.BuildTransaction;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.util.TownyFunctions;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

/**
 * Provides a variety of altar utilities,
 * including mapping altar types to their structures,
 * town UUIDs to their town-altar links,
 * and performs event-handling for altar clicks
 */
public class AltarManager {
  private static HashMap<AltarType, AltarStructure> altarTypeMap = new HashMap<>();
  private static HashMap<UUID, TownAltarLink> altarLinkMap = new HashMap<>();
  private static HashMap<AltarType, HashMap<Material, Double>> weightMapMap = new HashMap<>();

  public static String getAltarName(AltarType type) {
    if (altarTypeMap.get(type) == null) {
      return "ERROR";
    }
    return altarTypeMap.get(type).getName();
  }

  public static void addAltarType(AltarType type, AltarStructure struc) {
    altarTypeMap.put(type, struc);
  }

  /**
   * Registers a new town-altar link with the map
   * @param link  The town-altar link
   */
  public static void addAltarLink(TownAltarLink link) {
    altarLinkMap.put(link.getUniqueId(), link);
  }

  /**
   * Gets the altar link associated with the town UUID
   * @param  uuid               The town's UUID
   * @return                    The town's altar link
   */
  public static TownAltarLink getAltarLink(UUID uuid) {
    return altarLinkMap.get(uuid);
  }

  /**
   * Clears the map of altar links
   */
  public static void clearAltarLinks() {
    altarLinkMap.clear();
  }

  /**
   * Clears the map of altar types/structures
   */
  public static void clearTypeMap() {
    altarTypeMap.clear();
  }

  /**
   * Clears the map of altar sacrifice weights
   */
  public static void clearWeightMapMap() {
    weightMapMap.clear();
  }

  /**
   * Gets the weight map for this altar type
   * @param  type The type of altar whose weight map is being requested
   * @return      The weight map for this altar
   */
  public static HashMap<Material, Double> getWeightMap(AltarType type) {
    return weightMapMap.get(type);
  }

  /**
   * Sets the weight map for the altar type
   * provided, to the weight map provided
   * @param type  The altar type
   * @param map   The new weight map
   */
  public static void setWeightMap(AltarType type, HashMap<Material, Double> map) {
    weightMapMap.put(type, map);
  }

  public static List<Material> getSacrificeTypes(AltarType type) {
    if (weightMapMap.get(type) == null) {
      Bukkit.getLogger().warning("Tried to get sacrifice types for " + type + ", found none.");
      return null;
    }
    ArrayList<Material> matList = new ArrayList<>();
    for (Material m : weightMapMap.get(type).keySet()) {
      matList.add(m);
    }
    return matList;
  }

  /**
   * Gets the altar structure associated with the type
   * @param  type               The type of altar
   * @return                    The altar's structure
   */
  public static AltarStructure getAltarByType(AltarType type) {
    return altarTypeMap.get(type);
  }

  /**
   * Gets the town-altar link associated with the town
   * @param  town               The town whose link to get
   * @return                    The town's altar link
   */
  public static TownAltarLink getAltarLinkFromTown(Town town) {
    for (TownAltarLink link : altarLinkMap.values()) {
      if (town.equals(link.getTown())) {
        return link;
      }
    }
    return null;
  }

  /**
   * Gets the altar clicked by the player. Creates a new
   * altar if it does not exist and it is valid to do so.
   * Checks to verify that the altar is still valid, and
   * performs permission-checking
   * @param  loc                  The location of the click
   * @param  player               The player who clicked
   * @return                      The altar located at that location
   * TODO: Refactor this to make it cleaner
   */
  public static AltarMeta getAltarFromLoc(Location loc, Player player) {
    AltarMeta altarMeta = null;

    TownBlock locTB = TownyAPI.getInstance().getTownBlock(loc);
    if (locTB == null) {
      return null;
    }

    for (TownAltarLink link : altarLinkMap.values()) {
      // if (locsAreEqual(meta.getLocation(), loc)) {
      if (!link.getTown().equals(TownyFunctions.getTownFromLocation(loc))) {
        continue;
      }
      // Check if the altar is still in the town it's supposed to be in
      for (AltarMeta meta : link.getAltars()) {
        try {
          TownBlock tb = TownyAPI.getInstance().getTownBlock(meta.getLocation());
          if (tb != null && tb.getTown().equals(meta.getTown())) {
            if (meta.getLocation().equals(loc)) {
              // If it's no longer a valid altar
              if (getStructureFromLoc(loc, true) == null) {
                player.sendMessage(StringConstants.ALTAR_NO_LONGER_VALID.get());
              // If it's still a valid altar and the same type
              } else if (getStructureFromLoc(loc, true).getType().equals(meta.getType())) {
                return meta;
              } else {
                // If it's a different type of altar, just continue.
                continue;
              }
              // If this campfire is somewhere else, but it's still a valid altar
            } else if (getStructureFromLoc(loc, true) != null) {
              AltarType typeAtLoc = getStructureFromLoc(loc, true).getType();
              AltarType metaType = meta.getType();
              meta.getStructure();
              // If the original location still contains a campfire and is the same type of altar
              if (AltarStructure.isPossibleInterfaceBlock(meta.getLocation().getBlock().getType()) && typeAtLoc.equals(metaType)) {
                player.sendMessage(StringConstants.NO_DUPLICATE_ALTARS.get());
              // If the original location's campfire is gone and they're the same type of altar, update its location
              } else if (!AltarStructure.isPossibleInterfaceBlock(meta.getLocation().getBlock().getType()) && typeAtLoc.equals(metaType)) {
                Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
                if (resident == null || (!resident.isMayor() && !resident.hasTownRank("high-priest"))) {
                  player.sendMessage(StringConstants.NO_PERMISSIONS_TO_CREATE_ALTAR.get());
                  return null;
                }
                player.sendMessage(StringConstants.ALTAR_HAS_BEEN_MOVED.get());
                meta.setLocation(loc);
                BuildTransaction transac = new BuildTransaction(meta.getUniqueId(), meta.getLink(), player.getUniqueId(),
                                                                UUID.randomUUID(), System.currentTimeMillis(), loc.getWorld().toString(),
                                                                (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
                meta.getLink().getTransacCache().store(transac);
                return meta;
              }
            }
          }
        } catch (NotRegisteredException e) {
          // It's not in a town. This altar's town unclaimed the chunk and it is thus deactivated. Return null.
          return null;
        }
      }
    }

    AltarStructure struc = getStructureFromLoc(loc, false);

    Town town = null;
    Resident resident = null;
    resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
    if (locTB != null) {
      try {
        town = locTB.getTown();
        if (town != null) {
          if (resident.getTown() == null || !resident.getTown().getUUID().equals(town.getUUID())) {
            player.sendMessage(StringConstants.MUST_BE_IN_TOWN_TO_CREATE_ALTAR_THERE.get());
            return null;
          }
        }
      } catch (NotRegisteredException e) {
        // Ignore
      }
    }

    if (resident == null || (!resident.isMayor() && !resident.hasTownRank("high-priest"))) {
      if (getStructureFromLoc(loc, true) != null || getStructureFromLoc(loc, false) != null) {
        player.sendMessage(StringConstants.NO_PERMISSIONS_TO_CREATE_ALTAR.get());
        return null;
      }
    }

    // If it's not a valid altar, we return null.
    if (struc == null) {
      return null;
    // If it IS a valid altar but we dont know about it,
    // we create a new altar meta for it and add it to the map
    } else {
      try {
        // If there's already an altar of that type in that town, return null
        if (town != null) {
          TownAltarLink link = AltarManager.getAltarLink(town.getUUID());
          if (link == null) {
            link = new TownAltarLink(town);
          }
          AltarMeta townAltar = link.getAltar(struc.getType());
          if (townAltar != null) {
            player.sendMessage(StringConstants.NO_DUPLICATE_ALTARS.get());
            return null;
          }
        }
        TownAltarLink link = AltarManager.getAltarLink(town.getUUID());
        if (link == null) {
          link = new TownAltarLink(town);
        }
        // altarMeta = new AltarMeta(struc.getType(), link, loc, UUID.randomUUID());
        altarMeta = new AltarMeta(link, struc.getType(), UUID.randomUUID(), loc, struc, getWeightMap(struc.getType()));
        altarMeta.doEffect();
        link.addAltar(altarMeta.getType(), altarMeta);
        BuildTransaction transac = new BuildTransaction(altarMeta.getUniqueId(), altarMeta.getLink(), player.getUniqueId(),
                                                        UUID.randomUUID(), System.currentTimeMillis(), loc.getWorld().toString(),
                                                        (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        altarMeta.getLink().getTransacCache().store(transac);
        return altarMeta;
      } catch (NotInATownException | WrongTownException e) {
        // They're not in a town. Ignore it.
        player.sendMessage(StringConstants.NO_ALTARS_IN_THE_WILD.get());
        return null;
      }
    }
  // If we found the altar, return it
  }

  /**
   * Gets the altar structure located at the location
   * Returns null if invalid
   * @param  loc                             The location of the structure
   * @param  shouldHaveCandles               Whether the structure should have candles or not
   * @return                                 The altar's structure, or null if invalid altar
   */
  public static AltarStructure getStructureFromLoc(Location loc, boolean shouldHaveCandles) {
    AltarStructure struc = null;
    for (AltarStructure altarStructure : altarTypeMap.values()) {
      if (altarStructure.meetsRequirements(loc, shouldHaveCandles)) {
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
