package com.condor.voidaltars.main;

import java.sql.Connection;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.condor.voidaltars.command.CommandControl;
import com.condor.voidaltars.listener.AltarListener;
import com.condor.voidaltars.altar.AltarManager;
import com.condor.voidaltars.sql.SQLLinker;
import com.condor.voidaltars.sql.SQLConfig;
import com.condor.voidaltars.constants.ConstantsLoader;
import com.condor.voidaltars.constants.SacrificesConfigLoader;

// import net.milkbowl.vault.economy.Economy;
// import net.milkbowl.vault.economy.EconomyResponse;

public class AltarMain extends JavaPlugin {

	public static final String HEIRO = "<F#SDF";

	public static final String VERSION = "v0.0.3";

	public static final String TIMEID = HEIRO + " " + VERSION;

	static {
		Bukkit.getLogger().info("AltarMain: [" + TIMEID + "]");
	}

  // public Economy econ = null;

  private AltarManager altarManager;

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

	static final String RWLOAD =
			"\n"
			+ "-----------------\n"
			+ "-----------------\n"
			+ "Plugin loading...\n"
			+ "-----------------\n"
			+ "-----------------\n";

	@Override
	public void onEnable() {
		plugin = this;
		getLogger().info(RWLOAD + TIMEID);

		Bukkit.getLogger().info("Command Control...");

		Bukkit.getLogger().info("<><><><><><><><><>");
		CommandControl.loadExecutors(this);
		Bukkit.getLogger().info("<><><><><><><><><>");

    // Bukkit.getLogger().info("Loading vault-economy hook...");
    // if (!setupEconomy()) {
    //   Bukkit.getLogger().info("Economy has failed to load.");
    // }

		//This registers the listener
		Bukkit.getLogger().info("Loading listeners...");
		try {
			AltarListener.loadListeners(this);
		}
		catch(Exception e) {

		}

    Bukkit.getLogger().info("Loading Altar Manager...");
    this.altarManager = new AltarManager();

    Bukkit.getLogger().info("Loading Sacrifice config...");
    SacrificesConfigLoader.init();

    Bukkit.getLogger().info("Loading SQL config...");
    SQLConfig.init();

    Bukkit.getLogger().info("Initializing SQL connection...");
    SQLLinker.init();

		Bukkit.getLogger().info("Calling onStart...");
		this.onStart();

    Bukkit.getLogger().info("Loading text config...");
    ConstantsLoader.init();

    Bukkit.getLogger().info("Loading has been completed!");

	}

  // private boolean setupEconomy() {
  //   if (getServer().getPluginManager().getPlugin("Vault") == null) {
  //     return false;
  //   }
  //   RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
  //   if (rsp == null) {
  //       return false;
  //   }
  //   econ = rsp.getProvider();
  //   return econ != null;
  // }
  //
  // public Economy getEconomy() {
  //   return econ;
  // }

	/**
	 * Called on server shutdown <p>
	 *
	 * 1. {@link OnPlayerJoinLeaveStartOrShutdown#shutdown()}
	 */
	@Override
	public void onDisable() {

	}

	//-------------------------------------------------------------------------------------

	/**
	 * Called on server start <p>
	 *
	 * 1. Calls {@link OnPlayerJoinLeaveStartOrShutdown#startup()}
	 */
	public void onStart() {

	}
}
