package com.condor.voidaltars.altar;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.exception.NotInATownException;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.constants.StringConstants;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;

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
  ArrayList<Boon> boons = new ArrayList<>();
  long nextEvalTime;
  Town town;

  public TownAltarLink(Town town) throws NotInATownException {
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
  }

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
  }

   // TODO: Make this throw an error if someone tries to add a duplicate altar
   public void addAltar(AltarType type, AltarMeta meta) {
     altarMap.put(type, meta);
   }

   public AltarMeta getAltar(AltarType type) {
     return altarMap.get(type);
   }

   public Collection<AltarMeta> getAltars() {
     return altarMap.values();
   }

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

   public UUID getUniqueId() {
     return this.town.getUUID();
   }

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

  public boolean isSatisfied() {
   return this.totalRecentSacrifices >= this.sacrificesWanted;
  }

  public boolean shouldLevelUp() {
   return this.totalRecentSacrifices >= (this.sacrificesWanted * 1.5);
  }

  public boolean hasMetQuota() {
   if (this.getSacrificesRemaining() > 0) {
     long now = System.currentTimeMillis();
     if (now > this.getNextEvalTime()) {
       return false;
     }
   }
   return true;
  }

  // Process the level up event
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

   TownAltarLink tempLink = this;
   Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
     @Override
     public void run() {
       SQLLinker.pushToDB(tempLink);
     }
   });
  }

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

  public void clearBoons() {
   for (int i = 0; i < boons.size(); i++) {
     if (boons.get(i) != null) {
       boons.get(i).removeTown(this.getTown());
       boons.set(i, null);
     }
   }
  }

  public Boon getBoon(int index) {
   if (index < boons.size()) {
     return boons.get(index);
   } else {
     return null;
   }
  }

  public long getNextEvalTime() {
   return this.nextEvalTime;
  }

  public long calcNextEvalTime() {
   long now = System.currentTimeMillis();
   return now + QUOTA_PERIOD;
  }

  public int getLevel() {
   return this.level;
  }

  public int getMaxLevel() {
   return 4;
  }

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
}
