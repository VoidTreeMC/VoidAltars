package com.condor.voidaltars.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.constants.AltarStructuresLoader;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.constants.SacrificesConfigLoader;
import com.condor.voidaltars.leaderboard.LeaderboardParser;
import com.condor.voidaltars.listener.AltarListener;
import com.condor.voidaltars.sql.SQLConfig;
import com.condor.voidaltars.sql.SQLLinker;

/**
 * The main class for the VoidAltars plugin
 */
public class AltarMain extends JavaPlugin {

	public static final String HEIRO = "<F#SDF";

	public static final String VERSION = "v0.0.4";

	public static final String TIMEID = HEIRO + " " + VERSION;

	static {
		Bukkit.getLogger().info("AltarMain: [" + TIMEID + "]");
	}

  // public Economy econ = null;

	private static AltarMain plugin;
	public static AltarMain getPlugin() {
		return plugin;
	}
	/**
	 * Debug printing for this class
	 */
	public static boolean
		bc = false,
		log = false;

	/**
	 * Our instance
	 */

	public AltarMain() {
		Bukkit.getLogger().info("Initializing");
	}

	static final String LOADING_STRING =
			"\n"
			+ "-----------------\n"
			+ "-----------------\n"
			+ "Plugin loading...\n"
			+ "-----------------\n"
			+ "-----------------\n";

	@Override
	public void onEnable() {
		plugin = this;
		getLogger().info(LOADING_STRING + TIMEID);

		Bukkit.getLogger().info("Command Control...");

		Bukkit.getLogger().info("<><><><><><><><><>");
		CommandControl.loadExecutors(this);
		Bukkit.getLogger().info("<><><><><><><><><>");

		//This registers the listener
		Bukkit.getLogger().info("Loading listeners...");
		try {
			AltarListener.loadListeners(this);
		}
		catch(Exception e) {
      e.printStackTrace();
		}

    Bukkit.getLogger().info("Loading altar_structures.yml...");
    AltarStructuresLoader.init();

    Bukkit.getLogger().info("Loading Sacrifice config...");
    SacrificesConfigLoader.init();

    Bukkit.getLogger().info("Loading SQL config...");
    SQLConfig.init();

    Bukkit.getLogger().info("Initializing SQL connection...");
    SQLLinker.init();

    Bukkit.getLogger().info("Loading altar leaderboards...");
    LeaderboardParser.init();

		Bukkit.getLogger().info("Calling onStart...");
		this.onStart();

    Bukkit.getLogger().info("Loading text config...");
    ConstantsLoader.init();

    Bukkit.getLogger().info("Loading has been completed!");

	}


	/**
   * Called on plugin disable (reloading or server shutdown)
   */
	@Override
	public void onDisable() {

	}

	/**
   * Called on plugin loading (reloading or server start)
   */
	public void onStart() {

	}
}
