package com.condor.voidaltars.altar.transaction;

public enum TransactionType {
  BUILD,
  DESTROY,
  SACRIFICE,
  BOON,
  LEVEL;

  public static TransactionType getTypeFromString(String name) {
    for (TransactionType type : TransactionType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
