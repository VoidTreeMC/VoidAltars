package condor.main;

import java.sql.Connection;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;

import condor.command.CommandControl;
import condor.listener.AltarListener;
import condor.altar.AltarManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class AltarMain extends JavaPlugin {

	public static final String HEIRO = "<F#SDF";

	public static final String VERSION = "v0.0.2";

	public static final String TIMEID = HEIRO + " " + VERSION;

	static {
		System.out.println("AltarMain: [" + TIMEID + "]");
	}

  public Economy econ = null;

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
		System.out.println("Initializing");
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

		System.out.println("Command Control...");

		System.out.println("<><><><><><><><><>");
		CommandControl.loadExecutors(this);
		System.out.println("<><><><><><><><><>");

    System.out.println("Loading vault-economy hook...");
    if (!setupEconomy()) {
      System.out.println("Economy has failed to load.");
    }

		//This registers the listener
		System.out.println("Loading listeners...");
		try {
			AltarListener.loadListeners(this);
		}
		catch(Exception e) {

		}

    System.out.println("Loading Altar Manager...");
    this.altarManager = new AltarManager();

		System.out.println("Calling onStart...");
		this.onStart();

    System.out.println("Loading has been completed!");

	}

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
        return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }

  public Economy getEconomy() {
    return econ;
  }

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
