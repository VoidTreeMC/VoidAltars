package condor.altar.multiblock.structures;

import java.util.TreeMap;
import java.lang.Integer;

import org.bukkit.Material;

import condor.altar.multiblock.AltarStructure;
import condor.altar.AltarType;

public class FarmAltarStructure extends AltarStructure {
  private static TreeMap<Material, Integer> materialMap = new TreeMap<>();
  private static final int size = 5;
  private static final Material interfaceBlockType = Material.CAMPFIRE;

  static {
    materialMap.put(Material.ACACIA_PLANKS, 25);
    materialMap.put(Material.ACACIA_STAIRS, 3);
    materialMap.put(Material.ACACIA_FENCE, 27);
    materialMap.put(Material.HAY_BLOCK, 9);
    materialMap.put(Material.BEE_NEST, 1);
    materialMap.put(Material.YELLOW_CANDLE, 4);
  }

  public FarmAltarStructure() {
    super(AltarType.FARM_ALTAR, materialMap, size, interfaceBlockType);
  }
}
