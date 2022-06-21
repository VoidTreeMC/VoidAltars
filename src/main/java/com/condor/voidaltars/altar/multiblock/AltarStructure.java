package com.condor.voidaltars.altar.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.condor.voidaltars.altar.AltarType;

/**
 * Represents an altar's physical structure
 */
public class AltarStructure {
  // The map of materials that make up the altar
  private HashMap<Material, Integer> materialMap;
  // The maximum radius in which the altar's blocks are searched for, from the interface block
  private static final int size = 5;
  // The altar's interface block (typically a campfire)
  private Material interfaceBlockType;
  // The altar's candle type
  private Material candleType;
  // The type of the altar
  private AltarType type;
  // The altar structure's name
  private String name;
  // A list of known interface blocks
  private static ArrayList<Material> knownInterfaceBlocks = new ArrayList<>();

  /**
   * Constructor for an altar's structure
   * @param type                The type of the altar
   * @param matMap              A map of materials to quantities of said materials
   * @param interfaceBlockType  The type of interface block that the altar has
   * @param candleType          The type of candle that the altar uses
   * @param Name                The name of the altar type (e.g. Farm Altar)
   */
  public AltarStructure(AltarType type, HashMap<Material, Integer> matMap, Material interfaceBlockType, Material candleType, String name) {
    // If we haven't seen this interface block type before, register it
    if (!isPossibleInterfaceBlock(interfaceBlockType)) {
      knownInterfaceBlocks.add(interfaceBlockType);
    }
    this.type = type;
    this.materialMap = matMap;
    this.candleType = candleType;
    this.interfaceBlockType = interfaceBlockType;
    this.name = name;
  }

  /**
   * Returns true if the block type is a known interface block type
   * @param  mat The material of the block in question
   * @return     True if the block type is a known interface block type, false otherwise
   */
  public static boolean isPossibleInterfaceBlock(Material mat) {
    return knownInterfaceBlocks.contains(mat);
  }

  /**
   * Returns the type of material that is used
   * as the altar interface block
   * @return The altar interface block type
   */
  public Material getInterfaceType() {
    return interfaceBlockType;
  }

  /**
   * Scans the blocks around the location and
   * returns a boolean that indicates whether
   * the structure at that location meets the requirements
   * to be considered an altar
   * TODO: Optimize this by having it count the blocks and end early
   * when it finds all necessary blocks
   * @param  loc The location of the altar interface block
   * @return     True if the structure is a valid altar, False otherwise
   */
  private boolean areBlocksCorrect(Location loc, boolean expectCandles) {
    HashMap<Material, Integer> localBlockMap = new HashMap<>();
    // Iterate through the nearby blocks and count them by type
    for (int i = -size; i < size * 2; i++) {
      for (int j = -size; j < size * 2; j++) {
        for (int k = -size; k < size * 2; k++) {
          Location currLoc = new Location(loc.getWorld(), loc.getX() + i, loc.getY() + j, loc.getZ() + k);
          Material mat = currLoc.getBlock().getType();
          if (mat == Material.AIR) {
            continue;
          }
          if (localBlockMap.get(mat) != null) {
            localBlockMap.put(mat, localBlockMap.get(mat) + 1);
          } else {
            localBlockMap.put(mat, 1);
          }
        }
      }
    }

    boolean allRequirementsMet = true;

    // Iterate through the list of required blocks and check to
    // see that all blocks are required
    for (Map.Entry<Material, Integer> entry : materialMap.entrySet()) {
      Material key = entry.getKey();
      if (expectCandles && key == Material.LIGHTNING_ROD) {
        key = this.getCandleType();
      }
      if (!localBlockMap.containsKey(key)) {
        allRequirementsMet = false;
        break;
      // If they don't have enough of that block
      } else if (localBlockMap.get(key) < entry.getValue()) {
        allRequirementsMet= false;
        break;
      }
    }

    return allRequirementsMet;
  }

  /**
   * Scans the blocks around the location as well as the interface block
   * and returns a boolean that indicates whether
   * the structure at that location meets the requirements
   * to be considered an altar
   * @param  loc                The location of the altar interface block
   * @param  shouldHaveCandles  True if it should have candles, False if it should have lightning rods
   * @return                    True if the structure is a valid altar, False otherwise
   */
  public boolean meetsRequirements(Location loc, boolean shouldHaveCandles) {
    boolean isInterfaceCorrect = loc.getBlock().getType() == interfaceBlockType;
    boolean areBlocksCorrect = isInterfaceCorrect && areBlocksCorrect(loc, shouldHaveCandles);
    return areBlocksCorrect;
  }

  public int getSize() {
    return size;
  }

  public String getName() {
    return this.name;
  }

  public AltarType getType() {
    return this.type;
  }

  public Material getCandleType() {
    return this.candleType;
  }
}
