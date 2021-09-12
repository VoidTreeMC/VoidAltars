package com.condor.voidaltars.altar;

import java.util.TreeMap;

import org.bukkit.event.Event;
import java.util.Map.Entry;

import com.condor.voidaltars.altar.boons.*;

public class BoonManager {
  private static TreeMap<BoonType, Boon> boonMap = new TreeMap<>();

  static {
    boonMap.put(BoonType.HARVEST_BOON, new HarvestBoon());
  }

  public static Boon getBoonByType(BoonType type) {
    return boonMap.get(type);
  }

  public static void parseEvent(Event event) {
    for (Entry<BoonType, Boon> boonsByType : boonMap.entrySet()) {
      boonsByType.getValue().eval(event);
    }
  }
}
