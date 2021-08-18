package condor.command;

import org.bukkit.command.CommandSender;

import condor.command.CommandControl.FailureCode;

/**
 * Represents a subcommand - a command whose methods are invoked 
 * by a parent command rather than from the plugin itself
 * @author iron-condor.
 *
 */
public abstract class SubCommand {
	
	/**
	 * Executes the code inside, and returns a FailureCode describing
	 * the results of its execution
	 * @param sender The sender of the command
	 * @param label The first item in the command
	 * @param args A list of arguments
	 * @return A FailureCode describing the execution
	 */
	public abstract FailureCode execute(CommandSender sender, String label, String[] args);
}
