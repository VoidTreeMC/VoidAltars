package com.condor.voidaltars.altar.exception;

import java.lang.Exception;

import org.bukkit.Location;

/**
 * Exception fired when the location is not in a town
 */
public class NotInATownException extends Exception {
  /**
   * Exception fired when the location is not in a town
   * @param loc  The location
   */
  public NotInATownException(Location loc) {
    super("Town not found at " + loc.toString());
  }
}
