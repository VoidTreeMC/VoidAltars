package com.condor.voidaltars.leaderboard;

import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.altar.SacrificeManager;
import com.condor.voidaltars.altar.Sacrifice;
import com.condor.voidaltars.main.AltarMain;

public class LeaderboardParser {

  // The maximum number of entries in a page
  public static final int PAGE_SIZE = 10;

  private static ArrayList<AltarRank> rankList = new ArrayList<>();

  public static void init() {
    rankList = SQLLinker.getLeaderboardPoints();
  }

  public static ArrayList<AltarRank> getPage(int pageNum) {
    ArrayList<AltarRank> ret = new ArrayList<>();
    if (((pageNum - 1) * PAGE_SIZE) > rankList.size()) {
      return ret;
    }
    int upperBound = Math.min(pageNum * PAGE_SIZE, rankList.size());
    for (int i = ((pageNum - 1) * PAGE_SIZE); i < upperBound; i++) {
      ret.add(rankList.get(i));
    }
    return ret;
  }

  public static int getCurrentPosition(UUID townUUID) {
    for (int i = 0; i < rankList.size(); i++) {
      if (rankList.get(i).getUUID().equals(townUUID)) {
        return (i + 1);
      }
    }
    return -1;
  }

  public static void parseSacrifice(Sacrifice sac) {
    double weight = sac.getOwner().getSacrificeWeight(sac.getType());
    weight = (weight > 0) ? weight : 100;
    Material mat = sac.getType();
    UUID townUUID = sac.getOwner().getLink().getTown().getUUID();
    int maxStackAmount = (new ItemStack(mat)).getMaxStackSize();
    int score = (int) (Math.max(1, (int) ((0.5 * 1 / weight) * maxStackAmount / SacrificeManager.STACK_SIZE_FACTOR)));

    boolean found = false;
    for (AltarRank rank : rankList) {
      if (rank.getUUID().equals(townUUID)) {
        rank.setPoints(rank.getPoints() + score);
        found = true;
        break;
      }
    }
    if (!found) {
      AltarRank rank = new AltarRank(townUUID, score);
      rankList.add(rank);
    }
    Collections.sort(rankList);

    Bukkit.getScheduler().runTaskAsynchronously(AltarMain.getPlugin(), new Runnable() {
      @Override
      public void run() {
        SQLLinker.pushToLeaderboard(townUUID, score);
      }
    });
  }
}
