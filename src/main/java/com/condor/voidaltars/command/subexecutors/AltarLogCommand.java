package com.condor.voidaltars.command.subexecutors;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.transaction.AltarTransaction;
import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.command.SubCommand;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Command that shows a list of altar transactions
 * in a paginated display
 * Usage: /altar log {page number}
 *        /altar history {page number}
 */
public class AltarLogCommand extends SubCommand {

	public final static String NAME = "log";
	public final static String ALT_NAME = "history";

	@Override
	public FailureCode execute(CommandSender sender, String label, String[] args) {

		Player player = (Player) sender;
    int pageNum = 1;

    //If they specified a page number
		if (args.length > 1) {
			//If the page number is valid
			try {
				pageNum = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
			  player.sendMessage("Invalid page number entered. Please try again.");
			  return FailureCode.SUCCESS;
			}
		}

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

    ArrayList<AltarTransaction> transacList = link.getTransacCache().getPage(pageNum);
    player.sendMessage(ChatColor.GREEN + "========" + town.getName() + " Altar Log========");
    for (AltarTransaction transac : transacList) {
      player.sendMessage(transac.toString());
    }
    final int IDEAL_SUFFIX_LENGTH = 11;
    int pageNumChars = ("" + pageNum).length();
    String suffix = "";
    for (int i = 0; i < IDEAL_SUFFIX_LENGTH - pageNumChars; i++) {
      suffix += "=";
    }
    player.sendMessage(ChatColor.GREEN + "========Page " + pageNum + suffix);

		return FailureCode.SUCCESS;
	}
}
