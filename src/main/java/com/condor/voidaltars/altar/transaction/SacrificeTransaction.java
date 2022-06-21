package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.sql.SQLLinker;

public class SacrificeTransaction extends AltarTransaction {
  int level;
  AltarType altarType;
  String itemType;
  int amt;
  int amtRemaining;

  /**
   * Constructor for a BuildTransaction.
   * Intended for creating a new transaction rather
   * than loading one from the database.
   * @param altarUUID     The UUID of the altar
   * @param link          The town-altar link
   * @param playerUUID    The UUID of the player
   * @param transacUUID   The transaction UUID
   * @param transacTime   The time at which the transaction took place
   * @param altarType     The type of altar
   * @param level         The current level of the altar
   * @param itemType      The type of item sacrificed
   * @param amt           The amount of the item sacrificed
   * @param amtRemaining  The amount of the item that is remaining
   */
  public SacrificeTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, AltarType altarType, int level, String itemType, int amt, int amtRemaining) {
    super(TransactionType.SACRIFICE, altarUUID, link, playerUUID, transacUUID, transacTime);
    this.altarType = altarType;
    this.level = level;
    this.itemType = itemType;
    this.amt = amt;
    this.amtRemaining = amtRemaining;
  }

  /**
   * Constructor for a SacrificeTransaction.
   * Intended for loading a boon from the
   * database and re-constructing it.
   * @param transacUUID  The transaction UUID, already in the database
   */
  public SacrificeTransaction(UUID transacUUID) {
    super(transacUUID);
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT altar_type, altar_level, item_type, amount, amount_remaining FROM SacrificeTransactionTable WHERE transaction_uuid=?");
      stmt.setString(1, this.transacUUID.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        this.altarType = AltarType.getTypeFromString(rs.getString("altar_type"));
        this.level = rs.getInt("altar_level");
        this.itemType = rs.getString("item_type");
        this.amt = rs.getInt("amount");
        this.amtRemaining = rs.getInt("amount_remaining");
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
    ret += ChatColor.AQUA + "SACRIFICE: " + ChatColor.GOLD + playerName + " " + ChatColor.YELLOW + timestamp + " " + ChatColor.GOLD + amt + " " + ChatColor.AQUA + itemType + ", " + ChatColor.GOLD + amtRemaining + ChatColor.AQUA + " remaining.";
    return ret;
  }

  public void pushToDB() {
    try {
      PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'SACRIFICE', ?, ?);");
      toGenericTable.setString(1, this.altarUUID.toString());
      toGenericTable.setString(2, this.link.getUniqueId().toString());
      toGenericTable.setString(3, this.playerUUID.toString());
      toGenericTable.setString(4, this.transacUUID.toString());
      toGenericTable.setLong(5, this.transacTime);
      toGenericTable.executeUpdate();

      PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO SacrificeTransactionTable(transaction_uuid, altar_type, altar_level, item_type, amount, amount_remaining) VALUES (?, ?, ?, ?, ?, ?);");
      toSpecificTable.setString(1, this.transacUUID.toString());
      toSpecificTable.setString(2, this.altarType.toString());
      toSpecificTable.setInt(3, this.level);
      toSpecificTable.setString(4, this.itemType);
      toSpecificTable.setInt(5, this.amt);
      toSpecificTable.setInt(6, this.amtRemaining);
      toSpecificTable.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
