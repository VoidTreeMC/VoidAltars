package condor.altar.exception;

import java.lang.Exception;

import org.bukkit.Location;

public class NotInATownException extends Exception {
  public NotInATownException(Location loc) {
    super("Town not found at " + loc.toString());
  }
}
