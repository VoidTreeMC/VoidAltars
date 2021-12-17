package com.condor.voidaltars.altar.transaction;

import java.util.UUID;
import java.sql.PreparedStatement;

import com.condor.voidaltars.altar.TownAltarLink;

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
