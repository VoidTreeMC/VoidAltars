package com.condor.voidaltars.altar;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.exception.NotInATownException;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.altar.transaction.TransactionCache;
import com.condor.voidaltars.altar.transaction.LevelTransaction;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;

/**
 * Class that links a town to its altars, and provides
 * a variety of utility and administrative methods that affect
 * all altars in the town
 */
public class TownAltarLink {
  // The amount of milliseconds in a quota period
  // Currently, 1 week
  private final static long QUOTA_PERIOD = 1000 * 60 * 60 * 24 * 7;
  private HashMap<AltarType, AltarMeta> altarMap = new HashMap<>();
  // The number of sacrifices made in this interval
  int totalRecentSacrifices;
  // The number of sacrifices wanted in this interval
  int sacrificesWanted;
  // The level of the altar
  int level;
  // The total number of sacrifices made to this altar, ever
  int totalSacrificesMade;
  // A list of the town's active boons
  ArrayList<Boon> boons = new ArrayList<>();
  // The next time that the town's altars will be evaluated for de-ranking
  long nextEvalTime;
  // The town that is being linked to its altars
  Town town;
  // The cache that stores the town's altar transactions
  TransactionCache transacCache;

  /**
   * Constructor for a TownAltarLink.
   * Intended for creating a new TownAltarLink, rather than
   * reconstructing one from the database
   * @param  town                 The town to be linked
   */
  public TownAltarLink(Town town) {
    this.town = town;
    this.totalRecentSacrifices = 0;
    this.level = 0;
    this.sacrificesWanted = AltarManager.getSacrificesNeededByLevel(this.level);
    this.nextEvalTime = this.calcNextEvalTime();
    TownAltarLink temp = this;
    AltarManager.addAltarLink(this);
    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        Bukkit.getLogger().info("Pushing TownAltarLink.");
        SQLLinker.pushToDB(temp);
      }
    });
    this.transacCache = new TransactionCache(this);
  }

  /**
   * A constructor for a TownAltarLink object.
   * Intended for reconstructing a TownAltarLink
   * object from the database.
   * @param townUUID               The town's UUID
   * @param level                  The level of the altars
   * @param boonList               The list of boons active in the town
   * @param totalRecentSacrifices  The total number of recent sacrifices made by the town (in this quota period)
   * @param totalSacrificesMade    The total number of sacrifices made at this altar (over all time)
   * @param nextEvalTime           The time of the next evaluation period for the altar
   */
  public TownAltarLink(UUID townUUID, int level, ArrayList<String> boonList,
                   int totalRecentSacrifices, int totalSacrificesMade, long nextEvalTime) {
    this.nextEvalTime = nextEvalTime;
    try {
     this.town = TownyAPI.getInstance().getDataSource().getTown(townUUID);
    } catch (NotRegisteredException e) {
     Bukkit.getLogger().info("The town " + townUUID + " is no longer registered. Purging from database.");
     Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
       @Override
       public void run() {
         SQLLinker.removeAltarByTownUUID(townUUID);
       }
     });
    }
    this.level = level;
    this.sacrificesWanted = AltarManager.getSacrificesNeededByLevel(this.level);
    for (int i = 0; i < boonList.size(); i++) {
     if (!boonList.get(i).isEmpty()) {
        setBoon(BoonManager.getBoonByType(BoonType.getTypeFromString(boonList.get(i))), i);
     }
    }
    this.totalRecentSacrifices = totalRecentSacrifices;
    this.totalSacrificesMade = totalSacrificesMade;
    AltarManager.addAltarLink(this);
    this.transacCache = new TransactionCache(this);
  }

   // TODO: Make this throw an error if someone tries to add a duplicate altar
   /**
    * Registers a new altar with this town-altar link
    * @param type  The type of altar to be registered
    * @param meta  The meta object associated with the altar
    */
   public void addAltar(AltarType type, AltarMeta meta) {
     altarMap.put(type, meta);
   }

   /**
    * Gets the altar of the specified type belonging to this town
    * @param  type               The type of the altar
    * @return                    The altar if it exists, or null otherwise
    */
   public AltarMeta getAltar(AltarType type) {
     return altarMap.get(type);
   }

   /**
    * Gets a collection of all altars belonging to this town
    * @return A collection of all altars in this town
    */
   public Collection<AltarMeta> getAltars() {
     return altarMap.values();
   }

   /**
    * Handles the the unclaiming of a chunk by the town. If
    * an altar was in that chunk, it deactivates the altar and
    * informs all residents. If it was the town's only altar, it
    * deactivates all boons for the town.
    */
   public void handleChunkUnclaim() {
     int numUnclaimed = 0;
     for (AltarMeta altar : altarMap.values()) {
       Location altarLoc = altar.getLocation();
       TownBlock tb = TownyAPI.getInstance().getTownBlock(altarLoc);
       // If the altar's chunk is no longer a town block
       if (tb == null) {
         numUnclaimed++;
         // Send a message to all town members telling them their altar was just unclaimed
         for (Resident resident : town.getResidents()) {
           Player player = resident.getPlayer();
           if (player != null) {
             player.sendMessage(StringConstants.TOWN_HAS_UNCLAIMED_ALTAR.get());
           }
         }
       }
     }
     if (numUnclaimed >= altarMap.size()) {
       this.clearBoons();
     }
   }

   /**
    * Gets the town-altar link's UUID
    * @return The UUID belonging to the town-altar link
    */
   public UUID getUniqueId() {
     return this.town.getUUID();
   }

  /**
   * Increments the number of sacrifices performed by
   * the town, and levels the altars if necessary
   */
  public void incrementSacrifices() {
   totalRecentSacrifices++;
   totalSacrificesMade++;
   if (shouldLevelUp()) {
     levelUp();
   }
   TownAltarLink tempLink = this;
   Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
     @Override
     public void run() {
       SQLLinker.pushToDB(tempLink);
     }
   });
  }

  /**
   * Returns true if the gods are satisfied, false otherwise
   * @return True if the gods are satisfied, false otherwise
   */
  public boolean isSatisfied() {
   return this.totalRecentSacrifices >= this.sacrificesWanted;
  }

  /**
   * Returns true if the altars should level up, false otherwise
   * @return True if the altar should level up, false otherwise
   */
  public boolean shouldLevelUp() {
   return (this.totalRecentSacrifices >= (this.sacrificesWanted * 1.5)) && (this.getLevel() < this.getMaxLevel());
  }

  /**
   * Returns true if the town has met its sacrifice quota, false otherwise
   * @return True if the town has met its sacrifice quota, false otherwise
   */
  public boolean hasMetQuota() {
   if (this.getSacrificesRemaining() > 0) {
     long now = System.currentTimeMillis();
     if (now > this.getNextEvalTime()) {
       return false;
     }
   }
   return true;
  }

  /**
   * Processes the level-up event for a town's altars
   */
  public void levelUp() {
   if (this.level >= 4) {
     return;
   }
   this.level++;
   this.sacrificesWanted = AltarManager.getSacrificesNeededByLevel(this.level);
   this.totalRecentSacrifices = 0;
   this.nextEvalTime = this.calcNextEvalTime();
   for (AltarMeta meta : altarMap.values()) {
     meta.addNewSacrifice();
     Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
       @Override
       public void run() {
         SQLLinker.pushToDB(meta);
       }
     });
     meta.doEffect();
   }
   announceLevelUp();

   TownAltarLink tempLink = this;
   Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
     @Override
     public void run() {
       SQLLinker.pushToDB(tempLink);
     }
   });
   LevelTransaction transac = new LevelTransaction(this, UUID.randomUUID(), System.currentTimeMillis(), this.level);
   this.getTransacCache().store(transac);
  }

  /**
   * Processes the level-down event for a town's altars
   */
  public void levelDown() {
   if (this.level <= 0) {
     return;
   }
   this.level--;
   if (boons.size() > 0) {
     boons.get(boons.size() - 1).removeTown(this.getTown());
     boons.remove(boons.size() - 1);
   }
   this.totalRecentSacrifices = 0;
   this.nextEvalTime = this.calcNextEvalTime();

   for (AltarMeta meta : altarMap.values()) {
     meta.removeSacrifice();
     if (this.level == 0) {
       meta.setCandlesLit(false);
     } else {
       meta.setCandles(this.level);
     }
     Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
       @Override
       public void run() {
         SQLLinker.pushToDB(meta);
       }
     });
   }

   TownAltarLink tempLink = this;
   Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
     @Override
     public void run() {
       SQLLinker.pushToDB(tempLink);
     }
   });
  }

  /**
   * Announces to the entire server that this town
   * has levelled up its altars
   * TODO: Move this string to a string constant config
   */
  private void announceLevelUp() {
    String townName = this.town.getName();
    boolean pluralAltars = this.getAltars().size() > 1;
    String pluralAltarsS = pluralAltars ? "s" : "";
    String pluralBoonS = (this.getNumBoonSlots() > 1) ? "s" : "";
    String hasOrHave = pluralAltars ? "have" : "has";
    Bukkit.broadcastMessage(ChatColor.YELLOW + "The Gods of " + ChatColor.RED + "Void" + ChatColor.GRAY +
                            "Tree" + ChatColor.YELLOW + " smile upon " + ChatColor.GOLD + townName + "'s" +
                            ChatColor.YELLOW + " altar" + pluralAltarsS + ", which " + hasOrHave + " achieved level " +
                            ChatColor.GOLD + "" + this.level + ChatColor.YELLOW + "! " + ChatColor.GOLD +
                            townName + ChatColor.YELLOW + " has earned " + ChatColor.GOLD + "" + this.level +
                            ChatColor.YELLOW + " boon" + pluralBoonS + "!");
  }

  /**
   * Sets the boon at the specified index to the specified type
   * @param boon   The boon
   * @param index  The index (0-3)
   */
  public void setBoon(Boon boon, int index) {
   if (boons.size() > index) {
     if (boons.get(index) != null) {
       boons.get(index).removeTown(this.town);
     }
     boons.set(index, boon);
   } else {
     boons.add(boon);
   }
   boon.addTown(this.town);
  }

  /**
   * Clears and deactivates all of the town's boons
   */
  public void clearBoons() {
   for (int i = 0; i < boons.size(); i++) {
     if (boons.get(i) != null) {
       boons.get(i).removeTown(this.getTown());
       boons.set(i, null);
     }
   }
  }

  /**
   * Gets the boon at the specified index
   * @param  index               The index (0-3)
   * @return                     The boon at the index
   */
  public Boon getBoon(int index) {
   if (index < boons.size()) {
     return boons.get(index);
   } else {
     return null;
   }
  }

  /**
   * Gets the next time after which the altar will be evaluated
   * @return The next time after which the altar will be evaluated
   */
  public long getNextEvalTime() {
   return this.nextEvalTime;
  }

  /**
   * Calculates the next time after which the town
   * will be evaluated. Used when setting a new
   * eval time.
   * @return The next time after which the town should be evaluated
   */
  public long calcNextEvalTime() {
   long now = System.currentTimeMillis();
   return now + QUOTA_PERIOD;
  }

  /**
   * Gets the level of the town's altars
   * @return The level of the town's altars
   */
  public int getLevel() {
   return this.level;
  }

  /**
   * Gets the maximum level of the town's altars
   * @return The maximum level of the town's altars
   */
  public int getMaxLevel() {
   return 4;
  }

  /**
   * Gets the number of boon slots available
   * to the town
   * @return The number of boon slots
   */
  public int getNumBoonSlots() {
    return this.getLevel();
  }

  public Town getTown() {
    return this.town;
  }

  public int getSacrificesWanted() {
    return this.sacrificesWanted;
  }

  public int getSacrificesNeededForLevelUp() {
    return ((int) (this.sacrificesWanted * 1.5)) - this.totalRecentSacrifices;
  }

  public int getSacrificesRemaining() {
    return this.sacrificesWanted - this.totalRecentSacrifices;
  }

  public int getTotalRecentSacrifices() {
    return this.totalRecentSacrifices;
  }

  public int getTotalSacrificesMade() {
    return this.totalSacrificesMade;
  }

  public TransactionCache getTransacCache() {
    return this.transacCache;
  }
}
