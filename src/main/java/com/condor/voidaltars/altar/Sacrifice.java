package com.condor.voidaltars.altar;

import java.util.ArrayList;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.lang.ClassNotFoundException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

/**
 * Represents a sacrifice -- a demand from
 * an altar for a specified amount of a specific item
 */
public class Sacrifice {
  Material type;
  int sacrificed;
  int total;
  AltarMeta owner;

  /**
   * Builds a new sacrifice from the data provided
   * @param type   The type of item sacrificed
   * @param total  The total amount of that item demanded
   * @param owner  The altar that owns this sacrifice
   */
  public Sacrifice(Material type, int total, AltarMeta owner) {
    this.type = type;
    this.total = total;
    this.owner = owner;
  }


  /**
   * Builds a new sacrifice from a serialized byte array
   * @param bytes  The byte array containing an arraylist with the Sacrifice's fields
   * @param owner  The altar that owns this sacrifice
   */
  public Sacrifice(byte[] bytes, AltarMeta owner) {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    ObjectInput in = null;
    try {
      in = new ObjectInputStream(bis);
      ArrayList<Object> arr = (ArrayList<Object>) in.readObject();
      this.type = (Material) arr.get(0);
      this.sacrificed = (Integer) arr.get(1);
      this.total = (Integer) arr.get(2);
      this.owner = owner;
    } catch (IOException | ClassNotFoundException e) {
      Bukkit.getLogger().severe("Encountered error when reading Sacrifice from DB.");
    }
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        // ignore close exception
      }
    }
  }

  /**
   * Handles a sacrifice
   * @param  sacrifice An ItemStack representing the items being sacrificed
   * @return The number of sacrifices remaining in the quota. If negative, the quota has been met.
   */
  public int handleSacrifice(ItemStack sacrifice) {
    int count = sacrifice.getAmount();
    this.sacrificed += count;
    return this.total - this.sacrificed;
  }

  /**
   * Gets the number of the sacrifice remaining
   * @return The number of sacrifices remaining
   */
  public int getNumRemaining() {
    return total - sacrificed;
  }

  /**
   * Gets the total number of the sacrifice wanted
   * @return The total number of items wanted by the sacrifice
   */
  public int getTotal() {
    return total;
  }

  /**
   * Gets the number of items sacrificed so far
   * @return The number of items sacrificed so far
   */
  public int getNumSacrificed() {
    return sacrificed;
  }

  /**
   * Gets the type of the item demanded
   * @return The type of the item demanded
   */
  public Material getType() {
    return this.type;
  }

  /**
   * Gets the altar that owns this sacrifice
   * @return The altar that owns the sacrifice
   */
  public AltarMeta getOwner() {
    return this.owner;
  }

  /**
   * Gets whether the sacrifice is finished or not
   * @return True if the sacrifice is finished, false otherwise
   */
  public boolean isFinished() {
    return getNumRemaining() <= 0;
  }

  /**
   * Adds the specified amount of items to the
   * amount of items sacrificed so far
   * @param num  The number of items newly sacrificed
   */
  public void addToSacrificed(int num) {
    this.sacrificed += num;
  }

  /**
   * Serializes the sacrifice to bytes for easy storage
   * Protocol: The object is serialized to an ArrayList<Object>
   * with the following protocol
   * [0]: Material (the type of the item)
   * [1]: int (the amount sacrificed so far)
   * [2]: int (the total amount demanded)
   * @return A byte array containing the sacrifice's data
   */
  public byte[] serialize() {
    ArrayList<Object> toWrite = new ArrayList<>();
    toWrite.add(type);
    toWrite.add(sacrificed);
    toWrite.add(total);
    byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(bos);
      out.writeObject(toWrite);
		  bytes = bos.toByteArray();
      out.close();
      bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		return bytes;
  }
}
