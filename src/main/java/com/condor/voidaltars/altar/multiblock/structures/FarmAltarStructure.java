package com.condor.voidaltars.altar.multiblock.structures;

import java.util.HashMap;
import java.lang.Integer;

import org.bukkit.Material;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarType;

public class FarmAltarStructure extends AltarStructure {
  private static HashMap<Material, Integer> materialMap = new HashMap<>();
  private static final int size = 5;
  private static final Material interfaceBlockType = Material.CAMPFIRE;

  static {
    materialMap.put(Material.ACACIA_PLANKS, 25);
    materialMap.put(Material.ACACIA_STAIRS, 3);
    materialMap.put(Material.ACACIA_FENCE, 27);
    materialMap.put(Material.HAY_BLOCK, 9);
    materialMap.put(Material.BEE_NEST, 1);
    materialMap.put(Material.LIGHTNING_ROD, 4);
  }

  public Material getCandleType() {
    return Material.YELLOW_CANDLE;
  }

  public FarmAltarStructure() {
    super(AltarType.FARM_ALTAR, materialMap, size, interfaceBlockType);
  }
}
