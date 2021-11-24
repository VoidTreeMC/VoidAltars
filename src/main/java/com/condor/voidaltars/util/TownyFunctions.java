package com.condor.voidaltars.util;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class TownyFunctions {
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
