package com.condor.voidaltars.sql;

import java.util.TreeMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class SQLConfig {
  private static TreeMap<String, String> valMap = new TreeMap<>();

  public static void init() {
    // TODO: Make dir and file if not exist
    // TODO: Exception handling
    try {
      Scanner scanner = new Scanner(new File("plugins/VoidAltars/config.yml"));
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] arr = line.split(": ");
        valMap.put(arr[0], arr[1]);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static String getVal(String key) {
    return valMap.get(key);
  }
}
