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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.mysql.cj.jdbc.MysqlDataSource;

import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.sql.SQLConfig;
import com.condor.voidaltars.altar.TownAltarLink;

import com.palmergames.bukkit.towny.object.Town;

public class SQLLinker {

  private static Connection conn;

  private static final int NUM_SACRIFICES_AND_BOONS = 4;

  public static void initHost() {
		try {
	    String url = SQLConfig.getVal("jdbc-url");
	    String username = SQLConfig.getVal("jdbc-user");
	    String password = SQLConfig.getVal("jdbc-password");

      conn = DriverManager.getConnection(url, username, password);
	    // conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  public static void probeConnection() {
    try {
      PreparedStatement stmt = conn.prepareStatement("Select * FROM AltarTable");
      stmt.executeQuery();
    } catch (Exception e) {
      Bukkit.getLogger().log(Level.INFO, "Connection probe failed. Re-establishing connection and re-probing.");
      // initHost();
      // probeConnection();
    }
  }

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
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // De-level all of the altars that need to be de-leveled
    for (TownAltarLink link : toDelevel) {
      link.levelDown();
    }
  }

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
    UUID townUUID = town.getUuid();
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

  public static void pushSacrificeToDB(long time, UUID playerUUID, AltarMeta altar, Material matType, int amt, int amount_remaining) {
    probeConnection();
    UUID uuid = altar.getUniqueId();
    UUID townUUID = altar.getTown().getUuid();
    String type = altar.getType().toString();
    int level = altar.getLevel();
    try {

      PreparedStatement stmt = conn.prepareStatement("INSERT INTO SacrificeTable(time_sacrificed, town_uuid, player_uuid, altar_id, altar_type, altar_level, item_type, amount, amount_remaining) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
      stmt.setLong(1, time);
      stmt.setString(2, townUUID.toString());
      stmt.setString(3, playerUUID.toString());
      stmt.setString(4, uuid.toString());
      stmt.setString(5, type);
      stmt.setInt(6, level);
      stmt.setString(7, matType.toString());
      stmt.setInt(8, amt);
      stmt.setInt(9, amount_remaining);

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void removeAltarByTownUUID(UUID townUUID) {
    probeConnection();
    try {

      PreparedStatement stmt = conn.prepareStatement("DELETE FROM AltarTable WHERE uuid=?");
      stmt.setString(1, townUUID.toString());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void init() {
    initHost();
    pullFromDB();
  }
}
