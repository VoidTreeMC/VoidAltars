package com.condor.voidaltars.command;

import java.util.Map.Entry;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.Bukkit;

import com.condor.voidaltars.command.executors.CommandAltarReloadText;
import com.condor.voidaltars.command.executors.SetAltarLevel;
import com.condor.voidaltars.command.executors.RefreshSacrifices;
import com.condor.voidaltars.command.executors.AddSacrifices;
import com.condor.voidaltars.command.executors.AltarCommand;
import com.condor.voidaltars.command.executors.AltarTopCommandTwo;

/**
 * Utility class used for registering commands with
 * their appropriate executors
 */
public abstract class CommandControl implements CommandExecutor {

	public enum FailureCode {
		UNINIT,
		SUCCESS,
		FAILURE,
		LENGTH_MISMATCH,
		TYPE_MISMATCH,
		EVALUATION,
		DNE,
		ENTITY_DNE,
		ENTITY_MISMATCH,
		NOT_A_PLAYER,
    NOT_IN_A_TOWN,
		TEST,
		NOT_AN_ARGUMENT,
    PLAYER_OFFLINE,
    PERMISSION_DENIED,
    STILL_PROCESSING;
	}



	private static HashMap<String,CommandControl> executors = new HashMap<>();

  /**
   * Initializes all executors.
   * Add new executors here.
   */
	public static void initExecutors() {
    new CommandAltarReloadText("altarreloadtext");
    new SetAltarLevel("setaltarlevel");
    new RefreshSacrifices("refreshsacrifices");
    new AddSacrifices("addsacrifices");
    new AltarCommand("altar");
    new AltarTopCommandTwo("altartop");
	}

	/**
	 * Loops through all commands to load them with given JavaPlugin
	 * @param j javaplugin
	 */
	public static final void loadExecutors(JavaPlugin j) {

		Bukkit.getLogger().info("loadExecutors -> initExecutors");

		initExecutors();
		Bukkit.getLogger().info("initExecutors completed.");

		//loadall
		for(Entry<String,CommandControl> c : executors.entrySet()) {
			//IF A COMMAND IS NULL HERE - ITS PROBABLY NOT IN THE PLUGIN.YML DOOFUS

			j.getCommand(c.getKey()).setExecutor(c.getValue());
		}
	}

	private String name;

	private final int ARGS_LENGTH;

  public static void parseOrigTabComplete(TabCompleteEvent event) {
    String commandStr = event.getBuffer();
    String[] words = commandStr.split(" ");
    boolean isExecutor = (words.length <= 0) ? false : executors.containsKey(words[0].substring(1));
    if (!isExecutor) {
      return;
    }
    CommandControl sub = executors.get(words[0].substring(1));
    if (sub != null) {
      String restOfCommand = "";
      for (int i = 0; i < words.length; i++) {
        if (i > 0) {
          restOfCommand += words[i];
        }
      }
      sub.parseTabComplete(event, restOfCommand);
    }
  }

  protected abstract void parseTabComplete(TabCompleteEvent event, String restOfString);

	/**
	 * Constructor for All new commands.  <br>
	 * Any commands created this way must be declared in the initExecutors section
	 * @param name String of the command name that will be used
	 * @param argsLength int of required number of arguments, 0 if none required
	 */
	public CommandControl(String name, int argsLength) {
		// Always uppercase
		this.name = name.toUpperCase();
		this.ARGS_LENGTH = argsLength;
		executors.put(name, this);
	}

	/**
   * Gets the executor's name
   * @return The name of the executor
   */
	public String name() {
		return this.name;
	}

  /**
   * Processes a command registered by this plugin
   * @param  sender               The command sender
   * @param  cmd                  The command
   * @param  label                The command's label
   * @param  args                 The arguments provided to the command
   * @return                      True if the command succeeded, false otherwise
   */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		FailureCode ret = null;

		FailureCode necc = null;
		if(this.ARGS_LENGTH == 0) {
			necc = FailureCode.SUCCESS;
		}
		else if(args == null || args.length < this.ARGS_LENGTH) {
			necc = FailureCode.LENGTH_MISMATCH;
		}
		else {
			necc = this.isNecessary(sender, label, args);
		}

		if(necc==FailureCode.SUCCESS) {
			ret = this.execute(sender, label, args);
		}

		return ret == FailureCode.SUCCESS;
	}

	/**
   * Executes the command with the given arguments
   * @param  sender               The command sender
   * @param  label                The command's label
   * @param  args                 The arguments provided to the command
   * @return                      The failure/success type
   */
	protected abstract FailureCode execute(CommandSender sender, String label, String[] args);


	/**
	 * Used to determine if action is necessary <p>
	 * Called in evaluate before execute
	 * @param sender
	 * @param label
	 * @param args
	 * @return failure level, 0 means success
	 */
	/**
   * Used to determine if actions are necessary.
   * Called before execution.
   * @param  sender               The command sender
   * @param  label                The command's label
   * @param  args                 The arguments provided to the command
   * @return                      The failure/success type
   */
	protected abstract FailureCode isNecessary(CommandSender sender, String label, String[] args);

}
