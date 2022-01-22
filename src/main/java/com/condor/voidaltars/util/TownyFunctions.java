package com.condor.voidaltars.util;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

/**
 * Utility class that contains functions to be
 * used in tandem with Towny
 */
public class TownyFunctions {
  /**
   * Returns the town located at the specified location
   * @param  loc               The location
   * @return                   The town that owns that location
   */
  public static Town getTownFromLocation(Location loc) {
    if (loc != null) {
      TownBlock tb = TownyAPI.getInstance().getTownBlock(loc);
      if (tb != null) {
        try {
          Town town = tb.getTown();
          return town;
        } catch (NotRegisteredException e) {
          return null;
        }
      }
    }
    return null;
  }
}
