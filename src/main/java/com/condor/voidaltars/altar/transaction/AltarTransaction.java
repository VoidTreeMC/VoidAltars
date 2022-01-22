package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

/**
 * Represents a transaction with an altar
 */
public abstract class AltarTransaction {
  protected TransactionType type;
  protected UUID altarUUID;
  protected TownAltarLink link;
  protected UUID playerUUID;
  protected UUID transacUUID;
  protected long transacTime;

  /**
   * Constructor for an AltarTransaction.
   * Intended for creating new transactions rather than
   * loading one from the database
   * @param type         The type of transaction
   * @param altarUUID    The altar's UUID
   * @param link         The town-altar link
   * @param playerUUID   The player's UUID who completed the transaction
   * @param transacUUID  The UUID of the transaction
   * @param transacTime  The time at which the transaction took place
   */
  public AltarTransaction(TransactionType type, UUID altarUUID, TownAltarLink link,
                          UUID playerUUID, UUID transacUUID, long transacTime) {
    this.type = type;
    this.altarUUID = altarUUID;
    this.link = link;
    this.playerUUID = playerUUID;
    this.transacUUID = transacUUID;
    this.transacTime = transacTime;
  }

  /**
   * Constructor for an AltarTransaction
   * Intended for pulling information from the database
   * and using it to re-construct a transaction
   * @param transacUUID  The UUID of the transaction, already stored in the database
   */
  public AltarTransaction(UUID transacUUID) {
    this.transacUUID = transacUUID;
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT * FROM AltarTransactionTable WHERE transaction_uuid=?");
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

  /**
   * Converts the transaction to a chat-readable string,
   * intended for usage with /altar log
   * @return A string representing the transaction
   */
  public abstract String toString();

  /**
   * Pushes the transaction to the database
   */
  public abstract void pushToDB();
}
