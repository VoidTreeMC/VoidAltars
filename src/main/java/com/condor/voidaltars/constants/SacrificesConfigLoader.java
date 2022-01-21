package com.condor.voidaltars.constants;

import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map.Entry;
import java.io.IOException;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.AltarMeta;


public class SacrificesConfigLoader {

  private static final String CONFIG_LOC = "plugins/VoidAltars/sacrifices.yml";
  private static final String MATERIAL_LINE_PRECURSOR = "  - ";
  private static HashMap<AltarType, HashMap<Material, Double>> altarWeightMap = new HashMap<>();

  // TODO: Instantiate default file
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
        // If it's a material entry and we have a context
        if (line.startsWith(MATERIAL_LINE_PRECURSOR) && context != null) {
          line = line.substring(MATERIAL_LINE_PRECURSOR.length(), line.length());
          String[] split = line.split(" ");
          Material mat = Material.valueOf(split[0]);
          try {
            Double weight = Double.parseDouble(split[1]);
            altarWeightMap.get(context).put(mat, weight);
          } catch (NumberFormatException e) {
            Bukkit.getLogger().info("Invalid weight specified on line " + lineNum);
          }
        } else if (!line.startsWith(MATERIAL_LINE_PRECURSOR) && line.endsWith(":")) {
          AltarType type = AltarType.getTypeFromString(line.substring(0, line.length() - 1));
          altarWeightMap.put(type, new HashMap<Material, Double>());
          context = type;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    updateValues();
  }

  public static void updateValues() {
    for (Entry<AltarType, HashMap<Material, Double>> entry : altarWeightMap.entrySet()) {
      try {
        Class metaClass = AltarType.getMetaFromType(entry.getKey());
        Bukkit.getLogger().info("- " + entry.getKey());
        metaClass.getMethod("clearWeightMap").invoke(null);
        for (Entry<Material, Double> weightEntry : entry.getValue().entrySet()) {
          Material mat = weightEntry.getKey();
          Double weight = weightEntry.getValue();
          Class[] parameterTypes = {Material.class, Double.class};
          metaClass.getMethod("addToWeightMap", parameterTypes).invoke(null, mat, weight);
        }
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
