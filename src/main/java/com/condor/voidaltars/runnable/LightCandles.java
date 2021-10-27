package com.condor.voidaltars.runnable;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Candle;

import com.condor.voidaltars.main.AltarMain;
import com.condor.voidaltars.altar.AltarMeta;

public class LightCandles extends BukkitRunnable {

	//The plugin
	JavaPlugin plugin;

  // The candle block to light
  Block block;

  // The altar's level
  int level;

	public LightCandles(Block block, int level) {
		this.plugin = AltarMain.getPlugin();
		this.block = block;
    this.level = level;
	}

	@Override
	public void run() {
    AltarMeta.setCandleLit(block, true);
    block.getLocation().getWorld().strikeLightningEffect(block.getLocation());
    if (level > 0) {
      Candle candle = (Candle) block.getBlockData();
      candle.setCandles(level);
      block.setBlockData(candle);
    }
	}
}
