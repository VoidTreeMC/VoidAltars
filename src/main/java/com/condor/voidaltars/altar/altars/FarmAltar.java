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
import com.condor.voidaltars.altar.multiblock.structures.FarmAltarStructure;
import com.condor.voidaltars.altar.TownAltarLink;

/**
 * Altar for agricultural sacrifices
 */
public class FarmAltar extends AltarMeta {

  private static HashMap<Material, Double> weightMap = new HashMap<>();

  static {
    weightMap.put(Material.HAY_BLOCK, 45.0);
    weightMap.put(Material.GRASS_BLOCK, 3.0);
    weightMap.put(Material.MYCELIUM, 10.0);
    weightMap.put(Material.PODZOL, 8.0);
    weightMap.put(Material.ACACIA_LOG, 3.0);
    weightMap.put(Material.BIRCH_LOG, 3.0);
    weightMap.put(Material.DARK_OAK_LOG, 3.0);
    weightMap.put(Material.OAK_LOG, 3.0);
    weightMap.put(Material.SPRUCE_LOG, 3.0);
    weightMap.put(Material.ACACIA_LEAVES, 3.0);
    weightMap.put(Material.BIRCH_LEAVES, 3.0);
    weightMap.put(Material.DARK_OAK_LEAVES, 3.0);
    weightMap.put(Material.OAK_LEAVES, 3.0);
    weightMap.put(Material.SPRUCE_LEAVES, 3.0);
    weightMap.put(Material.ACACIA_SAPLING, 3.0);
    weightMap.put(Material.BEE_NEST, -1.0);
    weightMap.put(Material.BEEHIVE, 50.0);
    weightMap.put(Material.BEETROOT_SEEDS, 3.0);
    weightMap.put(Material.CARROT, 1.0);
    weightMap.put(Material.EGG, 3.0);
    weightMap.put(Material.MELON, 9.45);
    weightMap.put(Material.MELON_SEEDS, 3.0);
    weightMap.put(Material.POTATO, 5.0);
    weightMap.put(Material.PUMPKIN, 5.0);
    weightMap.put(Material.PUMPKIN_SEEDS, 1.19);
    weightMap.put(Material.SUGAR_CANE, 0.5);
    weightMap.put(Material.WHEAT, 5.0);
    weightMap.put(Material.WHEAT_SEEDS, 3.0);
    weightMap.put(Material.BEETROOT, 5.0);
    weightMap.put(Material.APPLE, 10.0);
    weightMap.put(Material.BREAD, 15.75);
    weightMap.put(Material.CAKE, 2.0);
    weightMap.put(Material.COOKIE, 2.1);
    weightMap.put(Material.GLOW_BERRIES, 20.0);
    weightMap.put(Material.HONEY_BOTTLE, 7.94);
    weightMap.put(Material.BAKED_POTATO, 5.25);
    weightMap.put(Material.BEETROOT_SOUP, 1.0);
    weightMap.put(Material.MUSHROOM_STEW, 0.7);
    weightMap.put(Material.HONEYCOMB, 5.0);
    weightMap.put(Material.MILK_BUCKET, 0.7);
    weightMap.put(Material.POISONOUS_POTATO, 250.0);
    weightMap.put(Material.SWEET_BERRIES, 5.0);
  }

  public FarmAltar(TownAltarLink link, Location interfaceLoc, UUID uuid) throws NotInATownException, WrongTownException {
    super(link, AltarType.FARM_ALTAR, uuid, interfaceLoc, AltarManager.getStructureFromLoc(interfaceLoc, false), weightMap);
  }

  public FarmAltar(UUID uuid, String typeStr, UUID townUUID, String worldStr, double x, double y, double z,
                   ArrayList<byte[]> sacrificeList) {
    // TODO: Check to make sure that by not calling AltarManager.getStructureFromLoc(), we're not allowing invalid altars to persist once created in the DB
    super(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList, weightMap, new FarmAltarStructure());
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
