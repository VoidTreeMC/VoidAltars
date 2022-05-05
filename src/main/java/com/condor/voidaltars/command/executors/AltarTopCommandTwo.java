package com.condor.voidaltars.command.executors;

import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.server.TabCompleteEvent;

import com.condor.voidaltars.command.CommandControl.FailureCode;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.command.executors.AltarCommand;
import com.condor.voidaltars.command.subexecutors.AltarTopCommand;

/**
 * Command that shows the list of altars
 * in descending rank
 * Usage: /altartop {page number}
 */
public class AltarTopCommandTwo extends CommandControl {

  public AltarTopCommandTwo(String name) {
		super(name,0);
	}

	@Override
	protected FailureCode execute(CommandSender sender, String label, String[] args) {
    String[] toPass = (args.length >= 1) ? new String[] {"top", args[0]} : args;
    AltarCommand.subcommandTree.get(AltarTopCommand.NAME).execute(sender, label, toPass);
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
