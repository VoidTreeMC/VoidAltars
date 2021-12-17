package com.condor.voidaltars.altar.transaction;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

import com.condor.voidaltars.altar.TownAltarLink;

public class TransactionCache {
  private TownAltarLink link;
  // Maps transaction UUID to transaction
  private HashMap<UUID, AltarTransaction> uuidTransacMap = new HashMap<>();
  // Maps player UUID to transaction
  private HashMap<UUID, AltarTransaction> playerTransacMap = new HashMap<>();
  // Maps transaction type to transaction
  private HashMap<TransactionType, AltarTransaction> transacTypeMap = new HashMap<>();
  // Keeps list of transactions sorted by time
  private ArrayList<AltarTransaction> transacList = new ArrayList<>();
  private int numTransactions;

  public static final int PAGE_SIZE = 10;

  public TransactionCache(TownAltarLink link) {
    this.link = link;
    PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT COUNT(transaction_uuid) FROM AltarTransactionTable WHERE town_uuid=?;");
    stmt.setString(1, link.getUniqueId());
    ResultSet rs = stmt.executeQuery();
    rs.next();
    // TODO: Check if this works
    numTransactions = rs.getInt("COUNT(transaction_uuid)");
  }

  public void store(Transaction transac) {
    uuidTransacMap.put(transac.getUUID(), transac);
    playerTransacMap.put(transac.getPlayerUUID(), transac);
    transacTypeMap.put(transac.getType(), transac);
    transacList.add(transac);
    numTransactions++;
  }

  public ArrayList<Transaction> getPage(int pageNum) {

  }

  // TODO
  public ArrayList<Transaction> getPage(int pageNum, UUID playerUUID) {
    return null;
  }

  // TODO
  public ArrayList<Transaction> getPage(int pageNum, TransactionType type) {
    return null;
  }

  public int getNumTransactions() {
    return this.numTransactions;
  }
}
