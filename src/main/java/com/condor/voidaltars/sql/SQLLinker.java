package com.condor.voidaltars.sql;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.UUID;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.mysql.cj.jdbc.MysqlDataSource;

import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.sql.SQLConfig;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.leaderboard.AltarRank;
import com.condor.voidaltars.altar.SettingsType;
import com.condor.voidaltars.altar.AltarSettings;
import com.condor.voidaltars.altar.settings.OutsiderSacrificeSetting;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

/**
 * Utility class that performs most of the SQL interactions
 */
public class SQLLinker {

  // The SQL connection
  private static Connection conn;

  // The number of sacrifices and boons
  private static final int NUM_SACRIFICES_AND_BOONS = 4;

  private static final int month = LocalDate.now().getMonthValue();

  /**
   * Initializes a connection with the host
   */
  public static void initHost() {
		try {
	    String url = SQLConfig.getVal("jdbc-url");
	    String username = SQLConfig.getVal("jdbc-user");
	    String password = SQLConfig.getVal("jdbc-password");

      conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  /**
   * Probes the connection to see if it is still active,
   * and reinitializes it if it is inactive
   */
  public static void probeConnection() {
    try {
      PreparedStatement stmt = conn.prepareStatement("Select * FROM AltarTable");
      stmt.executeQuery();
    } catch (Exception e) {
      Bukkit.getLogger().log(Level.INFO, "Connection probe failed. Re-establishing connection and re-probing.");
      initHost();
      probeConnection();
    }
  }

  /**
   * Gets the connection object to the SQL server
   * @return A Connection object to the SQL server
   */
  public static Connection getConn() {
    probeConnection();
    return conn;
  }

  /**
   * Pulls all data from the database and uses it to
   * reconstruct the town-altar links, altars, and their
   * settings
   */
  public static void pullFromDB() {
    probeConnection();
    Bukkit.getLogger().log(Level.INFO, "Fetching altar links from DB...");
    ArrayList<TownAltarLink> toDelevel = new ArrayList<>();
    try {
      PreparedStatement tatStmt = conn.prepareStatement("SELECT * FROM TownAltarTable;");
      ResultSet tatResults = tatStmt.executeQuery();
      boolean rsnext = tatResults.next();
      AltarManager.clearAltarLinks();
      while (rsnext) {
        UUID townUUID = UUID.fromString(tatResults.getString("town_uuid"));
        int level = tatResults.getInt("level");
        ArrayList<String> boonList = new ArrayList<>();
        for (int i = 1; i <= NUM_SACRIFICES_AND_BOONS; i++) {
          boonList.add(tatResults.getString("boon_" + i));
        }
        int totalRecentSacrifices = tatResults.getInt("total_recent_sacrifices");
        int totalSacrificesMade = tatResults.getInt("total_sacrifices_made");
        long nextEvalTime = tatResults.getLong("next_eval_time");
        try {
         Town town = TownyAPI.getInstance().getDataSource().getTown(townUUID);
        } catch (NotRegisteredException e) {
         Bukkit.getLogger().info("The town " + townUUID + " is no longer registered. Purging from database.");
         SQLLinker.removeAltarByTownUUID(townUUID);
         rsnext = tatResults.next();
         continue;
        }

        TownAltarLink altarLink = new TownAltarLink(townUUID, level, boonList, totalRecentSacrifices, totalSacrificesMade, nextEvalTime);
        if (!altarLink.hasMetQuota()) {
          toDelevel.add(altarLink);
        }
        rsnext = tatResults.next();
      }

      Bukkit.getLogger().log(Level.INFO, "Fetching altars from DB...");

      PreparedStatement altarStmt = conn.prepareStatement("SELECT * FROM AltarTable;");
      ResultSet altarResults = altarStmt.executeQuery();
      rsnext = altarResults.next();
      while (rsnext) {
        UUID uuid = UUID.fromString(altarResults.getString("uuid"));
        UUID townUUID = UUID.fromString(altarResults.getString("town_uuid"));
        String type = altarResults.getString("type");
        String worldStr = altarResults.getString("world");
        double x = (double) altarResults.getInt("x");
        double y = (double) altarResults.getInt("y");
        double z = (double) altarResults.getInt("z");
        ArrayList<byte[]> sacrificeList = new ArrayList<>();
        for (int i = 1; i <= NUM_SACRIFICES_AND_BOONS; i++) {
          sacrificeList.add(altarResults.getBytes("sacrifice_" + i));
        }

        AltarMeta altar = AltarMeta.create(uuid, type, townUUID, worldStr, x, y, z, sacrificeList);
        rsnext = altarResults.next();
      }

      Bukkit.getLogger().log(Level.INFO, "Fetching altar settings from DB...");
      PreparedStatement settingsStmt = conn.prepareStatement("SELECT * FROM AltarSettingsTable;");
      ResultSet settingsResults = settingsStmt.executeQuery();
      rsnext = settingsResults.next();
      while (rsnext) {
        UUID townSettingsUUID = UUID.fromString(settingsResults.getString("town_uuid"));
        boolean outsidersSacrifice = settingsResults.getBoolean("outsiders_sacrifice");
        HashMap<SettingsType, AltarSettings> settingsMap = new HashMap<>();
        settingsMap.put(SettingsType.OUTSIDER_SACRIFICES, new OutsiderSacrificeSetting(outsidersSacrifice));
        TownAltarLink settingsLink = AltarManager.getAltarLink(townSettingsUUID);
        if (settingsLink != null) {
          settingsLink.setSettings(settingsMap);
        }
        rsnext = settingsResults.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // De-level all of the altars that need to be de-leveled
    for (TownAltarLink link : toDelevel) {
      link.levelDown();
    }
  }

  /**
   * Returns a list of AltarRank objects that indicate the current month's
   * altars' points
   * @return          A list of AltarRank objects
   */
  public static ArrayList<AltarRank> getLeaderboardPoints() {
    probeConnection();
    ArrayList<AltarRank> rankList = new ArrayList<>();
    try {
      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT town_uuid, points FROM AltarLeaderboardByMonth WHERE month=? ORDER BY points DESC;");
      isInTableStmt.setInt(1, month);
      ResultSet results = isInTableStmt.executeQuery();

      while (results.next()) {
        rankList.add(new AltarRank(UUID.fromString(results.getString("town_uuid")), results.getInt("points")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    Collections.sort(rankList);
    return rankList;
  }

  /**
   * Pushes data to the leaderboard, and updates
   * the score for the town
   * @param townUUID  The town whose data is being updated
   * @param points     The number of points to be added
   */
  public static void pushToLeaderboard(UUID townUUID, int points) {
    probeConnection();
    try {
      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT points FROM AltarLeaderboardByMonth WHERE town_uuid=? AND month=?");
      isInTableStmt.setString(1, townUUID.toString());
      isInTableStmt.setInt(2, month);
      ResultSet results = isInTableStmt.executeQuery();

      PreparedStatement currentTableStatement;
      PreparedStatement totalTableStatement;
      if (results.next()) {
        int currPoints = results.getInt("points");
        currentTableStatement = conn.prepareStatement("UPDATE AltarLeaderboardByMonth SET points=? WHERE town_uuid=? AND month=?;");
        currentTableStatement.setInt(1, currPoints + points);
        currentTableStatement.setString(2, townUUID.toString());
        currentTableStatement.setInt(3, month);

        PreparedStatement totalQuery = conn.prepareStatement("SELECT points FROM TotalAltarLeaderboard WHERE town_uuid=?");
        totalQuery.setString(1, townUUID.toString());
        ResultSet totalPointsRS = totalQuery.executeQuery();
        totalPointsRS.next();
        int currTotalPoints = totalPointsRS.getInt("points");
        totalTableStatement = conn.prepareStatement("UPDATE TotalAltarLeaderboard SET points=? WHERE town_uuid=?;");
        totalTableStatement.setInt(1, points + currTotalPoints);
        totalTableStatement.setString(2, townUUID.toString());
      } else {
        currentTableStatement = conn.prepareStatement("INSERT INTO AltarLeaderboardByMonth(month, town_uuid, points) VALUES (?, ?, ?);");
        currentTableStatement.setInt(1, month);
        currentTableStatement.setString(2, townUUID.toString());
        currentTableStatement.setInt(3, points);
        totalTableStatement = conn.prepareStatement("INSERT INTO TotalAltarLeaderboard(town_uuid, points) VALUES (?, ?);");
        totalTableStatement.setString(1, townUUID.toString());
        totalTableStatement.setInt(2, points);
      }

      currentTableStatement.executeUpdate();
      totalTableStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Pushes the data from a town-altar link
   * to the database
   * @param altarLink  The town-altar link whose data is to be stored/updated
   */
  public static void pushToDB(TownAltarLink altarLink) {
    probeConnection();
    int level = altarLink.getLevel();
    Town town = altarLink.getTown();
    if (town == null) {
      Bukkit.getLogger().warning("Couldn't push altar to DB because it's town no longer exists: " + altarLink.getUniqueId());
      return;
    }
    UUID townUUID = altarLink.getUniqueId();
    String boonOne = (altarLink.getBoon(0) != null) ? altarLink.getBoon(0).getType().toString() : "";
    String boonTwo = (altarLink.getBoon(1) != null) ? altarLink.getBoon(1).getType().toString() : "";
    String boonThree = (altarLink.getBoon(2) != null) ? altarLink.getBoon(2).getType().toString() : "";
    String boonFour = (altarLink.getBoon(3) != null) ? altarLink.getBoon(3).getType().toString() : "";
    int totalRecentSacrifices = altarLink.getTotalRecentSacrifices();
    int totalSacrificesMade = altarLink.getTotalSacrificesMade();
    long nextEvalTime = altarLink.getNextEvalTime();

    try {

      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT town_uuid FROM TownAltarTable WHERE town_uuid=?");
      isInTableStmt.setString(1, townUUID.toString());
      boolean isInTable = isInTableStmt.executeQuery().next();

      PreparedStatement stmt;
      if (isInTable) {
        stmt = conn.prepareStatement("UPDATE TownAltarTable SET town_uuid=?, level=?, boon_1=?, boon_2=?, boon_3=?, boon_4=?, total_recent_sacrifices=?, total_sacrifices_made=?, next_eval_time=? WHERE town_uuid=?;");
        stmt.setString(1, townUUID.toString());
        stmt.setInt(2, level);
        stmt.setString(3, boonOne);
        stmt.setString(4, boonTwo);
        stmt.setString(5, boonThree);
        stmt.setString(6, boonFour);
        stmt.setInt(7, totalRecentSacrifices);
        stmt.setInt(8, totalSacrificesMade);
        stmt.setLong(9, nextEvalTime);
        stmt.setString(10, townUUID.toString());
      } else {
        stmt = conn.prepareStatement("INSERT INTO TownAltarTable(town_uuid, level, boon_1, boon_2, boon_3, boon_4, total_recent_sacrifices, total_sacrifices_made, next_eval_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, townUUID.toString());
        stmt.setInt(2, level);
        stmt.setString(3, boonOne);
        stmt.setString(4, boonTwo);
        stmt.setString(5, boonThree);
        stmt.setString(6, boonFour);
        stmt.setInt(7, totalRecentSacrifices);
        stmt.setInt(8, totalSacrificesMade);
        stmt.setLong(9, nextEvalTime);
      }

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Pushes the data from an altar to the database
   * @param altar  The altar whose data is to be stored/updated
   */
  public static void pushToDB(AltarMeta altar) {
    probeConnection();
    UUID uuid = altar.getUniqueId();
    Location altarLoc = altar.getLocation();
    String worldStr = altarLoc.getWorld().getName();
    double x = altarLoc.getX();
    double y = altarLoc.getY();
    double z = altarLoc.getZ();
    String type = altar.getType().toString();
    Town town = altar.getTown();
    if (town == null) {
      Bukkit.getLogger().warning("Couldn't push altar to DB because it's town no longer exists: " + worldStr + ": " + x + ", " + y + ", " + z);
      return;
    }
    UUID townUUID = town.getUUID();
    byte[] sacrificeOne = (altar.getSacrifice(0) != null) ? altar.getSacrifice(0).serialize() : null;
    byte[] sacrificeTwo = (altar.getSacrifice(1) != null) ? altar.getSacrifice(1).serialize() : null;
    byte[] sacrificeThree = (altar.getSacrifice(2) != null) ? altar.getSacrifice(2).serialize() : null;
    byte[] sacrificeFour = (altar.getSacrifice(3) != null) ? altar.getSacrifice(3).serialize() : null;

    try {

      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT uuid FROM AltarTable WHERE uuid=?");
      isInTableStmt.setString(1, uuid.toString());
      boolean isInTable = isInTableStmt.executeQuery().next();

      PreparedStatement stmt;
      if (isInTable) {
        stmt = conn.prepareStatement("UPDATE AltarTable SET uuid=?, town_uuid=?, type=?, world=?, x=?, y=?, z=?, sacrifice_1=?, sacrifice_2=?, sacrifice_3=?, sacrifice_4=? WHERE uuid=?;");
        stmt.setString(1, uuid.toString());
        stmt.setString(2, townUUID.toString());
        stmt.setString(3, type);
        stmt.setString(4, worldStr);
        stmt.setInt(5, (int) x);
        stmt.setInt(6, (int) y);
        stmt.setInt(7, (int) z);
        stmt.setBytes(8, sacrificeOne);
        stmt.setBytes(9, sacrificeTwo);
        stmt.setBytes(10, sacrificeThree);
        stmt.setBytes(11, sacrificeFour);
        stmt.setString(12, uuid.toString());
      } else {
        stmt = conn.prepareStatement("INSERT INTO AltarTable(uuid, town_uuid, type, world, x, y, z, sacrifice_1, sacrifice_2, sacrifice_3, sacrifice_4) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, uuid.toString());
        stmt.setString(2, townUUID.toString());
        stmt.setString(3, type);
        stmt.setString(4, worldStr);
        stmt.setInt(5, (int) x);
        stmt.setInt(6, (int) y);
        stmt.setInt(7, (int) z);
        stmt.setBytes(8, sacrificeOne);
        stmt.setBytes(9, sacrificeTwo);
        stmt.setBytes(10, sacrificeThree);
        stmt.setBytes(11, sacrificeFour);
      }

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Pushes the altar settings for the specified town to the
   * database
   * @param altarLink    The town-altar-link whose settings are to be pushed
   * @param settingsMap  The settings map to push
   */
  public static void pushToDB(TownAltarLink altarLink, HashMap<SettingsType, AltarSettings> settingsMap) {
    probeConnection();
    UUID town_uuid = altarLink.getUniqueId();
    try {
      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT town_uuid FROM AltarSettingsTable WHERE town_uuid=?");
      isInTableStmt.setString(1, town_uuid.toString());
      boolean isInTable = isInTableStmt.executeQuery().next();

      PreparedStatement stmt;
      if (isInTable) {
        stmt = conn.prepareStatement("UPDATE AltarSettingsTable SET outsiders_sacrifice=? WHERE town_uuid=?;");
        stmt.setBoolean(1, (Boolean) settingsMap.get(SettingsType.OUTSIDER_SACRIFICES).getState());
        stmt.setString(2, town_uuid.toString());
      } else {
        stmt = conn.prepareStatement("INSERT INTO AltarSettingsTable(town_uuid, outsiders_sacrifice) VALUES (?, ?);");
        stmt.setString(1, town_uuid.toString());
        stmt.setBoolean(2, (Boolean) settingsMap.get(SettingsType.OUTSIDER_SACRIFICES).getState());
      }
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes all altars from the database associated
   * with the town's UUID. Called upon server load,
   * when it is discovered that a town no longer exists.
   * @param townUUID  The town UUID whose altars are to be removed
   */
  public static void removeAltarByTownUUID(UUID townUUID) {
    probeConnection();
    try {
      PreparedStatement stmt = conn.prepareStatement("DELETE FROM AltarTable WHERE town_uuid=?");
      PreparedStatement stmtTwo = conn.prepareStatement("DELETE FROM TownAltarTable WHERE town_uuid=?");
      stmt.setString(1, townUUID.toString());
      stmtTwo.setString(1, townUUID.toString());
      stmt.executeUpdate();
      stmtTwo.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the SQL connection and
   * pulls all data from the database to
   * reconstruct necessary objects.
   */
  public static void init() {
    initHost();
    pullFromDB();
  }
}
