package com.condor.voidaltars.sql;

import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class SQLConfig {
  private static final String CONFIG_LOC = "plugins/VoidAltars/config.yml";
  private static HashMap<String, String> valMap = new HashMap<>();

  public static void init() {
    // TODO: Make dir and file if not exist
    // TODO: Exception handling
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
