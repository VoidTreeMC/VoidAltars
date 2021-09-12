package com.condor.voidaltars.altar.multiblock.structures;

import java.util.TreeMap;
import java.lang.Integer;

import org.bukkit.Material;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarType;

public class MiningAltarStructure extends AltarStructure {
  private static TreeMap<Material, Integer> materialMap = new TreeMap<>();
  private static final int size = 5;
  private static final Material interfaceBlockType = Material.CAMPFIRE;

  static {
    materialMap.put(Material.DEEPSLATE_IRON_ORE, 25);
    materialMap.put(Material.DEEPSLATE_GOLD_ORE, 9);
    materialMap.put(Material.DEEPSLATE_DIAMOND_ORE, 1);
    materialMap.put(Material.BLACK_CANDLE, 4);
    materialMap.put(Material.COBBLED_DEEPSLATE_WALL, 27);
    materialMap.put(Material.COBBLED_DEEPSLATE_STAIRS, 3);
  }

  public MiningAltarStructure() {
    super(AltarType.MINING_ALTAR, materialMap, size, interfaceBlockType);
  }
}
