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

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

public class AddSacrifices extends CommandControl {

  public AddSacrifices(String name) {
		super(name,0);
	}

	@Override
	protected FailureCode execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission("condor.altar.addsacrifices")) {
      sender.sendMessage("You do not have permission to use this command.");
      return FailureCode.PERMISSION_DENIED;
    }

    if (args.length < 2) {
      sender.sendMessage("Please provide the town name and the amount of completed sacrifices to add");
      return FailureCode.NOT_AN_ARGUMENT;
    }

    String townName = args[0];
    String newSacrificesStr = args[1];
    int newSacrifices = 0;

    try {
      newSacrifices = Integer.parseInt(newSacrificesStr);
    } catch (NumberFormatException e) {
      sender.sendMessage("ERROR: New level must be an integer number.");
      return FailureCode.FAILURE;
    }

    if (newSacrifices < 0) {
      sender.sendMessage("ERROR: New level must be greater than 0");
      return FailureCode.FAILURE;
    }


    Town town = TownyAPI.getInstance().getTown(townName);
    if (town == null) {
      sender.sendMessage("ERROR: Could not find a town by that name.");
      return FailureCode.FAILURE;
    }

    AltarMeta altar = AltarManager.getAltarFromTown(town);
    if (altar == null) {
      sender.sendMessage("ERROR: Could not find an altar belonging to that town.");
      return FailureCode.FAILURE;
    }

    // TODO: Rework method, remove for loop
    for (int i = 0; i < newSacrifices; i++) {
      altar.incrementSacrifices();
    }

    sender.sendMessage("Successfully added " + newSacrifices + " to altar.");

		return FailureCode.SUCCESS;
	}

	@Override
	protected FailureCode isNecessary(CommandSender sender, String label, String[] args) {
		return FailureCode.SUCCESS;
	}
}
