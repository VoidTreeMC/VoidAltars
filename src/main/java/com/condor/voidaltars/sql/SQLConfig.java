package com.condor.voidaltars.sql;

import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * Loads the config.yml config file which contains
 * details and credentials for the SQL connection
 */
public class SQLConfig {
  private static final String CONFIG_LOC = "plugins/VoidAltars/config.yml";
  private static HashMap<String, String> valMap = new HashMap<>();

  /**
   * Loads the config file and updates the connection
   * details in the value map
   * TODO: Instantiate default file
   * TODO: Exception handling
   */
  public static void init() {
    try {
      File file = new File(CONFIG_LOC);
      file.createNewFile();
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] arr = line.split(": ");
        valMap.put(arr[0], arr[1]);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getVal(String key) {
    return valMap.get(key);
  }
}
