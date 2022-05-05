package com.condor.voidaltars.command.subexecutors;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.command.SubCommand;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.leaderboard.LeaderboardParser;
import com.condor.voidaltars.leaderboard.AltarRank;

/**
 * Command that shows the list of altars
 * in descending rank
 * Usage: /altar top {page number}
 */
public class AltarTopCommand extends SubCommand {

	public final static String NAME = "top";

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

    ArrayList<AltarRank> rankList = LeaderboardParser.getPage(pageNum);
    player.sendMessage(ChatColor.GREEN + "========Altar Leaderboard========");
    for (int i = 0; i < rankList.size(); i++) {
      player.sendMessage(ChatColor.YELLOW + "" + (i + 1) + rankList.get(i).toString());
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
