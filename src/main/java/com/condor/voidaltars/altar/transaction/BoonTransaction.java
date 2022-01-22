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
import com.condor.voidaltars.altar.BoonManager;
import com.condor.voidaltars.altar.BoonType;
import com.condor.voidaltars.sql.SQLLinker;

/**
 * Represents an altar transaction in which the altar's boon was changed
 */
public class BoonTransaction extends AltarTransaction {
  String oldBoon;
  String newBoon;

  /**
   * Constructor for a BoonTransaction.
   * Intended for creating a new transaction rather
   * than loading one from the database.
   * @param altarUUID    The UUID of the altar
   * @param link         The town-altar link
   * @param playerUUID   The UUID of the player
   * @param transacUUID  The transaction UUID
   * @param transacTime  The time at which the transaction took place
   * @param oldBoon      The old boon (source boon)
   * @param newBoon      The new boon (destination boon)
   */
  public BoonTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, String oldBoon, String newBoon) {
    super(TransactionType.BOON, altarUUID, link, playerUUID, transacUUID, transacTime);
    this.oldBoon = oldBoon;
    this.newBoon = newBoon;
  }

  /**
   * Constructor for a BoonTransaction.
   * Intended for loading a boon from the
   * database and re-constructing it.
   * @param transacUUID  The transaction UUID, already in the database
   */
  public BoonTransaction(UUID transacUUID) {
    super(transacUUID);
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT old_boon, new_boon FROM BoonTransactionTable WHERE transaction_uuid=?");
      stmt.setString(1, this.transacUUID.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        this.oldBoon = rs.getString("old_boon");
        this.newBoon = rs.getString("new_boon");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getName();
    Date theTime = new Date(this.transacTime);
    String timestamp = new SimpleDateFormat("M/d H:").format(theTime);
    String minute = new SimpleDateFormat("m").format(theTime);
    if (minute.length() == 1) {
      minute = "0" + minute;
    }
    timestamp += minute;
    String oldBoonStr = "No boon";
    if (!oldBoon.equals("")) {
      oldBoonStr = BoonManager.getBoonByType(BoonType.getTypeFromString(oldBoon)).getName();
    }
    String newBoonStr = BoonManager.getBoonByType(BoonType.getTypeFromString(newBoon)).getName();
    ret += ChatColor.AQUA + "BOON: " + ChatColor.GOLD + playerName + " " + ChatColor.YELLOW + timestamp + " " + ChatColor.GOLD + oldBoonStr + ChatColor.YELLOW + "->" + ChatColor.GOLD + newBoonStr;
    return ret;
  }

  public void pushToDB() {
    try {
      PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'BOON', ?, ?);");
      toGenericTable.setString(1, this.altarUUID.toString());
      toGenericTable.setString(2, this.link.getUniqueId().toString());
      toGenericTable.setString(3, this.playerUUID.toString());
      toGenericTable.setString(4, this.transacUUID.toString());
      toGenericTable.setLong(5, this.transacTime);
      toGenericTable.executeUpdate();

      PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO BoonTransactionTable(transaction_uuid, old_boon, new_boon) VALUES (?, ?, ?);");
      toSpecificTable.setString(1, this.transacUUID.toString());
      toSpecificTable.setString(2, this.oldBoon);
      toSpecificTable.setString(3, this.newBoon);
      toSpecificTable.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
