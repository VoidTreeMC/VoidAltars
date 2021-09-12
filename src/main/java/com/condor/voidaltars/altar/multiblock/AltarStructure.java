package com.condor.voidaltars.altar.multiblock;

import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.AltarType;

public abstract class AltarStructure {
  private TreeMap<Material, Integer> materialMap;
  private int size;
  private Material interfaceBlockType;
  private AltarType type;
  private static ArrayList<Material> knownInterfaceBlocks = new ArrayList<>();


  public AltarStructure(AltarType type, TreeMap<Material, Integer> matMap, int size, Material interfaceBlockType) {
    // If we haven't seen this interface block type before, register it
    if (!isPossibleInterfaceBlock(interfaceBlockType)) {
      knownInterfaceBlocks.add(interfaceBlockType);
    }
    this.type = type;
    this.materialMap = matMap;
    this.size = size;
    this.interfaceBlockType = interfaceBlockType;
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
  private boolean areBlocksCorrect(Location loc) {
    TreeMap<Material, Integer> localBlockMap = new TreeMap<>();
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
      if (!localBlockMap.containsKey(entry.getKey())) {
        System.out.println("Altar is missing " + entry.getKey());
        allRequirementsMet = false;
        break;
      }

      // If they don't have enough of that block
      if (localBlockMap.get(entry.getKey()) < entry.getValue()) {
        System.out.println("Altar has " + localBlockMap.get(entry.getKey()) + " " + entry.getKey() + ", but needs " + entry.getValue());
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
   * @param  loc The location of the altar interface block
   * @return     True if the structure is a valid altar, False otherwise
   */
  public boolean meetsRequirements(Location loc) {
    boolean isInterfaceCorrect = loc.getBlock().getType() == interfaceBlockType;
    boolean areBlocksCorrect = isInterfaceCorrect && areBlocksCorrect(loc);
    return areBlocksCorrect;
  }

  public int getSize() {
    return this.size;
  }

  public AltarType getType() {
    return this.type;
  }
}
