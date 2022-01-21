package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

public abstract class AltarTransaction {
  protected TransactionType type;
  protected UUID altarUUID;
  protected TownAltarLink link;
  protected UUID playerUUID;
  protected UUID transacUUID;
  protected long transacTime;

  public AltarTransaction(TransactionType type, UUID altarUUID, TownAltarLink link,
                          UUID playerUUID, UUID transacUUID, long transacTime) {
    this.type = type;
    this.altarUUID = altarUUID;
    this.link = link;
    this.playerUUID = playerUUID;
    this.transacUUID = transacUUID;
    this.transacTime = transacTime;
  }

  public AltarTransaction(UUID transacUUID) {
    this.transacUUID = transacUUID;
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT * FROM AltarTransactionType WHERE transaction_uuid=?");
      stmt.setString(1, transacUUID.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String altar_uuid = rs.getString("altar_uuid");
        if (altar_uuid != null) {
          this.altarUUID = UUID.fromString(altar_uuid);
        }
        String player_uuid = rs.getString("player_uuid");
        if (player_uuid != null) {
          this.playerUUID = UUID.fromString(player_uuid);
        }
        this.link = AltarManager.getAltarLink(UUID.fromString(rs.getString("town_uuid")));
        this.type = TransactionType.getTypeFromString(rs.getString("transaction_type"));
        this.transacTime = rs.getLong("transaction_time");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public UUID getUUID() {
    return this.transacUUID;
  }

  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  public TransactionType getType() {
    return this.type;
  }

  public abstract String toString();

  public abstract void pushToDB();
}
