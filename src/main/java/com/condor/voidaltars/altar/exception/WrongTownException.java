package com.condor.voidaltars.altar.exception;

import java.lang.Exception;
import java.util.UUID;

/**
 * Exception fired when an altar is in one
 * town but registered with another
 */
public class WrongTownException extends Exception {
  /**
   * Exception fired when an altar is in one
   * town but registered with another
   * @param townUUID  The UUID of the town in which the altar is geographically located
   * @param linkUUID  The UUID of the town with which the altar is registered
   */
  public WrongTownException(UUID townUUID, UUID linkUUID) {
    super("Altar was registered with town " + linkUUID + " but it is located in " + townUUID);
  }
}
