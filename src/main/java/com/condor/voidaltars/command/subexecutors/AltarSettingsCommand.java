package com.condor.voidaltars.command.subexecutors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.command.SubCommand;
import com.condor.voidaltars.gui.AltarSettingsGUI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Command that brings up the
 * altar settings GUI
 * Usage: /altar settings
 */
public class AltarSettingsCommand extends SubCommand {

	public final static String NAME = "settings";

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

    AltarSettingsGUI.displaySettingsGUI(player, link);

		return FailureCode.SUCCESS;
	}
}
