package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.sql.PreparedStatement;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;

public abstract class LevelTransaction {
  int newLevel

  public LevelTransaction(UUID altarUUID, TownAltarLink link, UUID transacUUID, long transacTime, int newLevel) {
    super(TransactionType.LEVEL, altarUUID, link, playerUUID, transacUUID, transacTime)
    this.newLevel = newLevel;
  }

  // TODO: Add text colors
  public String toString() {
    String ret = "";
    Date theTime = new Date(this.transacTime);
    String timestamp = DateFormat.format(theTime);
    ret += "LEVEL: " + timestamp + " Level changed to " + newLevel;
    return ret;
  }

  public void pushToDB() {
    PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'LEVEL', ?, ?);");
    toGenericTable.setString(1, this.altarUUID);
    toGenericTable.setString(2, this.link.getUniqueId());
    toGenericTable.setString(3, "");
    toGenericTable.setString(4, this.transacUUID);
    toGenericTable.setLong(5, this.transacTime);
    toGenericTable.executeUpdate();

    PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO LevelTransactionTable(transaction_uuid, new_level) VALUES (?, ?);");
    toSpecificTable.setString(1, this.transacUUID);
    toSpecificTable.setInt(2, this.newLevel);
    toSpecificTable.executeUpdate();
  }
}
