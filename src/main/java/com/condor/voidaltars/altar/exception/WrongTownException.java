package com.condor.voidaltars.altar.exception;

import java.lang.Exception;
import java.util.UUID;

import org.bukkit.Location;

public class WrongTownException extends Exception {
  public WrongTownException(UUID townUUID, UUID linkUUID) {
    super("Altar was registered with town " + linkUUID + " but it is located in " + townUUID);
  }
}
