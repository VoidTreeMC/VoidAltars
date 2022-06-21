package com.condor.voidaltars.altar;

import java.util.Random;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.condor.voidaltars.constants.StringConstants;

public class RewardGenerator {

  private static final double RANDOM_REWARD_CHANCE = 0.03;
  // TODO: Abstract this to config, maybe make configurable chances for rewards
  private static final HashMap<String, String> rewardCommands = new HashMap<>();
  private static final String PLAYER_NAME_PLACEHOLDER = "$PLAYERNAME";
  static {
    rewardCommands.put("getcustomitem voidcoin " + PLAYER_NAME_PLACEHOLDER + " 5", " 5 void coins");
    rewardCommands.put("minecraft:effect give " + PLAYER_NAME_PLACEHOLDER + " minecraft:hero_of_the_village 600 0", " 10 minutes of hero-of-the-village");
    rewardCommands.put("give " + PLAYER_NAME_PLACEHOLDER + " netherite_ingot 1", " one netherite ingot");
  }

  private static Random rng = new Random();

  private static double getLevelChanceModifier(int level) {
    switch (level) {
      case 4:
        return 2.0;
      case 3:
        return 1.75;
      case 2:
        return 1.5;
      case 1:
      case 0:
      default:
        return 1.0;
    }
  }

  /**
   * Has a chance to reward the player for
   * completing a sacrifice.
   * @param player  The player to be rewarded
   * @param level   The level of the altar
   */
  public static void rollAndReward(Player player, int level) {
    double chance = rng.nextDouble() * getLevelChanceModifier(level);
    if (chance <= RANDOM_REWARD_CHANCE) {
      RewardGenerator.rewardPlayer(player);
    }
  }

  /**
   * Rewards the player for completing a sacrifice. Rewards are
   * chosen from the table of random rewards
   * @param player  The player to be rewarded
   */
  public static void rewardPlayer(Player player) {
    String command = (String) rewardCommands.keySet().toArray()[rng.nextInt(rewardCommands.size())];
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace(PLAYER_NAME_PLACEHOLDER, player.getName()));
    player.sendMessage(StringConstants.RANDOM_REWARD.get() + ChatColor.GOLD + rewardCommands.get(command));
  }
}
