package com.condor.voidaltars.command.executors;

import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.command.SubCommand;
import com.condor.voidaltars.command.subexecutors.AltarHelpCommand;
import com.condor.voidaltars.command.subexecutors.AltarLogCommand;
import com.condor.voidaltars.command.subexecutors.AltarSacrificesCommand;

/**
 * Supercommand used to trigger various sub-commands,
 * described in the subexecutors package.
 * Usage: /altar {subcommand}
 */
public class AltarCommand extends CommandControl {

  private static TreeMap<String, SubCommand> subcommandTree = new TreeMap<>();

  static {
    AltarLogCommand altarLog = new AltarLogCommand();
		subcommandTree.put(AltarLogCommand.NAME, altarLog);
		subcommandTree.put(AltarLogCommand.ALT_NAME, altarLog);
    subcommandTree.put(AltarHelpCommand.NAME, new AltarHelpCommand());
    AltarSacrificesCommand altarSacrificesCommand = new AltarSacrificesCommand();
    subcommandTree.put(AltarSacrificesCommand.NAME, altarSacrificesCommand);
    subcommandTree.put(AltarSacrificesCommand.ALT_NAME, altarSacrificesCommand);
  }

  public AltarCommand(String name) {
		super(name,0);
	}

	@Override
	protected FailureCode execute(CommandSender sender, String label, String[] args) {
    //If there are no arguments
		if (args.length <= 0) {
			subcommandTree.get(AltarHelpCommand.NAME).execute(sender, label, args);
			return FailureCode.NOT_AN_ARGUMENT;
		}

		//If the next entry is a valid subcommand, invoke that subcommand
		//and let it manage things
		if (subcommandTree.containsKey(args[0].toLowerCase())) {
			subcommandTree.get(args[0].toLowerCase()).execute(sender, label, args);
		} else {
			sender.sendMessage("That is an unrecognized altar command.");
			sender.sendMessage("Please see /altar help for a list of options");
		}

		return FailureCode.SUCCESS;
	}

	@Override
	protected FailureCode isNecessary(CommandSender sender, String label, String[] args) {
		return FailureCode.SUCCESS;
	}

}
