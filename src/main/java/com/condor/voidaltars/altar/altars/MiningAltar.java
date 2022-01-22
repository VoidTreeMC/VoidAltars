package com.condor.voidaltars.altar.altars;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Location;

import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.exception.NotInATownException;
import com.condor.voidaltars.altar.exception.WrongTownException;
import com.condor.voidaltars.altar.multiblock.structures.MiningAltarStructure;
import com.condor.voidaltars.altar.TownAltarLink;

/**
 * Altar for mining sacrifices
 */
public class MiningAltar extends AltarMeta {

  private static HashMap<Material, Double> weightMap = new HashMap<>();

  static {
    weightMap.put(Material.CALCITE, 16.00);
    weightMap.put(Material.DRIPSTONE_BLOCK, 15.00);
    weightMap.put(Material.STONE, 0.68);
    weightMap.put(Material.TUFF, 2.00);
    weightMap.put(Material.ANDESITE, 0.72);
    weightMap.put(Material.BLACKSTONE, 0.75);
    weightMap.put(Material.COBBLESTONE, 0.68);
    weightMap.put(Material.COBBLED_DEEPSLATE, 0.68);
    weightMap.put(Material.DEEPSLATE, 0.72);
    weightMap.put(Material.DIORITE, 0.72);
    weightMap.put(Material.GRAVEL, 3.00);
    weightMap.put(Material.GRANITE, 0.72);
    weightMap.put(Material.POINTED_DRIPSTONE, 15.00);
    weightMap.put(Material.AMETHYST_BLOCK, 50.00);
    weightMap.put(Material.AMETHYST_SHARD, 10.00);
    weightMap.put(Material.COAL, 30.00);
    weightMap.put(Material.COPPER_INGOT, 30.00);
    weightMap.put(Material.DIAMOND, 250.00);
    weightMap.put(Material.EMERALD, 10.00);
    weightMap.put(Material.GOLD_INGOT, 45.00);
    weightMap.put(Material.IRON_INGOT, 45.00);
    weightMap.put(Material.LAPIS_LAZULI, 20.00);
    weightMap.put(Material.REDSTONE, 5.00);
    weightMap.put(Material.EMERALD_ORE, 150.00);
    weightMap.put(Material.FLINT, 10.00);
    weightMap.put(Material.GOLD_ORE, 60.00);
    weightMap.put(Material.LAPIS_ORE, 80.00);
    weightMap.put(Material.SMOOTH_BASALT, 42.00);
  }

  public MiningAltar(TownAltarLink link, Location interfaceLoc, UUID uuid) throws NotInATownException, WrongTownException {
    super(link, AltarType.MINING_ALTAR, uuid, interfaceLoc, AltarManager.getStructureFromLoc(interfaceLoc, false), weightMap);
  }

  public MiningAltar(UUID uuid, String typeStr, UUID townUUID, String worldStr, double x, double y, double z,
                   ArrayList<byte[]> sacrificeList) {
    // TODO: Check to make sure that by not calling AltarManager.getStructureFromLoc(), we're not allowing invalid altars to persist once created in the DB
    super(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList, weightMap, new MiningAltarStructure());
  }

  public static void clearWeightMap() {
    weightMap.clear();
  }

  public static void addToWeightMap(Material mat, Double weight) {
    weightMap.put(mat, weight);
  }

  public List<Material> getSacrificeTypes() {
    ArrayList<Material> matList = new ArrayList<>();
    for (Material m : weightMap.keySet()) {
      matList.add(m);
    }
    return matList;
  }
}
