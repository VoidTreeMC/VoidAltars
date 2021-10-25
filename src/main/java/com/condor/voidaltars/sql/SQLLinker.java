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

import com.mysql.cj.jdbc.MysqlDataSource;

import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.sql.SQLConfig;

public class SQLLinker {

  private static Connection conn;

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
        String boonOne = results.getString("boon_1");
        String boonTwo = results.getString("boon_2");
        String boonThree = results.getString("boon_3");
        String boonFour = results.getString("boon_4");
        byte[] sacrificeOne = results.getBytes("sacrifice_1");
        byte[] sacrificeTwo = results.getBytes("sacrifice_2");
        byte[] sacrificeThree = results.getBytes("sacrifice_3");
        byte[] sacrificeFour = results.getBytes("sacrifice_4");
        int totalRecentSacrifices = results.getInt("total_recent_sacrifices");
        int totalSacrificesMade = results.getInt("total_sacrifices_made");

        AltarMeta altar = AltarMeta.create(uuid, type, townUUID, worldStr, level, x, y, z, boonOne, boonTwo, boonThree, boonFour, sacrificeOne,
                                        sacrificeTwo, sacrificeThree, sacrificeFour, totalRecentSacrifices, totalSacrificesMade);
        AltarManager.addAltar(altar);
        rsnext = results.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void pushToDB(AltarMeta altar) {
    probeConnection();
    UUID uuid = altar.getUniqueId();
    String type = altar.getType().toString();
    UUID townUUID = altar.getTown().getUuid();
    Location altarLoc = altar.getLocation();
    String worldStr = altarLoc.getWorld().getName();
    int level = altar.getLevel();
    double x = altarLoc.getX();
    double y = altarLoc.getY();
    double z = altarLoc.getZ();
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

    try {

      PreparedStatement isInTableStmt = conn.prepareStatement("SELECT uuid FROM AltarTable WHERE uuid=?");
      isInTableStmt.setString(1, uuid.toString());
      boolean isInTable = isInTableStmt.executeQuery().next();

      PreparedStatement stmt;
      if (isInTable) {
        stmt = conn.prepareStatement("UPDATE AltarTable SET uuid=?, type=?, town_uuid=?, world=?, level=?, x=?, y=?, z=?, boon_1=?, boon_2=?, boon_3=?, boon_4=?, sacrifice_1=?, sacrifice_2=?, sacrifice_3=?, sacrifice_4=?, total_recent_sacrifices=?, total_sacrifices_made=? WHERE uuid=?;");
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
        stmt.setString(19, uuid.toString());
      } else {
        stmt = conn.prepareStatement("INSERT INTO AltarTable(uuid, type, town_uuid, world, level, x, y, z, boon_1, boon_2, boon_3, boon_4, sacrifice_1, sacrifice_2, sacrifice_3, sacrifice_4, total_recent_sacrifices, total_sacrifices_made) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
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
      }

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
