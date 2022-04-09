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
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

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
import com.condor.voidaltars.constants.StringConstants;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

/**
 * Represents an altar's data. Most altar
 * interactions are handled here.
 */
public abstract class AltarMeta {
  // The type of altar
  AltarType type;
  // The altar's town-altar link
  TownAltarLink link;
  // The location of the altar's interface block
  Location interfaceLoc;
  // The altar's structure
  AltarStructure structure;
  // A list of the altar's currently demanded sacrifices
  ArrayList<Sacrifice> sacrifices = new ArrayList<>();
  // The ID of the altar
  UUID uuid;
  // A map of sacrifice materials to their associated weights
  HashMap<Material, Double> weightMap;

  private static Random rng = new Random();

  /**
   * A constructor for an AltarMeta object.
   * Intended for creating a new AltarMeta object, rather than
   * reconstructing one from the database
   * @param  link                              The altar's town-altar link
   * @param  type                              The type of the altar
   * @param  uuid                              The altar's UUID
   * @param  interfaceLoc                      The location of the interface block
   * @param  structure                         The altar's structure
   * @param  weightMap                         The altar's map of sacrifice types to their weights
   * @throws NotInATownException               Thrown if the altar is not located in a town
   * @throws WrongTownException                Thrown if the altar is located in a town other than its registered town
   */
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
      sacrifices.add(SacrificeManager.getNewSacrifice(this, getCurrentSacrificeMaterials()));
    }
    AltarMeta tempMeta = this;
    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        SQLLinker.pushToDB(tempMeta);
      }
    });
  }

  /**
   * A constructor for an AltarMeta object.
   * Intended for reconstructing an AltarMeta
   * object from the database
   * @param uuid           The altar's UUID
   * @param type           The type of the altar
   * @param townUUID       The UUID of the town the altar is linked to
   * @param worldStr       The name of the world the altar is in
   * @param x              The X coordinate at which the altar's interface block is located
   * @param y              The Y coordinate at which the altar's interface block is located
   * @param z              The Z coordinate at which the altar's interface block is located
   * @param sacrificeList  An arraylist of bytes containing the altar's current sacrifices
   * @param weightMap      The altar's map of sacrifice types to their weights
   * @param structure      The altar's structure
   */
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

 /**
  * Reconstructs an altar of the specified type
  * @param  uuid                        The altar's UUID
  * @param  typeStr                     The type of altar to create
  * @param  townUUID                    The UUID of the town with which the altar is associated
  * @param  worldStr                    The name of the world in which the altar is located
  * @param  x                           The X coordinate of the altar's interface block
  * @param  y                           The Y coordinate of the altar's interface block
  * @param  z                           The Z coordinate of the altar's interface block
  * @param  sacrificeList               An arraylist of bytes containing the altar's current sacrifices
  * @return                             A new AltarMeta
  */
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

 /**
  * Handles behavior for when a sacrifice is finished
  * @param  finished               The sacrifice that was finished
  * @param  player                 The player that finished the sacrifice
  * @return                        The new sacrifice that the altar wants instead
  */
  public Sacrifice finishSacrifice(Sacrifice finished, Player player) {
    sacrifices.remove(finished);
    if (getLink().getSacrificesRemaining() == 1) {
      String townName = getLink().getTown().getName();
      TextComponent helpURLComponent = new TextComponent(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + StringConstants.ALTAR_HELP_URL.get());
      helpURLComponent.setClickEvent(new ClickEvent(Action.OPEN_URL, StringConstants.ALTAR_HELP_URL.get()));
      for (Player p : Bukkit.getOnlinePlayers()) {
        p.sendMessage(ChatColor.YELLOW + "The gods are pleased with " + ChatColor.GOLD + townName +
                      ChatColor.YELLOW + " for completing all of their sacrifices this week. " +
                      ChatColor.GOLD + townName + "'s'" + ChatColor.YELLOW + " altar is level " +
                      ChatColor.GOLD + getLink().getLevel() + ChatColor.YELLOW + ".");
        p.sendMessage(helpURLComponent);
      }
    }
    this.link.incrementSacrifices();
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this, getCurrentSacrificeMaterials());
    sacrifices.add(newSacrifice);
    AltarMeta tempMeta = this;
    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        SQLLinker.pushToDB(tempMeta);
      }
    });
    RewardGenerator.rollAndReward(player);
    return newSacrifice;
  }

  /**
   * Adds a new sacrifice to the list
   */
  public void addNewSacrifice() {
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this, getCurrentSacrificeMaterials());
    sacrifices.add(newSacrifice);
  }

  /**
   * Gets a list of the material types for the
   * altar's current sacrifices
   * @return A list of materials for the altar's current sacrifices
   */
  public ArrayList<Material> getCurrentSacrificeMaterials() {
    ArrayList<Material> ret = new ArrayList<>();
    for (Sacrifice s : sacrifices) {
      ret.add(s.getType());
    }
    return ret;
  }

  /**
   * Removes the sacrifice at the last index of the list
   */
  public void removeSacrifice() {
    sacrifices.remove(sacrifices.size() - 1);
  }

  /**
   * Plays an in-world animation with sound at the location.
   * Used for when an altar is activated or leveled up.
   */
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

  /**
   * Returns a list of blocks at which
   * candles are located around the altar.
   * @return A list of candle blocks near the altar
   */
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

  /**
   * Returns a list of blocks at which
   * lightning rods are located around the altar.
   * @return A list of lightning rod blocks near the altar
   */
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

  /**
   * Transforms lightning rods to candles, or
   * candles to lightning rods near the altar.
   * @param toCandles  True if lightning rods should be turned to candles, false if the other way around
   */
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

  /**
   * Sets candles near the altar to a specified number
   * e.g. 4 candles per block instead of 3
   * @param amt  The new amount of candles
   */
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

  /**
   * Lights or unlights the candle at the block
   * @param block  The candle to be lit or unlit
   * @param state  The state of the candle (true: lit; false: unlit)
   * TODO: Maybe move to a different file
   */
  public static void setCandleLit(Block block, boolean state) {
    Candle candle = (Candle) block.getBlockData();
    candle.setLit(state);
    block.setBlockData(candle);
  }

  /**
   * Lights or unlights all the candles near the altar
   * @param state  The state of the candle (true: lit; false: unlit)
   */
  public void setCandlesLit(boolean state) {
    ArrayList<Block> candles = getCandles();
    for (Block block : candles) {
      setCandleLit(block, state);
    }
  }

  /**
   * Sets the location of the altar's interface block
   * @param newLoc  The new location
   */
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

  /**
   * Gets the type of the next sacrifice that the
   * altar wants
   * @param  toAvoid  A list of materials to avoid
   * @return          The next sacrifice type
   */
  public Material getSacrificeType(ArrayList<Material> toAvoid) {
    List<Material> sacrificeTypes = this.getSacrificeTypes();
    ArrayList<Material> finalList = new ArrayList<>();
    if (toAvoid != null) {
      for (Material mat : sacrificeTypes) {
        if (!toAvoid.contains(mat)) {
          finalList.add(mat);
        }
      }
      return finalList.get(rng.nextInt(finalList.size()));
    }
    return sacrificeTypes.get(rng.nextInt(sacrificeTypes.size()));
  }

  /**
   * Gets the sacrifice at the specified index
   * @param  index               The index of the sacrifice slot (0-3)
   * @return                     The sacrifice at that slot
   */
  public Sacrifice getSacrifice(int index) {
    if (index < sacrifices.size()) {
      return sacrifices.get(index);
    } else if (index >= sacrifices.size() && index < getNumSacrificeSlots()) {
      Sacrifice sacrifice = SacrificeManager.getNewSacrifice(this, null);
      this.sacrifices.add(sacrifice);
      return sacrifice;
    } else {
      return null;
    }
  }

  /**
   * Returns the number of sacrifice slots that
   * this altar has available
   * @return The number of sacrifice slots available
   */
  public int getNumSacrificeSlots() {
    return Math.max(1, this.link.getLevel());
  }

  /**
   * Returns the weight of the sacrifice
   * of a specific type
   * @param  type               The type of the sacrifice
   * @return                    The weight of the sacrifice
   */
  public double getSacrificeWeight(Material type) {
    return weightMap.get(type);
  }

  /**
   * Returns the location of the altar's
   * interface block
   * @return The location of the altar's interface block
   */
  public Location getLocation() {
    return this.interfaceLoc;
  }

  /**
   * Gets the altar's current level
   * @return The altar's current level
   */
  public int getLevel() {
    return this.link.getLevel();
  }

  /**
   * Gets the altar's maximum level
   * @return The altar's maximum level
   */
  public int getMaxLevel() {
    return this.link.getMaxLevel();
  }

  /**
   * Gets the town with which the altar is linked
   * @return The town with which the altar is linked
   */
  public Town getTown() {
    return this.link.getTown();
  }

  /**
   * Gets the altar's structure
   * @return The altar's structure
   */
  public AltarStructure getStructure() {
    return this.structure;
  }

  /**
   * A list of the altar's current sacrifices
   * @return The altar's current sacrifices
   */
  public ArrayList<Sacrifice> getSacrifices() {
    return this.sacrifices;
  }

  /**
   * Clears the altar's sacrifices list
   */
  public void clearSacrifices() {
    this.sacrifices.clear();
  }

  /**
   * Returns a list of item types that the
   * altar may demand as a sacrifice
   * @return A list of item types that the altar may demand
   */
  public abstract List<Material> getSacrificeTypes();
}
