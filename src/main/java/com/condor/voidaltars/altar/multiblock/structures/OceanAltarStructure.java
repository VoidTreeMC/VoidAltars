package com.condor.voidaltars.altar.multiblock.structures;

import java.util.HashMap;
import java.lang.Integer;

import org.bukkit.Material;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarType;

public class OceanAltarStructure extends AltarStructure {
  private static HashMap<Material, Integer> materialMap = new HashMap<>();
  private static final int size = 5;
  private static final Material interfaceBlockType = Material.SOUL_CAMPFIRE;

  static {
    materialMap.put(Material.WATER, 25);
    materialMap.put(Material.DRIED_KELP_BLOCK, 9);
    materialMap.put(Material.PRISMARINE_WALL, 27);
    materialMap.put(Material.MAGMA_BLOCK, 1);
    materialMap.put(Material.DARK_PRISMARINE_STAIRS, 3);
    materialMap.put(Material.DARK_PRISMARINE, 49);
    materialMap.put(Material.LIGHTNING_ROD, 4);
  }

  public Material getCandleType() {
    return Material.CYAN_CANDLE;
  }

  public OceanAltarStructure() {
    super(AltarType.OCEAN_ALTAR, materialMap, size, interfaceBlockType);
  }
}
