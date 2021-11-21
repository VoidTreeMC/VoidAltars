package com.condor.voidaltars.altar.multiblock.structures;

import java.util.HashMap;
import java.lang.Integer;

import org.bukkit.Material;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarType;

public class MiningAltarStructure extends AltarStructure {
  private static HashMap<Material, Integer> materialMap = new HashMap<>();
  private static final int size = 5;
  private static final Material interfaceBlockType = Material.CAMPFIRE;

  static {
    materialMap.put(Material.DEEPSLATE_IRON_ORE, 25);
    materialMap.put(Material.DEEPSLATE_GOLD_ORE, 9);
    materialMap.put(Material.DEEPSLATE_DIAMOND_ORE, 1);
    materialMap.put(Material.LIGHTNING_ROD, 4);
    materialMap.put(Material.COBBLED_DEEPSLATE_WALL, 27);
    materialMap.put(Material.COBBLED_DEEPSLATE_STAIRS, 3);
  }

  public Material getCandleType() {
    return Material.BLACK_CANDLE;
  }

  public MiningAltarStructure() {
    super(AltarType.MINING_ALTAR, materialMap, size, interfaceBlockType);
  }
}
