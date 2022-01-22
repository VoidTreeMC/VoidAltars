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
import com.condor.voidaltars.altar.multiblock.structures.OceanAltarStructure;
import com.condor.voidaltars.altar.TownAltarLink;

/**
 * Altar for ocean sacrifices
 */
public class OceanAltar extends AltarMeta {

  private static HashMap<Material, Double> weightMap = new HashMap<>();

  static {
    weightMap.put(Material.TURTLE_EGG, 16.3);
    weightMap.put(Material.MAGMA_BLOCK, 5.0);
    weightMap.put(Material.SEA_LANTERN, 66.15);
    weightMap.put(Material.PRISMARINE, 29.4);
    weightMap.put(Material.DARK_PRISMARINE, 30.0);
    weightMap.put(Material.BRAIN_CORAL, 10.0);
    weightMap.put(Material.BRAIN_CORAL_BLOCK, 16.0);
    weightMap.put(Material.BRAIN_CORAL_FAN, 10.0);
    weightMap.put(Material.BUBBLE_CORAL, 10.0);
    weightMap.put(Material.BUBBLE_CORAL_BLOCK, 16.0);
    weightMap.put(Material.BUBBLE_CORAL_FAN, 10.0);
    weightMap.put(Material.FIRE_CORAL, 10.0);
    weightMap.put(Material.FIRE_CORAL_BLOCK, 16.0);
    weightMap.put(Material.FIRE_CORAL_FAN, 10.0);
    weightMap.put(Material.HORN_CORAL, 10.0);
    weightMap.put(Material.HORN_CORAL_BLOCK, 16.0);
    weightMap.put(Material.HORN_CORAL_FAN, 10.0);
    weightMap.put(Material.TUBE_CORAL, 10.0);
    weightMap.put(Material.TUBE_CORAL_BLOCK, 16.0);
    weightMap.put(Material.TUBE_CORAL_FAN, 10.0);
    weightMap.put(Material.KELP, 3.0);
    weightMap.put(Material.COOKED_SALMON, 6.3);
    weightMap.put(Material.SALMON_BUCKET, 79.56);
    weightMap.put(Material.SPONGE, 525.0);
    weightMap.put(Material.DRIED_KELP, 4.0);
    weightMap.put(Material.PUFFERFISH, 50.0);
    weightMap.put(Material.SALMON, 6.0);
    weightMap.put(Material.SEA_PICKLE, 6.0);
    weightMap.put(Material.TROPICAL_FISH, 50.0);
  }

  public OceanAltar(TownAltarLink link, Location interfaceLoc, UUID uuid) throws NotInATownException, WrongTownException {
    super(link, AltarType.OCEAN_ALTAR, uuid, interfaceLoc, AltarManager.getStructureFromLoc(interfaceLoc, false), weightMap);
  }

  public OceanAltar(UUID uuid, String typeStr, UUID townUUID, String worldStr, double x, double y, double z,
                   ArrayList<byte[]> sacrificeList) {
    // TODO: Check to make sure that by not calling AltarManager.getStructureFromLoc(), we're not allowing invalid altars to persist once created in the DB
    super(uuid, typeStr, townUUID, worldStr, x, y, z, sacrificeList, weightMap, new OceanAltarStructure());
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
