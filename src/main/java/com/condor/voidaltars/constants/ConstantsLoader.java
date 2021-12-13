package com.condor.voidaltars.constants;

import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map.Entry;
import java.io.IOException;

import org.bukkit.Bukkit;

public class ConstantsLoader {

  private static final String CONFIG_LOC = "plugins/VoidAltars/text.yml";
  // Key: A string representation of the enum in StringConstants
  // Value: The new version of the string
  private static HashMap<String, String> valMap = new HashMap<>();
  // Key: A string representation of the enum in StringListConstants
  // Value: The new version of the string-list
  // TODO: Change the protocol for these to be easier for humans to read
  private static HashMap<String, String[]> listValMap = new HashMap<>();

  // TODO: Instantiate default file
  public static void init() {
    try {
      File file = new File(CONFIG_LOC);
      file.createNewFile();
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] arr = line.split(": ");
        // This has to be 4 \'s because it's being parsed as regex as well
        String[] splitAsList = arr[1].split("\\\\n");
        if (splitAsList.length <= 1) {
          valMap.put(arr[0], arr[1]);
        } else {
          listValMap.put(arr[0], splitAsList);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    updateValues();
  }

  public static void updateValues() {
    for (Entry<String, String> entry : valMap.entrySet()) {
      try {
        StringConstants toUpdate = StringConstants.valueOf(entry.getKey());
        toUpdate.set(entry.getValue());
      } catch (IllegalArgumentException e) {
        // Ignore
        // Bukkit.getLogger().warning("Malformed key detected in " + CONFIG_LOC + ": " + entry.getKey());
      }
    }

    for (Entry<String, String[]> entry : listValMap.entrySet()) {
      try {
        StringListConstants toUpdate = StringListConstants.valueOf(entry.getKey());
        toUpdate.set(entry.getValue());
      } catch (IllegalArgumentException e) {
        // Ignore
        // Bukkit.getLogger().warning("Malformed key detected in " + CONFIG_LOC + ": " + entry.getKey());
      }
    }
  }

  public static String getVal(String key) {
    return valMap.get(key);
  }
}
