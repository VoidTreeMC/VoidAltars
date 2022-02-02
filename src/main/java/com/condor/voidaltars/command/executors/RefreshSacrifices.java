package com.condor.voidaltars.command.executors;

import java.util.TreeMap;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.server.TabCompleteEvent;

import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.altar.AltarMeta;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.altar.Sacrifice;
import com.condor.voidaltars.altar.SacrificeManager;
import com.condor.voidaltars.altar.AltarType;
import com.condor.voidaltars.altar.TownAltarLink;
import com.condor.voidaltars.sql.SQLLinker;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Administrator command used to re-generate
 * the sacrifices at a specific altar in a specific town
 * Usage: /refreshsacrifices {town name} {altar type}
 */
public class RefreshSacrifices extends CommandControl {

  public RefreshSacrifices(String name) {
		super(name,0);
	}

	@Override
	protected FailureCode execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission("condor.altar.refreshsacrifices")) {
      sender.sendMessage("You do not have permission to use this command.");
      return FailureCode.PERMISSION_DENIED;
    }

    if (args.length < 2) {
      sender.sendMessage("Please provide the town name and the altar type");
      return FailureCode.NOT_AN_ARGUMENT;
    }

    String townName = args[0];

    Town town = TownyAPI.getInstance().getTown(townName);
    if (town == null) {
      sender.sendMessage("ERROR: Could not find a town by that name.");
      return FailureCode.FAILURE;
    }

    String altarTypeStr = args[1].toUpperCase();
    AltarType altarType = AltarType.getTypeFromString(altarTypeStr);

    TownAltarLink link = AltarManager.getAltarLinkFromTown(town);
    if (link == null) {
      sender.sendMessage("That town does not have any altars.");
      return FailureCode.SUCCESS;
    }

    AltarMeta altar = link.getAltar(altarType);
    if (altar == null) {
      sender.sendMessage("That town does not have an altar of that type.");
      return FailureCode.SUCCESS;
    }

    // Do stuff here
    altar.clearSacrifices();
    int numSacrifices = altar.getNumSacrificeSlots();
    for (int i = 0; i < numSacrifices; i++) {
      altar.addNewSacrifice();
    }

    SQLLinker.pushToDB(altar);

    sender.sendMessage("Sacrifices refreshed.");

		return FailureCode.SUCCESS;
	}

	@Override
	protected FailureCode isNecessary(CommandSender sender, String label, String[] args) {
		return FailureCode.SUCCESS;
	}

  protected void parseTabComplete(TabCompleteEvent event, String restOfString) {
    return;
  }
}
