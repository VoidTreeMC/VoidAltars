package com.condor.voidaltars.altar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.event.Event;

import com.condor.voidaltars.altar.boons.BeekeeperBoon;
import com.condor.voidaltars.altar.boons.EndBoon;
import com.condor.voidaltars.altar.boons.ForgeBoon;
import com.condor.voidaltars.altar.boons.FreezeBoon;
import com.condor.voidaltars.altar.boons.HarvestBoon;
import com.condor.voidaltars.altar.boons.NetherBoon;
import com.condor.voidaltars.altar.boons.PeaceBoon;
import com.condor.voidaltars.altar.boons.PestControlBoon;
import com.condor.voidaltars.altar.boons.RancherBoon;
import com.condor.voidaltars.altar.boons.SentinelBoon;
import com.condor.voidaltars.altar.boons.SlimeBoon;
import com.condor.voidaltars.altar.boons.ThawBoon;

/**
 * Class that maps boon types to their
 * boon objects
 */
public class BoonManager {
  private static HashMap<BoonType, Boon> boonMap = new HashMap<>();

  static {
    boonMap.put(BoonType.HARVEST_BOON, new HarvestBoon());
    boonMap.put(BoonType.RANCHER_BOON, new RancherBoon());
    boonMap.put(BoonType.SENTINEL_BOON, new SentinelBoon());
    boonMap.put(BoonType.NETHER_BOON, new NetherBoon());
    boonMap.put(BoonType.END_BOON, new EndBoon());
    boonMap.put(BoonType.FORGE_BOON, new ForgeBoon());
    boonMap.put(BoonType.BEEKEEPER_BOON, new BeekeeperBoon());
    boonMap.put(BoonType.FREEZE_BOON, new FreezeBoon());
    boonMap.put(BoonType.THAW_BOON, new ThawBoon());
    boonMap.put(BoonType.PEACE_BOON, new PeaceBoon());
    boonMap.put(BoonType.ANTIPEST_BOON, new PestControlBoon());
    boonMap.put(BoonType.SLIME_BOON, new SlimeBoon());
  }

  /**
   * Gets the boon object associated with
   * the provided boon type
   * @param  type               The type of boon
   * @return                    The boon associated with that type
   */
  public static Boon getBoonByType(BoonType type) {
    if (type != null) {
      return boonMap.get(type);
    } else {
      return null;
    }
  }

  /**
   * Gets a collection of all boons
   * @return A collection of all boons
   */
  public static Collection<Boon> getBoons() {
    return boonMap.values();
  }

  /**
   * Gets the map of boon types to boons
   * @return The map of boon types to boons
   */
  public static HashMap<BoonType, Boon> getMap() {
    return boonMap;
  }

  /**
   * Parses an event for all relevant boons
   * @param event  The event to be parsed
   */
  public static void parseEvent(Event event) {
    for (Entry<BoonType, Boon> boonsByType : boonMap.entrySet()) {
      boonsByType.getValue().eval(event);
    }
  }
}
