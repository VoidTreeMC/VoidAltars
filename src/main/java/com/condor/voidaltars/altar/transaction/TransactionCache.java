package com.condor.voidaltars.altar.transaction;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.bukkit.Bukkit;

import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

/**
 * Used to access a town's altar transactions.
 * Does not currently support caching, but will
 * eventually.
 * TODO: Implement actual caching for this
 */
public class TransactionCache {
  private TownAltarLink link;
  // Maps transaction UUID to transaction
  // private HashMap<UUID, AltarTransaction> uuidTransacMap = new HashMap<>();
  // Maps player UUID to transaction
  // private HashMap<UUID, AltarTransaction> playerTransacMap = new HashMap<>();
  // Maps transaction type to transaction
  // private HashMap<TransactionType, AltarTransaction> transacTypeMap = new HashMap<>();
  // Keeps list of transactions sorted by time
  // private ArrayList<AltarTransaction> transacList = new ArrayList<>();
  private int numTransactions;

  // The maximum number of entries in a page
  public static final int PAGE_SIZE = 10;

  /**
   * Constructor for a TransactionCache
   * @param link  The town-altar link whose transactions are being cached
   */
  public TransactionCache(TownAltarLink link) {
    this.link = link;
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT COUNT(transaction_uuid) FROM AltarTransactionTable WHERE town_uuid=?;");
      stmt.setString(1, link.getUniqueId().toString());
      ResultSet rs = stmt.executeQuery();
      rs.next();
      // TODO: Check if this works
      this.numTransactions = rs.getInt("COUNT(transaction_uuid)");
    } catch (SQLException e) {
      e.printStackTrace();
      this.numTransactions = 0;
    }
  }

  /**
   * Stores a new transaction in the cache and the database
   * @param transac  The transaction to be stored
   */
  public void store(AltarTransaction transac) {
    // uuidTransacMap.put(transac.getUUID(), transac);
    // playerTransacMap.put(transac.getPlayerUUID(), transac);
    // transacTypeMap.put(transac.getType(), transac);
    // transacList.add(transac);
    transac.pushToDB();
    numTransactions++;
  }

  /**
   * Gets the transactions at a specific page number
   * @param  pageNum               The page number whose transactions are to be accessed
   * @return                       The transactions stored in this page
   */
  public ArrayList<AltarTransaction> getPage(int pageNum) {
    ArrayList<AltarTransaction> ret = new ArrayList<>();
    try {
      PreparedStatement stmt = SQLLinker.getConn().prepareStatement("SELECT transaction_uuid, transaction_type FROM AltarTransactionTable WHERE town_uuid=? ORDER BY transaction_time DESC LIMIT " + PAGE_SIZE + " OFFSET " + ((pageNum - 1) * PAGE_SIZE));
      stmt.setString(1, this.link.getUniqueId().toString());
      ResultSet rs = stmt.executeQuery();
      // For every row we just fetched from the table
      while (rs.next()) {
        TransactionType transacType = TransactionType.getTypeFromString(rs.getString("transaction_type"));
        UUID transacUUID = UUID.fromString(rs.getString("transaction_uuid"));
        AltarTransaction transac = null;
        switch (transacType) {
          case BUILD:
            transac = new BuildTransaction(transacUUID);
            break;
          case DESTROY:
            transac = new DestroyTransaction(transacUUID);
            break;
          case SACRIFICE:
            transac = new SacrificeTransaction(transacUUID);
            break;
          case BOON:
            transac = new BoonTransaction(transacUUID);
            break;
          case LEVEL:
            transac = new LevelTransaction(transacUUID);
            break;
          default:
            Bukkit.getLogger().info("Encountered error when processing transaction from database.");
        }

        if (transac != null) {
          ret.add(transac);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ret;
  }

  // TODO
  public ArrayList<AltarTransaction> getPage(int pageNum, UUID playerUUID) {
    return null;
  }

  // TODO
  public ArrayList<AltarTransaction> getPage(int pageNum, TransactionType type) {
    return null;
  }

  public int getNumTransactions() {
    return this.numTransactions;
  }
}
