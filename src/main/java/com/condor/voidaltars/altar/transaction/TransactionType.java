package com.condor.voidaltars.altar.transaction;

/**
 * Represents the various forms of AltarTransaction
 */
public enum TransactionType {
  BUILD,
  DESTROY,
  SACRIFICE,
  BOON,
  LEVEL;

  /**
   * Returns the transaction type associated with the string passed
   * @param  name               The name of the transaction type
   * @return                    The associated TransactionType enum
   */
  public static TransactionType getTypeFromString(String name) {
    for (TransactionType type : TransactionType.values()) {
      if (type.toString().equals(name)) {
        return type;
      }
    }
    return null;
  }
}
