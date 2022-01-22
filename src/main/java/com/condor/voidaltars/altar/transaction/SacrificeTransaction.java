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
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.sql.SQLLinker;

public class SacrificeTransaction extends AltarTransaction {
  int level;
  AltarType altarType;
  String itemType;
  int amt;
  int amtRemaining;

  public SacrificeTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, AltarType altarType, int level, String itemType, int amt, int amtRemaining) {
    super(TransactionType.SACRIFICE, altarUUID, link, playerUUID, transacUUID, transacTime);
    this.altarType = altarType;
    this.level = level;
    this.itemType = itemType;
    this.amt = amt;
    this.amtRemaining = amtRemaining;
  }

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

  // TODO: Add text colors
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
