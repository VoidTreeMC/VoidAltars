package com.condor.voidaltars.command.executors;

import org.bukkit.command.CommandSender;
import org.bukkit.event.server.TabCompleteEvent;

import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.constants.ConstantsLoader;

/**
 * Administrator command used to reload
 * text from the text.yml config file
 */
public class CommandAltarReloadText extends CommandControl {

  public CommandAltarReloadText(String name) {
		super(name,0);
	}

	@Override
	protected FailureCode execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission("condor.altar.reload.text")) {
      sender.sendMessage("You do not have permission to use this command.");
      return FailureCode.PERMISSION_DENIED;
    }

    ConstantsLoader.init();

    sender.sendMessage("Successfully reloaded text.");

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
