package com.condor.voidaltars.altar.multiblock.structures;

import java.util.HashMap;
import java.lang.Integer;

import org.bukkit.Material;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.AltarType;

public class NetherAltarStructure extends AltarStructure {
  private static HashMap<Material, Integer> materialMap = new HashMap<>();
  private static final int size = 5;
  private static final Material interfaceBlockType = Material.SOUL_CAMPFIRE;

  static {
    materialMap.put(Material.WARPED_PLANKS, 25);
    materialMap.put(Material.RED_NETHER_BRICK_STAIRS, 3);
    materialMap.put(Material.RED_NETHER_BRICK_WALL, 27);
    materialMap.put(Material.NETHER_BRICKS, 9);
    materialMap.put(Material.GLOWSTONE, 1);
    materialMap.put(Material.LIGHTNING_ROD, 4);
  }

  public Material getCandleType() {
    return Material.CYAN_CANDLE;
  }

  public NetherAltarStructure() {
    super(AltarType.NETHER_ALTAR, materialMap, size, interfaceBlockType);
  }
}
