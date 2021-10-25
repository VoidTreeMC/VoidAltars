package com.condor.voidaltars.altar;

import java.util.TreeMap;
import java.util.Collection;
import java.util.logging.Level;
import java.util.Map.Entry;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.boons.*;

public class BoonManager {
  private static TreeMap<BoonType, Boon> boonMap = new TreeMap<>();

  static {
    // boonMap.put(BoonType.HARVEST_BOON, new HarvestBoon());
    boonMap.put(BoonType.RANCHER_BOON, new RancherBoon());
    boonMap.put(BoonType.SENTINEL_BOON, new SentinelBoon());
    boonMap.put(BoonType.NETHER_BOON, new NetherBoon());
    boonMap.put(BoonType.END_BOON, new EndBoon());
  }

  public static Boon getBoonByType(BoonType type) {
    if (type != null) {
      return boonMap.get(type);
    } else {
      return null;
    }
  }

  public static Collection<Boon> getBoons() {
    return boonMap.values();
  }

  public static TreeMap<BoonType, Boon> getMap() {
    return boonMap;
  }

  public static void parseEvent(Event event) {
    for (Entry<BoonType, Boon> boonsByType : boonMap.entrySet()) {
      boonsByType.getValue().eval(event);
    }
  }
}
