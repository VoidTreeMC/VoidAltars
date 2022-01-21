package com.condor.voidaltars.altar;

import java.util.HashMap;
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
import com.condor.voidaltars.altar.exception.WrongTownException;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.runnable.PlaySparkleEffect;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.altar.transaction.BuildTransaction;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;

public class AltarManager {
  private static HashMap<AltarType, AltarStructure> altarTypeMap = new HashMap<>();
  private static HashMap<UUID, TownAltarLink> altarLinkMap = new HashMap<>();

  public AltarManager() {
    init();
  }

  private void init() {
    altarTypeMap.put(AltarType.FARM_ALTAR, new FarmAltarStructure());
    altarTypeMap.put(AltarType.NETHER_ALTAR, new NetherAltarStructure());
    altarTypeMap.put(AltarType.MINING_ALTAR, new MiningAltarStructure());
    altarTypeMap.put(AltarType.OCEAN_ALTAR, new OceanAltarStructure());
  }

  public static void addAltarLink(TownAltarLink link) {
    altarLinkMap.put(link.getUniqueId(), link);
  }

  public static TownAltarLink getAltarLink(UUID uuid) {
    return altarLinkMap.get(uuid);
  }

  public static void clearAltarLinks() {
    altarLinkMap.clear();
  }

  public static AltarStructure getAltarByType(AltarType type) {
    return altarTypeMap.get(type);
  }

  public static TownAltarLink getAltarLinkFromTown(Town town) {
    for (TownAltarLink link : altarLinkMap.values()) {
      if (town.equals(link.getTown())) {
        return link;
      }
    }
    return null;
  }

  // TODO: Refactor this to make it a bit cleaner.
  // Separate it into two sections: Looking for existing altar, creating new altar
  public static AltarMeta getAltarFromLoc(Location loc, Player player) {
    AltarMeta altarMeta = null;

    TownBlock locTB = TownyAPI.getInstance().getTownBlock(loc);
    if (locTB == null) {
      return null;
    }

    for (TownAltarLink link : altarLinkMap.values()) {
      // if (locsAreEqual(meta.getLocation(), loc)) {
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
              // If the original location still contains a campfire and is the same type of altar
              if (meta.getStructure().isPossibleInterfaceBlock(meta.getLocation().getBlock().getType()) && typeAtLoc.equals(metaType)) {
                player.sendMessage(StringConstants.NO_DUPLICATE_ALTARS.get());
              // If the original location's campfire is gone and they're the same type of altar, update its location
              } else if (!meta.getStructure().isPossibleInterfaceBlock(meta.getLocation().getBlock().getType()) && typeAtLoc.equals(metaType)) {
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
      player.sendMessage(StringConstants.NO_PERMISSIONS_TO_CREATE_ALTAR.get());
      return null;
    }

    // If there's no known altar at this location
    if (altarMeta == null) {
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
          switch (struc.getType()) {
            case MINING_ALTAR:
              altarMeta = new MiningAltar(link, loc, UUID.randomUUID());
              break;
            case OCEAN_ALTAR:
              altarMeta = new OceanAltar(link, loc, UUID.randomUUID());
              break;
            case NETHER_ALTAR:
              altarMeta = new NetherAltar(link, loc, UUID.randomUUID());
              break;
            case FARM_ALTAR:
            default:
              altarMeta = new FarmAltar(link, loc, UUID.randomUUID());
              break;
          }
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
    } else {
      return altarMeta;
    }
  }

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
