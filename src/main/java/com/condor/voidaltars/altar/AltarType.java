package com.condor.voidaltars.altar;

import java.util.HashMap;
import java.lang.Comparable;

/**
 * Represents a type of altar
 * TODO: Change the internal representation of this
 * to use something besides string comparison, in order
 * to speed it up
 */
public class AltarType implements Comparable {
  private static HashMap<String, AltarType> typeMap = new HashMap<>();

  private String type;

  public AltarType(String type) {
    this.type = type;
    typeMap.put(type, this);
  }

  public boolean equals(AltarType otherType) {
    return this.type.equals(otherType.toString());
  }

  @Override
  public int compareTo(Object otherType) {
    if (otherType instanceof AltarType) {
      return this.type.compareTo(((AltarType) otherType).toString());
    } else {
      return -1;
    }
  }

  public String toString() {
    return this.type;
  }

  /**
   * Returns the type of altar associated with
   * the name provided
   * @param  name               The name of the altar type
   * @return                    The altar type
   */
  public static AltarType getTypeFromString(String name) {
    return typeMap.get(name);
  }
}
