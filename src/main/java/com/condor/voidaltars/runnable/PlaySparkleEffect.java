package com.condor.voidaltars.runnable;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Effect;

import com.condor.voidaltars.main.AltarMain;

public class PlaySparkleEffect extends BukkitRunnable {

	//The plugin
	JavaPlugin plugin;

  // The location to put the particles at
  Location loc;

  // The number of layers of recursion deep we are
  int depth;

  private static final int TIME_TO_LAST = 10;

	public PlaySparkleEffect(Location loc, int depth) {
		this.plugin = AltarMain.getPlugin();
		this.loc = loc;
    this.depth = depth;
	}

	@Override
	public void run() {
    if (depth < TIME_TO_LAST) {
      loc.getWorld().playEffect(loc, Effect.ELECTRIC_SPARK, null);
      (new PlaySparkleEffect(loc, depth + 1)).runTaskLater(AltarMain.getPlugin(), 10);
    }
	}
}
