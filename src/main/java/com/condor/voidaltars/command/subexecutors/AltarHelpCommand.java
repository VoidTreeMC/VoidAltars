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

public class AltarHelpCommand extends SubCommand {

	public final static String NAME = "help";
	private final static ArrayList<String> helpEntries = new ArrayList<>();
	private static final int PAGE_SIZE = 10;
	private static int numPages = 0;
  private final static String ALTAR_HELP_WEBPAGE = "https://voidtreemc.com/voidtree-altar-help/";

	static {
		//Page 1
		helpEntries.add(ChatColor.GREEN + "/altar help " + ChatColor.LIGHT_PURPLE + "<page number>");
    helpEntries.add(ChatColor.GREEN + "/altar log" + ChatColor.LIGHT_PURPLE + " <page number>");
    numPages++;
	}

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

		//If the specified page number is out of range
		if ((pageNum * 10) > (helpEntries.size() + 9) || pageNum < 0) {
			player.sendMessage(String.format("The page number must be within the range %d to %d.", 1, numPages));
			return FailureCode.SUCCESS;
		}

		dispPageNumber(player, pageNum);
    player.sendMessage(ChatColor.GREEN + "For more information see: " + ChatColor.GOLD + "" + ChatColor.UNDERLINE + ALTAR_HELP_WEBPAGE);
		return FailureCode.SUCCESS;
	}

	/**
	 * Displays the selected help page number to the selected player.
	 * Assumes that the pageNum is within range.
	 * @param player The player to display the page to
	 * @param pageNum The selected page number
	 */
	private static void dispPageNumber(Player player, int pageNum) {
		player.sendMessage("");
		for (int i = (pageNum - 1) * 10; i < (pageNum * 10); i++) {
			if (i >= helpEntries.size()) {
				break;
			}
			player.sendMessage(helpEntries.get(i));
		}
		player.sendMessage(ChatColor.GREEN + "=====================");
		player.sendMessage(String.format(ChatColor.YELLOW + "Altar help page " + ChatColor.GOLD + "%d" + ChatColor.YELLOW + " out of " + ChatColor.GOLD + "%d", pageNum, numPages));
    player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GREEN + "/altar help " + ChatColor.GOLD + "#" + ChatColor.YELLOW + " to view that page.");
	}
}
