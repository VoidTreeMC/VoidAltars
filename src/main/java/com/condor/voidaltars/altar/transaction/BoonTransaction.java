package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

public class BoonTransaction extends AltarTransaction {
  String oldBoon;
  String newBoon;

  public BoonTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, String oldBoon, String newBoon) {
    super(TransactionType.BOON, altarUUID, link, playerUUID, transacUUID, transacTime);
    this.oldBoon = oldBoon;
    this.newBoon = newBoon;
  }

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

  // TODO: Add text colors
  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getName();
    Date theTime = new Date(this.transacTime);
    String timestamp = new SimpleDateFormat("M/d H:m").format(theTime);
    ret += "BOON: " + playerName + " " + timestamp + " " + oldBoon + "->" + newBoon;
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
