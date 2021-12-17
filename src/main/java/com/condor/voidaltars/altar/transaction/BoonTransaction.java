package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.sql.PreparedStatement;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;

public abstract class BoonTransaction {
  String oldBoon;
  String newBoon;

  public BoonTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, String oldBoon, String newBoon) {
    super(TransactionType.BOON, altarUUID, link, playerUUID, transacUUID, transacTime)
    this.oldBoon = oldBoon;
    this.newBoon = newBoon;
  }

  // TODO: Add text colors
  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getDisplayName();
    Date theTime = new Date(this.transacTime);
    String timestamp = DateFormat.format(theTime);
    ret += "BOON: " + playerName + " " + timestamp + " " + oldBoon + "->" + newBoon;
    return ret;
  }

  public void pushToDB() {
    PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'BOON', ?, ?);");
    toGenericTable.setString(1, this.altarUUID);
    toGenericTable.setString(2, this.link.getUniqueId());
    toGenericTable.setString(3, this.playerUUID);
    toGenericTable.setString(4, this.transacUUID);
    toGenericTable.setLong(5, this.transacTime);
    toGenericTable.executeUpdate();

    PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO BoonTransactionTable(transaction_uuid, old_boon, new_boon) VALUES (?, ?, ?);");
    toSpecificTable.setString(1, this.transacUUID);
    toSpecificTable.setString(2, this.oldBoon);
    toSpecificTable.setString(3, this.newBoon);
    toSpecificTable.executeUpdate();
  }
}
