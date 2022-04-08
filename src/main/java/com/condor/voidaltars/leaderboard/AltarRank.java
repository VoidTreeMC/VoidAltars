package com.condor.voidaltars.leaderboard;

import java.lang.Comparable;
import java.util.UUID;

import org.bukkit.ChatColor;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class AltarRank implements Comparable<AltarRank> {
  UUID townUUID;
  int points;

  public AltarRank(UUID townUUID, int points) {
    this.townUUID = townUUID;
    this.points = points;
  }

  public int getPoints() {
    return this.points;
  }

  public UUID getUUID() {
    return this.townUUID;
  }

  public void setPoints(int newPoints) {
    this.points = newPoints;
  }

  public void setUUID(UUID newUUID) {
    this.townUUID = newUUID;
  }

  public String toString() {
    String ret = ": ";
    try {
      String townName = TownyAPI.getInstance().getDataSource().getTown(townUUID).getName();
      ret += ChatColor.GOLD + townName;
    } catch (NotRegisteredException e) {
      e.printStackTrace();
      ret += "ERROR";
    }
    return ret;
  }

  @Override
  public int compareTo(AltarRank rank) {
    return -Integer.compare(this.points, rank.points);
  }
}
