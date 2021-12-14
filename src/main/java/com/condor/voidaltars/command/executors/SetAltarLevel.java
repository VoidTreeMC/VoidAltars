package com.condor.voidaltars.command.executors;

import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.TownAltarLink;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

public class SetAltarLevel extends CommandControl {

  public SetAltarLevel(String name) {
		super(name,0);
	}

	@Override
	protected FailureCode execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission("condor.altar.setlevel")) {
      sender.sendMessage("You do not have permission to use this command.");
      return FailureCode.PERMISSION_DENIED;
    }

    if (args.length < 2) {
      sender.sendMessage("Please provide the town name and the new level for the altar");
      return FailureCode.NOT_AN_ARGUMENT;
    }

    String townName = args[0];
    String newLevelStr = args[1];
    int newLevel = 0;

    try {
      newLevel = Integer.parseInt(newLevelStr);
    } catch (NumberFormatException e) {
      sender.sendMessage("ERROR: New level must be an integer number.");
      return FailureCode.FAILURE;
    }

    if (newLevel < 0 || newLevel > 4) {
      sender.sendMessage("ERROR: New level must be between 0 and 4");
      return FailureCode.FAILURE;
    }


    Town town = TownyAPI.getInstance().getTown(townName);
    if (town == null) {
      sender.sendMessage("ERROR: Could not find a town by that name.");
      return FailureCode.FAILURE;
    }

    TownAltarLink altarLink = AltarManager.getAltarLinkFromTown(town);
    if (altarLink == null) {
      sender.sendMessage("ERROR: Could not find an altar belonging to that town.");
      return FailureCode.FAILURE;
    }

    int currLevel = altarLink.getLevel();

    if (newLevel == currLevel) {
      sender.sendMessage("The altar is already at level " + currLevel);
      return FailureCode.SUCCESS;
    }

    if (newLevel > currLevel) {
      for (int i = 0; i < (newLevel - currLevel); i++) {
        // TODO: Make a method in TownAltarLink that allows it to level up by a certain number of times, or to a certain level
        altarLink.levelUp();
      }
      sender.sendMessage("Successfully set altar to level " + newLevel);
      return FailureCode.SUCCESS;
    }

    if (newLevel < currLevel) {
      for (int i = 0; i < (currLevel - newLevel); i++) {
        // TODO: Make a method in AltarMeta that allows it to level down by a certain number of times, or to a certain level
        altarLink.levelDown();
      }
      sender.sendMessage("Successfully set altar to level " + newLevel);
      return FailureCode.SUCCESS;
    }

		return FailureCode.SUCCESS;
	}

	@Override
	protected FailureCode isNecessary(CommandSender sender, String label, String[] args) {
		return FailureCode.SUCCESS;
	}
}
