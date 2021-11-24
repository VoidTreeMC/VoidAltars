package com.condor.voidaltars.altar;

import java.util.HashMap;
import java.util.Collection;
import java.util.logging.Level;
import java.util.Map.Entry;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.boons.*;

public class BoonManager {
  private static HashMap<BoonType, Boon> boonMap = new HashMap<>();

  static {
    // boonMap.put(BoonType.HARVEST_BOON, new HarvestBoon());
    boonMap.put(BoonType.RANCHER_BOON, new RancherBoon());
    boonMap.put(BoonType.SENTINEL_BOON, new SentinelBoon());
    boonMap.put(BoonType.NETHER_BOON, new NetherBoon());
    boonMap.put(BoonType.END_BOON, new EndBoon());
    boonMap.put(BoonType.FORGE_BOON, new ForgeBoon());
    boonMap.put(BoonType.BEEKEEPER_BOON, new BeekeeperBoon());
    boonMap.put(BoonType.FREEZE_BOON, new FreezeBoon());
    boonMap.put(BoonType.THAW_BOON, new ThawBoon());
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

  public static HashMap<BoonType, Boon> getMap() {
    return boonMap;
  }

  public static void parseEvent(Event event) {
    for (Entry<BoonType, Boon> boonsByType : boonMap.entrySet()) {
      boonsByType.getValue().eval(event);
    }
  }
}
