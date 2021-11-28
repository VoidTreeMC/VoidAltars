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
    Bukkit.getLogger().log(Level.INFO, "Fetching altars from DB...");
    try {
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AltarTable;");
      ResultSet results = stmt.executeQuery();
      boolean rsnext = results.next();
      AltarManager.clearAltars();
      while (rsnext) {
        UUID uuid = UUID.fromString(results.getString("uuid"));
        String type = results.getString("type");
        UUID townUUID = UUID.fromString(results.getString("town_uuid"));
        String worldStr = results.getString("world");
        int level = results.getInt("level");
        double x = (double) results.getInt("x");
        double y = (double) results.getInt("y");
        double z = (double) results.getInt("z");
        ArrayList<String> boonList = new ArrayList<>();
        ArrayList<byte[]> sacrificeList = new ArrayList<>();
        for (int i = 1; i <= NUM_SACRIFICES_AND_BOONS; i++) {
          boonList.add(results.getString("boon_" + i));
          sacrificeList.add(results.getBytes("sacrifice_" + i));
        }
        int totalRecentSacrifices = results.getInt("total_recent_sacrifices");
        int totalSacrificesMade = results.getInt("total_sacrifices_made");
        long nextEvalTime = results.getLong("next_eval_time");


        AltarMeta altar = AltarMeta.create(uuid, type, townUUID, worldStr, level, x, y, z, boonList, sacrificeList,
                                           totalRecentSacrifices, totalSacrificesMade, nextEvalTime);
        if (!altar.hasMetQuota()) {
          altar.levelDown();
        }
        rsnext = results.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void pushToDB(AltarMeta altar) {
    probeConnection();
    UUID uuid = altar.getUniqueId();
    Location altarLoc = altar.getLocation();
    String worldStr = altarLoc.getWorld().getName();
    int level = altar.getLevel();
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
    String boonOne = (altar.getBoon(0) != null) ? altar.getBoon(0).getType().toString() : "";
    String boonTwo = (altar.getBoon(1) != null) ? altar.getBoon(1).getType().toString() : "";
    String boonThree = (altar.getBoon(2) != null) ? altar.getBoon(2).getType().toString() : "";
    String boonFour = (altar.getBoon(3) != null) ? altar.getBoon(3).getType().toString() : "";
    byte[] sacrificeOne = (altar.getSacrifice(0) != null) ? altar.getSacrifice(0).serialize() : null;
    byte[] sacrificeTwo = (altar.getSacrifice(1) != null) ? altar.getSacrifice(1).serialize() : null;
    byte[] sacrificeThree = (altar.getSacrifice(2) != null) ? altar.getSacrifice(2).serialize() : null;
    byte[] sacrificeFour = (altar.getSacrifice(3) != null) ? altar.getSacrifice(3).serialize() : null;
    int totalRecentSacrifices = altar.getTotalRecentSacrifices();
    int totalSacrificesMade = altar.getTotalSacrificesMade();
    long nextEvalTime = altar.getNextEvalTime();

    try {

      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT uuid FROM AltarTable WHERE uuid=?");
      isInTableStmt.setString(1, uuid.toString());
      boolean isInTable = isInTableStmt.executeQuery().next();

      PreparedStatement stmt;
      if (isInTable) {
        stmt = conn.prepareStatement("UPDATE AltarTable SET uuid=?, type=?, town_uuid=?, world=?, level=?, x=?, y=?, z=?, boon_1=?, boon_2=?, boon_3=?, boon_4=?, sacrifice_1=?, sacrifice_2=?, sacrifice_3=?, sacrifice_4=?, total_recent_sacrifices=?, total_sacrifices_made=?, next_eval_time=? WHERE uuid=?;");
        stmt.setString(1, uuid.toString());
        stmt.setString(2, type);
        stmt.setString(3, townUUID.toString());
        stmt.setString(4, worldStr);
        stmt.setInt(5, level);
        stmt.setInt(6, (int) x);
        stmt.setInt(7, (int) y);
        stmt.setInt(8, (int) z);
        stmt.setString(9, boonOne);
        stmt.setString(10, boonTwo);
        stmt.setString(11, boonThree);
        stmt.setString(12, boonFour);
        stmt.setBytes(13, sacrificeOne);
        stmt.setBytes(14, sacrificeTwo);
        stmt.setBytes(15, sacrificeThree);
        stmt.setBytes(16, sacrificeFour);
        stmt.setInt(17, totalRecentSacrifices);
        stmt.setInt(18, totalSacrificesMade);
        stmt.setLong(19, nextEvalTime);
        stmt.setString(20, uuid.toString());
      } else {
        stmt = conn.prepareStatement("INSERT INTO AltarTable(uuid, type, town_uuid, world, level, x, y, z, boon_1, boon_2, boon_3, boon_4, sacrifice_1, sacrifice_2, sacrifice_3, sacrifice_4, total_recent_sacrifices, total_sacrifices_made, next_eval_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, uuid.toString());
        stmt.setString(2, type);
        stmt.setString(3, townUUID.toString());
        stmt.setString(4, worldStr);
        stmt.setInt(5, level);
        stmt.setInt(6, (int) x);
        stmt.setInt(7, (int) y);
        stmt.setInt(8, (int) z);
        stmt.setString(9, boonOne);
        stmt.setString(10, boonTwo);
        stmt.setString(11, boonThree);
        stmt.setString(12, boonFour);
        stmt.setBytes(13, sacrificeOne);
        stmt.setBytes(14, sacrificeTwo);
        stmt.setBytes(15, sacrificeThree);
        stmt.setBytes(16, sacrificeFour);
        stmt.setInt(17, totalRecentSacrifices);
        stmt.setInt(18, totalSacrificesMade);
        stmt.setLong(19, nextEvalTime);
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
