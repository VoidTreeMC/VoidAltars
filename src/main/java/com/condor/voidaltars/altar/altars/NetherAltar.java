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
import com.condor.voidaltars.altar.multiblock.structures.NetherAltarStructure;
import com.condor.voidaltars.altar.TownAltarLink;

public class NetherAltar extends AltarMeta {

  private static HashMap<Material, Double> weightMap = new HashMap<>();

  static {
    weightMap.put(Material.SOUL_SAND, 10.00);
    weightMap.put(Material.GLOWSTONE, 29.40);
    weightMap.put(Material.SHROOMLIGHT, 8.00);
    weightMap.put(Material.BASALT, 40.00);
    weightMap.put(Material.CRIMSON_NYLIUM, 6.00);
    weightMap.put(Material.WARPED_NYLIUM, 6.00);
    weightMap.put(Material.NETHERRACK, 0.20);
    weightMap.put(Material.NETHER_BRICKS, 0.89);
    weightMap.put(Material.RED_NETHER_BRICKS, 8.16);
    weightMap.put(Material.CRYING_OBSIDIAN, 62.50);
    weightMap.put(Material.OBSIDIAN, 31.25);
    weightMap.put(Material.CRIMSON_STEM, 3.00);
    weightMap.put(Material.WARPED_STEM, 3.00);
    weightMap.put(Material.CRIMSON_FUNGUS, 6.00);
    weightMap.put(Material.NETHER_WART, 3.00);
    weightMap.put(Material.WARPED_FUNGUS, 6.00);
    weightMap.put(Material.BLAZE_POWDER, 2.63);
    weightMap.put(Material.MAGMA_CREAM, 8.01);
    weightMap.put(Material.QUARTZ, -1.00);
    weightMap.put(Material.LAVA_BUCKET, 69.62);
    weightMap.put(Material.NETHER_BRICK, 0.21);
    weightMap.put(Material.NETHER_GOLD_ORE, 25.00);
    weightMap.put(Material.NETHER_QUARTZ_ORE, 31.50);
    weightMap.put(Material.NETHER_SPROUTS, 5.00);
    weightMap.put(Material.NETHER_WART_BLOCK, 28.35);
    weightMap.put(Material.SOUL_SOIL, 10.00);
  }

  public NetherAltar(TownAltarLink link, Location interfaceLoc, UUID uuid) throws NotInATownException, WrongTownException {
    super(link, AltarType.NETHER_ALTAR, uuid, interfaceLoc, AltarManager.getStructureFromLoc(interfaceLoc, false), weightMap);
  }

  public NetherAltar(UUID uuid, String typeStr, UUID townUUID, String worldStr, double x, double y, double z,
                   ArrayList<byte[]> sacrificeList) {
    // TODO: Check to make sure that by not calling AltarManager.getStructureFromLoc(), we're not allowing invalid altars to persist once created in the DB
    super(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList, weightMap, new NetherAltarStructure());
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
