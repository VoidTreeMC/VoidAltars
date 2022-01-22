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
 * Represents an altar transaction in which the altar was
 * levelled up
 */
public class LevelTransaction extends AltarTransaction {
  int newLevel;

  /**
   * Constructor for a LevelTransaction.
   * Intended for creating a new transaction rather
   * than loading one from the database.
   * @param link         The town-altar link
   * @param transacUUID  The transaction UUID
   * @param transacTime  The time at which the transaction took place
   * @param newLevel     The new level, to which the altar has levelled up
   */
  public LevelTransaction(TownAltarLink link, UUID transacUUID, long transacTime, int newLevel) {
    super(TransactionType.LEVEL, null, link, null, transacUUID, transacTime);
    this.newLevel = newLevel;
  }

  /**
   * Constructor for a LevelTransaction.
   * Intended for loading a boon from the
   * database and re-constructing it.
   * @param transacUUID  The transaction UUID, already in the database
   */
  public LevelTransaction(UUID transacUUID) {
    super(transacUUID);
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT new_level FROM LevelTransactionTable WHERE transaction_uuid=?");
      stmt.setString(1, this.transacUUID.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        this.newLevel = rs.getInt("new_level");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String toString() {
    String ret = "";
    Date theTime = new Date(this.transacTime);
    String timestamp = new SimpleDateFormat("M/d H:").format(theTime);
    String minute = new SimpleDateFormat("m").format(theTime);
    if (minute.length() == 1) {
      minute = "0" + minute;
    }
    timestamp += minute;
    ret += ChatColor.AQUA + "LEVEL: " + ChatColor.YELLOW + timestamp + ChatColor.AQUA + " Level changed to " + ChatColor.GOLD + newLevel;
    return ret;
  }

  public void pushToDB() {
    try {
      PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'LEVEL', ?, ?);");
      toGenericTable.setString(1, "");
      toGenericTable.setString(2, this.link.getUniqueId().toString());
      toGenericTable.setString(3, "");
      toGenericTable.setString(4, this.transacUUID.toString());
      toGenericTable.setLong(5, this.transacTime);
      toGenericTable.executeUpdate();

      PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO LevelTransactionTable(transaction_uuid, new_level) VALUES (?, ?);");
      toSpecificTable.setString(1, this.transacUUID.toString());
      toSpecificTable.setInt(2, this.newLevel);
      toSpecificTable.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
