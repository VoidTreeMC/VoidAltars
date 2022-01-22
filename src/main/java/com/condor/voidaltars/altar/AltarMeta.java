package com.condor.voidaltars.altar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
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
import com.condor.voidaltars.altar.exception.WrongTownException;
import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.altars.FarmAltar;
import com.condor.voidaltars.altar.altars.MiningAltar;
import com.condor.voidaltars.altar.altars.OceanAltar;
import com.condor.voidaltars.altar.altars.NetherAltar;
import com.condor.voidaltars.altar.multiblock.structures.FarmAltarStructure;
import com.condor.voidaltars.altar.multiblock.structures.MiningAltarStructure;
import com.condor.voidaltars.altar.multiblock.structures.OceanAltarStructure;
import com.condor.voidaltars.altar.multiblock.structures.NetherAltarStructure;
import com.condor.voidaltars.runnable.PlaySparkleEffect;
import com.condor.voidaltars.runnable.LightCandles;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.altar.TownAltarLink;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public abstract class AltarMeta {
  AltarType type;
  TownAltarLink link;
  Location interfaceLoc;
  AltarStructure structure;
  ArrayList<Sacrifice> sacrifices = new ArrayList<>();
  // The ID of the altar
  UUID uuid;
  HashMap<Material, Double> weightMap;

  private static Random rng = new Random();

  public AltarMeta(TownAltarLink link, AltarType type, UUID uuid, Location interfaceLoc, AltarStructure structure, HashMap<Material, Double> weightMap) throws NotInATownException, WrongTownException {
    this.link = link;
    this.type = type;
    this.structure = structure;
    TownBlock tb = TownyAPI.getInstance().getTownBlock(interfaceLoc);
    Town town = null;
    if (tb != null) {
      try {
        town = tb.getTown();
      } catch (NotRegisteredException e) {
        e.printStackTrace();
      }
    } else {
      throw new NotInATownException(interfaceLoc);
    }
    if (!town.getUUID().equals(link.getUniqueId())) {
      throw new WrongTownException(town.getUUID(), link.getUniqueId());
    }
    this.uuid = uuid;
    this.weightMap = weightMap;
    this.interfaceLoc = interfaceLoc;

    for (int i = 0; i < this.getNumSacrificeSlots(); i++) {
      sacrifices.add(SacrificeManager.getNewSacrifice(this));
    }
    AltarMeta tempMeta = this;
    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        SQLLinker.pushToDB(tempMeta);
      }
    });
  }

  public AltarMeta(UUID uuid, String type, UUID townUUID, String worldStr, double x, double y, double z,
                   ArrayList<byte[]> sacrificeList, HashMap<Material, Double> weightMap, AltarStructure structure) {
   this.uuid = uuid;
   this.type = AltarType.getTypeFromString(type);
   this.weightMap = weightMap;
   this.link = AltarManager.getAltarLink(townUUID);
   Bukkit.getLogger().info("Town UUID: " + townUUID);
   Bukkit.getLogger().info("Link: " + this.link);
   Location location = new Location(AltarMain.getPlugin().getServer().getWorld(worldStr), x, y, z);
   this.interfaceLoc = location;
   for (int i = 0; i < sacrificeList.size(); i++) {
     if (sacrificeList.get(i) != null) {
       sacrifices.add(new Sacrifice(sacrificeList.get(i), this));
     }
   }
   this.structure = structure;
   this.link.addAltar(this.type, this);
 }

 public static AltarMeta create(UUID uuid, String typeStr, UUID townUUID, String worldStr, double x, double y, double z, ArrayList<byte[]> sacrificeList) {
   AltarType type = AltarType.getTypeFromString(typeStr);
   switch (type) {
     case MINING_ALTAR:
       return new MiningAltar(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList);
     case OCEAN_ALTAR:
       return new OceanAltar(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList);
     case NETHER_ALTAR:
       return new NetherAltar(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList);
     case FARM_ALTAR:
     default:
      return new FarmAltar(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList);
   }
 }

  public Sacrifice finishSacrifice(Sacrifice finished) {
    sacrifices.remove(finished);
    this.link.incrementSacrifices();
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this);
    sacrifices.add(newSacrifice);
    AltarMeta tempMeta = this;
    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        SQLLinker.pushToDB(tempMeta);
      }
    });
    return newSacrifice;
  }

  public void addNewSacrifice() {
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this);
    sacrifices.add(newSacrifice);
  }

  public void removeSacrifice() {
    sacrifices.remove(sacrifices.size() - 1);
  }

  public void doEffect() {
    World world = interfaceLoc.getWorld();
    // The interval between lightning strikes. Measured in ticks.
    final int STRIKE_INTERVAL = 40;
    int timeOffset = STRIKE_INTERVAL;
    if (this.link.getLevel() == 1) {
      transformLightningRodsOrCandles(true);
    }
    ArrayList<Block> strikeables = this.getCandles();
    strikeables.addAll(this.getLightningRods());
    for (Block block : strikeables) {
      (new LightCandles(block, this.link.getLevel(), this.structure)).runTaskLater(AltarMain.getPlugin(), timeOffset);
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

  private ArrayList<Block> getLightningRods() {
    int size = this.structure.getSize();
    ArrayList<Block> ret = new ArrayList<>();
    for (int i = -size; i < size * 2; i++) {
      for (int j = -size; j < size * 2; j++) {
        for (int k = -size; k < size * 2; k++) {
          Location currLoc = new Location(interfaceLoc.getWorld(), interfaceLoc.getX() + i, interfaceLoc.getY() + j, interfaceLoc.getZ() + k);
          Block block = currLoc.getBlock();
          if (block.getType() == Material.LIGHTNING_ROD) {
            ret.add(block);
          }
        }
      }
    }
    return ret;
  }

  public void transformLightningRodsOrCandles(boolean toCandles) {
    if (toCandles) {
      ArrayList<Block> lightningRods = this.getLightningRods();
      for (Block block : lightningRods) {
        block.setType(this.structure.getCandleType());
      }
    } else {
      ArrayList<Block> candles = this.getCandles();
      for (Block block : candles) {
        block.setType(Material.LIGHTNING_ROD);
      }
    }
  }

  public void setCandles(int amt) {
    if (amt > 0) {
      ArrayList<Block> candles = getCandles();
      for (Block block : candles) {
        Candle candle = (Candle) block.getBlockData();
        candle.setCandles(amt);
        block.setBlockData(candle);
      }
    } else {
      ArrayList<Block> candles = getCandles();
      for (Block block : candles) {
        block.setType(Material.LIGHTNING_ROD);
      }
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

  public void setLocation(Location newLoc) {
    this.interfaceLoc = newLoc;
    AltarMeta tempMeta = this;
    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        SQLLinker.pushToDB(tempMeta);
      }
    });
  }

  public AltarType getType() {
    return this.type;
  }

  public TownAltarLink getLink() {
    return this.link;
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public Material getSacrificeType() {
    List<Material> sacrificeTypes = this.getSacrificeTypes();
    // Bukkit.getLogger().info("Sacrifice types");
    // for (int i = 0; i < sacrificeTypes.size(); i++) {
    //   Bukkit.getLogger().info("- " + sacrificeTypes.get(i));
    // }
    return sacrificeTypes.get(rng.nextInt(sacrificeTypes.size()));
  }

  public Sacrifice getSacrifice(int index) {
    if (index < sacrifices.size()) {
      return sacrifices.get(index);
    } else if (index >= sacrifices.size() && index < getNumSacrificeSlots()) {
      Sacrifice sacrifice = SacrificeManager.getNewSacrifice(this);
      this.sacrifices.add(sacrifice);
      return sacrifice;
    } else {
      return null;
    }
  }

  public int getNumSacrificeSlots() {
    return Math.max(1, this.link.getLevel());
  }

  public double getSacrificeWeight(Material type) {
    return weightMap.get(type);
  }

  public Location getLocation() {
    return this.interfaceLoc;
  }

  public int getLevel() {
    return this.link.getLevel();
  }

  public int getMaxLevel() {
    return this.link.getMaxLevel();
  }

  public Town getTown() {
    return this.link.getTown();
  }

  public AltarStructure getStructure() {
    return this.structure;
  }

  public ArrayList<Sacrifice> getSacrifices() {
    return this.sacrifices;
  }

  public abstract List<Material> getSacrificeTypes();
}
