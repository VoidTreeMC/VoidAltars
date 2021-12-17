package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.sql.PreparedStatement;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.AltarType;

public abstract class SacrificeTransaction {
  int level;
  AltarType altarType;
  String itemType;
  int amt;
  int amtRemaining;

  public SacrificeTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, AltarType altarType, int level, String itemType, int amt, int amtRemaining) {
    super(TransactionType.SACRIFICE, altarUUID, link, playerUUID, transacUUID, transacTime)
    this.altarType = altarType;
    this.level = level;
    this.itemType = itemType;
    this.amt = amt;
    this.amtRemaining = amtRemaining;
  }

  // TODO: Add text colors
  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getDisplayName();
    Date theTime = new Date(this.transacTime);
    String timestamp = DateFormat.format(theTime);
    ret += "SACRIFICE: " + playerName + " " + timestamp + " " + amt + " " + itemType + ", " + amtRemaining + " remaining.";
    return ret;
  }

  public void pushToDB() {
    PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'Sacrifice', ?, ?);");
    toGenericTable.setString(1, this.altarUUID);
    toGenericTable.setString(2, this.link.getUniqueId());
    toGenericTable.setString(3, this.playerUUID);
    toGenericTable.setString(4, this.transacUUID);
    toGenericTable.setLong(5, this.transacTime);
    toGenericTable.executeUpdate();

    PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO SacrificeTransactionTable(transaction_uuid, altar_type, altar_level, item_type, amount, amount_remaining) VALUES (?, ?, ?, ?, ?, ?);");
    toSpecificTable.setString(1, this.transacUUID);
    toSpecificTable.setString(2, this.altarType.toString());
    toSpecificTable.setInt(3, this.level);
    toSpecificTable.setString(4, this.itemType);
    toSpecificTable.setInt(5, this.amt);
    toSpecificTable.setInt(6, this.amtRemaining);
    toSpecificTable.executeUpdate();
  }
}
