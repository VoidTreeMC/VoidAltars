package com.condor.voidaltars.constants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.multiblock.AltarStructure;

/**
 * Loads the altar_structures.yml config file and
 * imports each altar from the file
 */
public class AltarStructuresLoader {

  private static final String CONFIG_LOC = "plugins/VoidAltars/altar_structures.yml";
  private static final String MATERIAL_LINE_PREFIX = "  - ";
  private static final String CANDLE_LINE_PREFIX = "  CandleType: ";
  private static final String ALTAR_NAME_PREFIX = "  Name: ";
  private static final String ALTAR_INFERFACE_PREFIX = "  InterfaceType: ";
  private static HashMap<AltarType, HashMap<Material, Integer>> altarMatMap = new HashMap<>();
  private static HashMap<AltarType, Material> altarCandleMap = new HashMap<>();
  private static HashMap<AltarType, Material> altarInterfaceBlockMap = new HashMap<>();
  private static HashMap<AltarType, String> altarNameMap = new HashMap<>();

  /**
   * Loads the config file and updates each altar structure
   * TODO: Instantiate default file
   * TODO: Remove some of the repetitive code here
   */
  public static void init() {
    try {
      File file = new File(CONFIG_LOC);
      file.createNewFile();
      Scanner scanner = new Scanner(file);
      AltarType context = null;
      int lineNum = 0;
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        lineNum++;
        // If it's a CandleType line and we have a context
        if (line.startsWith(CANDLE_LINE_PREFIX) && context != null) {
          line = line.substring(CANDLE_LINE_PREFIX.length(), line.length());
          Material candleType = Material.valueOf(line);
          altarCandleMap.put(context, candleType);
        }
        // If it's a material entry and we have a context
        else if (line.startsWith(MATERIAL_LINE_PREFIX) && context != null) {
          line = line.substring(MATERIAL_LINE_PREFIX.length(), line.length());
          String[] split = line.split(" ");
          Material mat = Material.valueOf(split[0]);
          try {
            int blockAmt = Integer.parseInt(split[1]);
            altarMatMap.get(context).put(mat, blockAmt);
          } catch (NumberFormatException e) {
            Bukkit.getLogger().info("Invalid block amount specified on line " + lineNum);
          }
        }
        // If it's a name entry and we have a context
        else if (line.startsWith(ALTAR_NAME_PREFIX) && context != null) {
          line = line.substring(ALTAR_NAME_PREFIX.length(), line.length());
          altarNameMap.put(context, line);
        }
        // If it's an interface block entry and we have a context
        else if (line.startsWith(ALTAR_INFERFACE_PREFIX) && context != null) {
          line = line.substring(ALTAR_INFERFACE_PREFIX.length(), line.length());
          Material interfaceType = Material.valueOf(line);
          altarInterfaceBlockMap.put(context, interfaceType);
        }
        else if (!line.startsWith(MATERIAL_LINE_PREFIX) && line.endsWith(":")) {
          String typeName = line.substring(0, line.length() - 1);
          AltarType type = AltarType.getTypeFromString(typeName);
          if (type == null) {
            type = new AltarType(typeName);
          }
          altarMatMap.put(type, new HashMap<Material, Integer>());
          context = type;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    updateValues();
  }

  /**
   * Loads the altar structures from the parsed
   * text, and represents it in the codebase
   */
  public static void updateValues() {
    for (AltarType type : altarMatMap.keySet()) {
      String altarName = altarNameMap.get(type);
      Material candleType = altarCandleMap.get(type);
      Material interfaceBlock = altarInterfaceBlockMap.get(type);
      HashMap<Material, Integer> matMap = altarMatMap.get(type);
      Bukkit.getLogger().info("Adding altar type: " + altarName);
      AltarStructure struc = new AltarStructure(type, matMap, interfaceBlock, candleType, altarName);
      AltarManager.addAltarType(type, struc);
    }
    altarMatMap.clear();
    altarCandleMap.clear();
    altarInterfaceBlockMap.clear();
    altarNameMap.clear();
  }
}
