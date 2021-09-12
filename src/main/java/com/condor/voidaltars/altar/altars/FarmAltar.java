package com.condor.voidaltars.altar.altars;

import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Location;

import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.exception.NotInATownException;

public class FarmAltar extends AltarMeta {

  private static TreeMap<Material, Double> weightMap = new TreeMap<>();

  static {
    weightMap.put(Material.HAY_BLOCK, 47.25);
    weightMap.put(Material.HONEY_BLOCK, 33.34);
    weightMap.put(Material.GRASS_BLOCK, 8.00);
    weightMap.put(Material.MYCELIUM, 8.00);
    weightMap.put(Material.PODZOL, 8.00);
    weightMap.put(Material.ACACIA_LOG, 3.00);
    weightMap.put(Material.BIRCH_LOG, 3.00);
    weightMap.put(Material.DARK_OAK_LOG, 3.00);
    weightMap.put(Material.OAK_LOG, 3.00);
    weightMap.put(Material.SPRUCE_LOG, 3.00);
    weightMap.put(Material.HONEYCOMB_BLOCK, 13.23);
    weightMap.put(Material.ACACIA_LEAVES, 3.00);
    weightMap.put(Material.AZALEA_LEAVES, 3.15);
    weightMap.put(Material.BIRCH_LEAVES, 3.00);
    weightMap.put(Material.DARK_OAK_LEAVES, 3.00);
    weightMap.put(Material.FLOWERING_AZALEA_LEAVES, 3.15);
    weightMap.put(Material.OAK_LEAVES, 3.00);
    weightMap.put(Material.SPRUCE_LEAVES, 3.00);
    weightMap.put(Material.ACACIA_SAPLING, 3.00);
    weightMap.put(Material.AZALEA, 3.00);
    weightMap.put(Material.BEE_NEST, -1.00);
    weightMap.put(Material.BEEHIVE, 16.87);
    weightMap.put(Material.BEETROOT_SEEDS, 3.00);
    weightMap.put(Material.CARROT, 5.00);
    weightMap.put(Material.EGG, 5.00);
    weightMap.put(Material.MELON, 9.45);
    weightMap.put(Material.MELON_SEEDS, 3.00);
    weightMap.put(Material.POTATO, 5.00);
    weightMap.put(Material.PUMPKIN, 5.00);
    weightMap.put(Material.PUMPKIN_SEEDS, 1.19);
    weightMap.put(Material.SUGAR_CANE, 0.10);
    weightMap.put(Material.WHEAT, 5.00);
    weightMap.put(Material.WHEAT_SEEDS, 3.00);
    weightMap.put(Material.BEETROOT, 5.00);
    weightMap.put(Material.APPLE, 10.00);
    weightMap.put(Material.BREAD, 15.75);
    weightMap.put(Material.CAKE, 240.68);
    weightMap.put(Material.COOKIE, 2.10);
    weightMap.put(Material.GLOW_BERRIES, 1.00);
    weightMap.put(Material.HONEY_BOTTLE, 7.94);
    weightMap.put(Material.BAKED_POTATO, 5.25);
    weightMap.put(Material.BEETROOT_SOUP, 38.72);
    weightMap.put(Material.MUSHROOM_STEW, 5.12);
    weightMap.put(Material.HONEYCOMB, 3.15);
    weightMap.put(Material.MILK_BUCKET, 69.67);
    weightMap.put(Material.POISONOUS_POTATO, -1.00);
    weightMap.put(Material.SWEET_BERRIES, 5.00);
  }

  public FarmAltar(Location interfaceLoc) throws NotInATownException {
    super(AltarType.FARM_ALTAR, interfaceLoc, AltarManager.getStructureFromLoc(interfaceLoc), weightMap);
  }

  public List<Material> getSacrificeTypes() {
    ArrayList<Material> matList = new ArrayList<>();
    for (Material m : weightMap.keySet()) {
      matList.add(m);
    }
    return matList;
  }
}
