package com.condor.voidaltars.command.subexecutors;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.command.SubCommand;
import com.condor.voidaltars.altar.transaction.AltarTransaction;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.AltarManager;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

/**
 * Command that shows information about a town's altars
 * Usage: /altar status
 */
public class AltarStatusCommand extends SubCommand {

	public final static String NAME = "status";
  private static final int SECONDS_IN_A_MINUTE = 60;
  private static final int SECONDS_IN_AN_HOUR = 60 * SECONDS_IN_A_MINUTE;
  private static final int SECONDS_IN_A_DAY = 24 * SECONDS_IN_AN_HOUR;

	@Override
	public FailureCode execute(CommandSender sender, String label, String[] args) {

		Player player = (Player) sender;

    Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
    if (resident == null) {
      player.sendMessage("You must be in a town in order to use this command.");
      return FailureCode.NOT_IN_A_TOWN;
    }

    Town town = null;
    try {
      town = resident.getTown();
    } catch (NotRegisteredException e) {
      e.printStackTrace();
    }
    TownAltarLink link = AltarManager.getAltarLinkFromTown(town);

    if (link == null) {
      player.sendMessage("Your town has no altars.");
      return FailureCode.SUCCESS;
    }

    int altarLevel = link.getLevel();
    int neededForLevelUp = link.getSacrificesNeededForLevelUp();
    int neededToPleaseGods = link.getSacrificesWanted();
    long nextEvalTime = link.getNextEvalTime();
    long timeUntil = nextEvalTime - System.currentTimeMillis();
    String timeStr = ChatColor.YELLOW + "The gods will review your altar in " + ChatColor.GOLD + getTimeString(timeUntil);

    player.sendMessage(ChatColor.GREEN + "===== Altar Status =====");
    player.sendMessage(ChatColor.AQUA + "Level: " + ChatColor.GOLD + "" + altarLevel);
    if (neededToPleaseGods > 0) {
      player.sendMessage(ChatColor.AQUA + "Sacrifices needed to please gods: " + ChatColor.GOLD + "" + neededToPleaseGods);
    } else {
      player.sendMessage(ChatColor.AQUA + "The gods are pleased with your altar.");
    }
    if (altarLevel < link.getMaxLevel()) {
      player.sendMessage(ChatColor.AQUA + "Sacrifices needed for next level: " + ChatColor.GOLD + "" + neededForLevelUp);
    }
    player.sendMessage(timeStr);

		return FailureCode.SUCCESS;
	}

  public static String getTimeString(long time /* no see */) {
    String ret = "";
    int asSeconds = (int) (time / 1000);
    int days = 0;
    int hours = 0;
    int minutes = 0;
    if (asSeconds / SECONDS_IN_A_DAY >= 1) {
      days = asSeconds / SECONDS_IN_A_DAY;
      ret += days + " day" + ((days > 1) ? "s" : "") + " ";
    }

    asSeconds -= (days * SECONDS_IN_A_DAY);

    if (asSeconds / SECONDS_IN_AN_HOUR >= 1) {
      hours = asSeconds / SECONDS_IN_AN_HOUR;
      ret += hours + " hour" + ((hours > 1) ? "s" : "") + " ";
    }

    asSeconds -= (hours * SECONDS_IN_AN_HOUR);

    if (asSeconds / SECONDS_IN_A_MINUTE >= 1) {
      minutes = asSeconds / SECONDS_IN_A_MINUTE;
      ret += minutes + " minute" + ((minutes > 1) ? "s" : "") + " ";
    }

    asSeconds -= (minutes * SECONDS_IN_A_MINUTE);

    if (asSeconds > 0) {
      ret += "and " + asSeconds + " second" + ((asSeconds > 1) ? "s" : "");
    }

    return ret;
  }
}
