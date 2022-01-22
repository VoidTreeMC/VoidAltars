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
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.Sacrifice;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

/**
 * Command that shows a list of altar transactions
 * in a paginated display
 * Usage: /altar sacrifices
 */
public class AltarSacrificesCommand extends SubCommand {

	public final static String NAME = "listsacrifices";
  public final static String ALT_NAME = "sacrifices";

	@Override
	public FailureCode execute(CommandSender sender, String label, String[] args) {

		Player player = (Player) sender;
    String altarTypeStr = "";
    AltarType type = null;

    //If they specified an altar type
		if (args.length > 1) {
      altarTypeStr = args[1].toUpperCase();
      type = AltarType.getTypeFromString(altarTypeStr);
      if (type == null) {
        player.sendMessage("You have specified an invalid altar type. Try one of the following: farm_altar, mining_altar, nether_altar, ocean_altar");
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

    player.sendMessage(ChatColor.GREEN + "========" + town.getName() + " Altar Sacrifices========");
    if (type == null) {
      for (AltarMeta altar : link.getAltars()) {
        printSacrificeInfo(altar, player);
      }
    } else {
      printSacrificeInfo(link.getAltar(type), player);
    }

		return FailureCode.SUCCESS;
	}

  /**
   * Prints information about the altar's
   * sacrifices to the player
   * @param altar   The altar whose information is to be printed
   * @param player  The player to whom the information is to be shown
   */
  private static void printSacrificeInfo(AltarMeta altar, Player player) {
    String altarName = altar.getType().getName();
    player.sendMessage(ChatColor.AQUA + altarName);
    String underlineString = ChatColor.AQUA + "";
    for (int i = 0; i < altarName.length(); i++) {
      underlineString += "-";
    }
    player.sendMessage(ChatColor.AQUA + underlineString);

    for (Sacrifice sac : altar.getSacrifices()) {
      String sacString = ChatColor.YELLOW + "" + sac.getType() + ChatColor.GOLD + " ";
      sacString += sac.getNumSacrificed();
      sacString += ChatColor.YELLOW + " / " + ChatColor.GOLD;
      sacString += sac.getNumRemaining();
      player.sendMessage(sacString);
    }
    player.sendMessage("");
  }
}
