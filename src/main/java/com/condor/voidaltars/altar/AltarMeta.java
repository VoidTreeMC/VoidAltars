package com.condor.voidaltars.altar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

import com.condor.voidaltars.altar.multiblock.AltarStructure;
import com.condor.voidaltars.altar.exception.NotInATownException;

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

    for (int i = 0; i < this.getNumSacrificeSlots(); i++) {
      sacrifices.add(SacrificeManager.getNewSacrifice(this));
    }
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
    // TODO: Method stub
    this.level++;
    Sacrifice newSacrifice = SacrificeManager.getNewSacrifice(this);
    sacrifices.add(newSacrifice);
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

  public abstract List<Material> getSacrificeTypes();
}
