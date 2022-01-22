package com.condor.voidaltars.listener;

import java.util.Map.Entry;
import java.util.HashMap;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.condor.voidaltars.listener.listeners.EventListener;

/**
 * Used to manage multiple listeners. Currently only used for one,
 * which is EventListener
 */
public abstract class AltarListener implements Listener {

	/**
	 * Determines debug print statemetents
	 */
	public static final boolean ENABLE = true;

	/**
	 * Contains all functioning listeners, make sure to create a new instance of each listener
	 * @param String name simpleclass name
	 * @param Listener listener
	 */
	private static HashMap<String,Listener> listeners = new HashMap<>();

	public static EventListener testListener = new EventListener();


	//--------------------------------------------------------------------------

	public AltarListener() {
		// Adds listener to map with its simple name
		listeners.put(this.getClass().getSimpleName(), this);
	}

	public static void loadListeners(JavaPlugin j) {
		// Should loop through all instantiated listeners and register
		for(Entry<String, Listener> e : listeners.entrySet()) {

			// Registers listener
			j.getServer().getPluginManager().registerEvents(e.getValue(), j);
		}
	}
}
