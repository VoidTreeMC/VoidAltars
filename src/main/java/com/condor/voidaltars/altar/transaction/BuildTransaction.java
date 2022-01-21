package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

public class BuildTransaction extends AltarTransaction {
  String worldName;
  int x;
  int y;
  int z;

  public BuildTransaction(UUID altarUUID, TownAltarLink link, UUID playerUUID, UUID transacUUID,
                          long transacTime, String worldName, int x, int y, int z) {
    super(TransactionType.BUILD, altarUUID, link, playerUUID, transacUUID, transacTime);
    this.worldName = worldName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public BuildTransaction(UUID transacUUID) {
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

  // TODO: Add text colors
  public String toString() {
    String ret = "";
    String playerName = Bukkit.getServer().getOfflinePlayer(playerUUID).getName();
    Date theTime = new Date(this.transacTime);
    String locString = "XYZ: " + x + ", " + y + ", " + z;
    String timestamp = new SimpleDateFormat("M/d H:m").format(theTime);
    ret += "BUILD: " + playerName + " " + timestamp + " " + locString;
    return ret;
  }

  public void pushToDB() {
    try {
      PreparedStatement toGenericTable = SQLLinker.getConn().prepareStatement("INSERT INTO AltarTransactionTable(altar_uuid, town_uuid, player_uuid, transaction_type, transaction_uuid, transaction_time) VALUES (?, ?, ?, 'BUILD', ?, ?);");
      toGenericTable.setString(1, this.altarUUID.toString());
      toGenericTable.setString(2, this.link.getUniqueId().toString());
      toGenericTable.setString(3, this.playerUUID.toString());
      toGenericTable.setString(4, this.transacUUID.toString());
      toGenericTable.setLong(5, this.transacTime);
      toGenericTable.executeUpdate();

      PreparedStatement toSpecificTable = SQLLinker.getConn().prepareStatement("INSERT INTO BuildDestroyTransactionTable(transaction_uuid, type, world, x, y, z) VALUES (?, 'BUILD', ?, ?, ?, ?);");
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
