package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

/**
 * Represents an altar transaction in which an altar's interface
 * block was destroyed
 */
public class DestroyTransaction extends AltarTransaction {
  String worldName;
  int x;
  int y;
  int z;

  /**
   * Constructor for a DestroyTransaction.
   * Intended for creating a new transaction rather
   * than loading one from the database.
   * @param altarUUID    The UUID of the altar
   * @param link         The town-altar link
   * @param playerUUID   The UUID of the player
   * @param transacUUID  The transaction UUID
   * @param transacTime  The time at which the transaction took place
   * @param worldName    The name of the world in which the altar was created
   * @param x            The X coordinate at which the altar was created
   * @param y            The Y coordinate at which the altar was created
   * @param z            The Z coordinate at which the altar was created
   */
  public DestroyTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, String worldName, int x, int y, int z) {
    super(TransactionType.DESTROY, altarUUID, link, playerUUID, transacUUID, transacTime);
    this.worldName = worldName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Constructor for a DestroyTransaction.
   * Intended for loading a boon from the
   * database and re-constructing it.
   * @param transacUUID  The transaction UUID, already in the database
   */
  public DestroyTransaction(UUID transacUUID) {
    super(transacUUID);
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT world, x, y, z FROM BuildDestroyTransactionTable WHERE transaction_uuid=?");
      stmt.setString(1, this.transacUUID.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        this.worldName = rs.getString("world");
        this.x = rs.getInt("x");
        this.y = rs.getInt("y");
        this.z = rs.getInt("z");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getName();
    Date theTime = new Date(this.transacTime);
    String locString = "XYZ: " + ChatColor.GOLD + x + ", " + y + ", " + z;
    String timestamp = new SimpleDateFormat("M/d H:").format(theTime);
    String minute = new SimpleDateFormat("m").format(theTime);
    if (minute.length() == 1) {
      minute = "0" + minute;
    }
    timestamp += minute;
    ret += ChatColor.AQUA + "DESTROY: " + ChatColor.GOLD + playerName + " " + ChatColor.YELLOW + timestamp + " " + ChatColor.AQUA + locString;
    return ret;
  }

  public void pushToDB() {
    try {
      PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'DESTROY', ?, ?);");
      toGenericTable.setString(1, this.altarUUID.toString());
      toGenericTable.setString(2, this.link.getUniqueId().toString());
      toGenericTable.setString(3, this.playerUUID.toString());
      toGenericTable.setString(4, this.transacUUID.toString());
      toGenericTable.setLong(5, this.transacTime);
      toGenericTable.executeUpdate();

      PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO BuildDestroyTransactionTable(transaction_uuid, type, world, x, y, z) VALUES (?, 'DESTROY', ?, ?, ?, ?);");
      toSpecificTable.setString(1, this.transacUUID.toString());
      toSpecificTable.setString(2, this.worldName);
      toSpecificTable.setInt(3, x);
      toSpecificTable.setInt(4, y);
      toSpecificTable.setInt(5, z);
      toSpecificTable.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
