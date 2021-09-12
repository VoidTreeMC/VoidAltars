package com.condor.voidaltars.altar;

public class BoonManager {
  private static TreeMap<BoonType, Boon> boonMap = new TreeMap<>();

  static {

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
