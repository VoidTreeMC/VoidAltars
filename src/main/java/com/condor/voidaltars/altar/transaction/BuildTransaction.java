package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.sql.PreparedStatement;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;

public abstract class BuildTransaction {
  String worldName;
  int x;
  int y;
  int z;

  public BuildTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, String worldName, int x, int y, int z) {
    super(TransactionType.BUILD, altarUUID, link, playerUUID, transacUUID, transacTime)
    this.worldName = worldName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  // TODO: Add text colors
  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getDisplayName();
    Date theTime = new Date(this.transacTime);
    String locString = "XYZ: " + x = ", " + y + ", " + z;
    String timestamp = DateFormat.format(theTime);
    ret += "BUILD: " + playerName + " " + timestamp + " " + locString;
    return ret;
  }

  public void pushToDB() {
    PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'BUILD', ?, ?);");
    toGenericTable.setString(1, this.altarUUID);
    toGenericTable.setString(2, this.link.getUniqueId());
    toGenericTable.setString(3, this.playerUUID);
    toGenericTable.setString(4, this.transacUUID);
    toGenericTable.setLong(5, this.transacTime);
    toGenericTable.executeUpdate();

    PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO BuildDestroyTransactionTable(transaction_uuid, type, world, x, y, z) VALUES (?, 'BUILD', ?, ?, ?, ?);");
    toSpecificTable.setString(1, this.transacUUID);
    toSpecificTable.setString(2, this.worldName);
    toSpecificTable.setInt(3, x);
    toSpecificTable.setInt(4, y);
    toSpecificTable.setInt(5, z);
    toSpecificTable.executeUpdate();
  }
}
