package com.condor.voidaltars.altar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.World;
import org.bukkit.Sound;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.exception.NotInATownException;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.altars.FarmAltar;
import com.condor.voidaltars.altar.multiblock.structures.FarmAltarStructure;
import com.condor.voidaltars.runnable.PlaySparkleEffect;
import com.condor.voidaltars.runnable.LightCandles;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public abstract class AltarMeta {
  AltarType type;
  Town town;
  Location interfaceLoc;
  AltarStructure structure;
  ArrayList<Boon> boons = new ArrayList<>();
  ArrayList<Sacrifice> sacrifices = new ArrayList<>();
  // The ID of the altar
  UUID uuid;
  // The number of sacrifices made in this interval
  int totalRecentSacrifices;
  // The number of sacrifices wanted in this interval
  int sacrificesWanted;
  // The level of the altar
  int level;
  // The total number of sacrifices made to this altar, ever
  int totalSacrificesMade;
  TreeMap<Material, Double> weightMap;
  // Might end up being unused. To be used later, when statistics are
  // being implemented.
  // ArrayList<Sacrifice> sacrificeHistory = new ArrayList<>();

  private static Random rng = new Random();

  public AltarMeta(AltarType type, UUID uuid, Location interfaceLoc, AltarStructure structure, TreeMap<Material, Double> weightMap) throws NotInATownException {
    this.type = type;
    this.structure = structure;
    TownBlock tb = TownyAPI.getInstance().getTownBlock(interfaceLoc);
    if (tb != null) {
      try {
        this.town = tb.getTown();
      } catch (NotRegisteredException e) {
        e.printStackTrace();
      }
    } else {
      throw new NotInATownException(interfaceLoc);
    }
    this.uuid = uuid;
    this.totalRecentSacrifices = 0;
    this.level = 1;
    this.weightMap = weightMap;
    this.sacrificesWanted = AltarManager.getSacrificesNeededByLevel(this.level);
    this.interfaceLoc = interfaceLoc;

    for (int i = 0; i < this.getNumSacrificeSlots(); i++) {
      sacrifices.add(SacrificeManager.getNewSacrifice(this));
    }
  }

  public AltarMeta(UUID uuid, String type, UUID townUUID, String worldStr, int level, double x, double y, double z,
                   ArrayList<String> boonList, ArrayList<byte[]> sacrificeList, int totalRecentSacrifices, int totalSacrificesMade,
                   TreeMap<Material, Double> weightMap, AltarStructure structure) {
   this.uuid = uuid;
   this.type = AltarType.getTypeFromString(type);
   this.weightMap = weightMap;
   try {
     this.town = TownyAPI.getInstance().getDataSource().getTown(townUUID);
   } catch (NotRegisteredException e) {
     e.printStackTrace();
   }
   Location location = new Location(AltarMain.getPlugin().getServer().getWorld(worldStr), x, y, z);
   this.interfaceLoc = location;
   this.level = level;
   this.sacrificesWanted = AltarManager.getSacrificesNeededByLevel(this.level);
   for (int i = 0; i < boonList.size(); i++) {
     if (!boonList.get(i).isEmpty()) {
        setBoon(BoonManager.getBoonByType(BoonType.getTypeFromString(boonList.get(i))), i);
     }
   }
   for (int i = 0; i < sacrificeList.size(); i++) {
     if (sacrificeList.get(i) != null) {
       sacrifices.add(new Sacrifice(sacrificeList.get(i), this));
     }
   }
   this.totalRecentSacrifices = totalRecentSacrifices;
   this.totalSacrificesMade = totalSacrificesMade;
   this.structure = structure;
 }

 public static AltarMeta create(UUID uuid, String typeStr, UUID townUUID, String worldStr, int level, double x, double y, double z,
                  ArrayList<String> boonList, ArrayList<byte[]> sacrificeList, int totalRecentSacrifices, int totalSacrificesMade) {
   AltarType type = AltarType.getTypeFromString(typeStr);
   switch (type) {
     case FARM_ALTAR:
     default:
      return new FarmAltar(uuid, typeStr, townUUID, worldStr, level, x, y, z, boonList, sacrificeList,
                           totalRecentSacrifices, totalSacrificesMade);
   }
   // return null;
 }

  public Sacrifice finishSacrifice(Sacrifice finished) {
    sacrifices.remove(finished);
    totalRecentSacrifices++;
    totalSacrificesMade++;
    if (shouldLevelUp()) {
      levelUp();
    }
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this);
    sacrifices.add(newSacrifice);
    return newSacrifice;
  }

  public boolean isSatisfied() {
    return this.totalRecentSacrifices >= this.sacrificesWanted;
  }

  public boolean shouldLevelUp() {
    return this.totalRecentSacrifices >= (this.sacrificesWanted * 1.5);
  }

  // Process the level up event
  public void levelUp() {
    if (this.level >= 4) {
      return;
    }
    this.level++;
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this);
    sacrifices.add(newSacrifice);
    doEffect();
  }

  public void doEffect() {
    World world = interfaceLoc.getWorld();
    // The interval between lightning strikes. Measured in ticks.
    final int STRIKE_INTERVAL = 40;
    int timeOffset = STRIKE_INTERVAL;
    ArrayList<Block> candles = this.getCandles();
    for (Block block : candles) {
      (new LightCandles(block, this.level)).runTaskLater(AltarMain.getPlugin(), timeOffset);
      timeOffset += STRIKE_INTERVAL;
    }
    world.strikeLightningEffect(interfaceLoc);
    (new PlaySparkleEffect(interfaceLoc, 0)).runTask(AltarMain.getPlugin());
    world.playSound(interfaceLoc, Sound.ENTITY_ENDERMAN_DEATH, 1, 1);
    Bukkit.getScheduler().runTaskLater(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        world.playSound(interfaceLoc, Sound.ENTITY_WITCH_CELEBRATE, 1, 1);
      }
    }, 10);
  }

  private ArrayList<Block> getCandles() {
    int size = this.structure.getSize();
    ArrayList<Block> ret = new ArrayList<>();
    for (int i = -size; i < size * 2; i++) {
      for (int j = -size; j < size * 2; j++) {
        for (int k = -size; k < size * 2; k++) {
          Location currLoc = new Location(interfaceLoc.getWorld(), interfaceLoc.getX() + i, interfaceLoc.getY() + j, interfaceLoc.getZ() + k);
          Block block = currLoc.getBlock();
          if (block.getBlockData() instanceof Candle) {
            ret.add(block);
          }
        }
      }
    }
    return ret;
  }

  private void setCandles(int amt) {
    ArrayList<Block> candles = getCandles();
    for (Block block : candles) {
      Candle candle = (Candle) block.getBlockData();
      candle.setCandles(amt);
      block.setBlockData(candle);
    }
  }

  // TODO: Maybe move to a different file?
  public static void setCandleLit(Block block, boolean state) {
    Candle candle = (Candle) block.getBlockData();
    candle.setLit(state);
    block.setBlockData(candle);
  }

  public void setCandlesLit(boolean state) {
    ArrayList<Block> candles = getCandles();
    for (Block block : candles) {
      setCandleLit(block, state);
    }
  }

  public void setBoon(Boon boon, int index) {
    if (boons.size() > index) {
      boons.get(index).removeTown(this.town);
      boons.set(index, boon);
    } else {
      boons.add(boon);
    }
    boon.addTown(this.town);
  }

  public AltarType getType() {
    return this.type;
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public Material getSacrificeType() {
    List<Material> sacrificeTypes = this.getSacrificeTypes();
    return sacrificeTypes.get(rng.nextInt(sacrificeTypes.size()));
  }

  public Sacrifice getSacrifice(int index) {
    if (index < sacrifices.size()) {
      return sacrifices.get(index);
    } else {
      return null;
    }
  }

  public Boon getBoon(int index) {
    if (index < boons.size()) {
      return boons.get(index);
    } else {
      return null;
    }
  }

  public int getLevel() {
    return this.level;
  }

  public int getMaxLevel() {
    return 4;
  }

  public int getNumSacrificeSlots() {
    return this.getLevel();
  }

  public int getNumBoonSlots() {
    return this.getLevel();
  }

  public double getSacrificeWeight(Material type) {
    return weightMap.get(type);
  }

  public Location getLocation() {
    return this.interfaceLoc;
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

  public abstract List<Material> getSacrificeTypes();
}
