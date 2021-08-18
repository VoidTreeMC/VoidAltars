package condor.altar;

import java.util.TreeMap;

import org.bukkit.Location;

import condor.altar.AltarType;
import condor.altar.multiblock.AltarStructure;
import condor.altar.multiblock.structures.*;

public class AltarManager {
  private static TreeMap<AltarType, AltarStructure> altarTypeMap = new TreeMap<>();

  public AltarManager() {
    init();
  }

  private void init() {
    altarTypeMap.put(AltarType.FARM_ALTAR, new FarmAltarStructure());
    altarTypeMap.put(AltarType.NETHER_ALTAR, new NetherAltarStructure());
    altarTypeMap.put(AltarType.MINING_ALTAR, new MiningAltarStructure());
    altarTypeMap.put(AltarType.OCEAN_ALTAR, new OceanAltarStructure());
  }

  public static AltarStructure getAltarByType(AltarType type) {
    return altarTypeMap.get(type);
  }

  public static AltarStructure getAltarFromLoc(Location loc) {
    AltarStructure struc = null;

    for (AltarStructure altarStructure : altarTypeMap.values()) {
      if (altarStructure.meetsRequirements(loc)) {
        struc = altarStructure;
        break;
      }
    }

    return struc;
  }
}
