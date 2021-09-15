// package com.condor.voidaltars.altar.boons;
//
// import java.util.ArrayList;
// import java.util.Random;
// import java.util.logging.Level;
//
// import org.bukkit.inventory.ItemStack;
// import org.bukkit.inventory.meta.ItemMeta;
// import org.bukkit.Material;
// import org.bukkit.event.Event;
// import org.bukkit.event.block.BlockBreakEvent;
// import org.bukkit.event.player.PlayerHarvestBlockEvent;
// import org.bukkit.Location;
// import org.bukkit.Bukkit;
//
// import com.palmergames.bukkit.towny.object.TownBlock;
// import com.palmergames.bukkit.towny.object.Town;
// import com.palmergames.bukkit.towny.TownyAPI;
// import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
//
// import com.condor.voidaltars.altar.Boon;
// import com.condor.voidaltars.altar.BoonType;
//
// public class HarvestBoon extends Boon {
//
//   private static final String NAME = "Blessing of the Harvest";
//   private static ArrayList<String> loreList = new ArrayList<>();
//   private static ArrayList<Class> triggerList = new ArrayList<>();
//
//   private static Random rng = new Random();
//
//   static {
//     loreList.add("Additional crops from farming");
//
//     triggerList.add(PlayerHarvestBlockEvent.class);
//   }
//
//   public HarvestBoon() {
//     super(NAME, triggerList, BoonType.HARVEST_BOON);
//   }
//
//   public ItemStack getIcon() {
//     ItemStack is = new ItemStack(Material.BEACON, 1);
//     ItemMeta meta = is.getItemMeta();
//     meta.setDisplayName(NAME);
//     meta.setLore(loreList);
//     is.setItemMeta(meta);
//     return is;
//   }
//
//   public boolean isNecessary(Event event) {
//     boolean ret = false;
//
//     if (event instanceof PlayerHarvestBlockEvent) {
//       PlayerHarvestBlockEvent phbe = (PlayerHarvestBlockEvent) event;
//       Location loc = phbe.getHarvestedBlock().getLocation();
//       TownBlock tb = TownyAPI.getInstance().getTownBlock(loc);
//       if (tb != null) {
//         try {
//           Town town = tb.getTown();
//           if (this.registeredTowns.contains(town)) {
//             ret = true;
//           }
//         } catch (NotRegisteredException e) {
//           ret = false;
//         }
//       } else {
//         ret = false;
//       }
//     } else if (event instanceof BlockBreakEvent) {
//       BlockBreakEvent bbe = (BlockBreakEvent) event;
//       BlockData bd = event.getBlock().getBlockData();
//       if (bd instanceof Ageable) {
//         Ageable ageable = (Ageable) bd;
//         if (ageable.getAge() == ageable.getMaximumAge()) {
//           ret = true;
//         }
//       }
//     }
//
//     return ret;
//   }
//
//   public void execute(Event event) {
//     if (event instanceof PlayerHarvestBlockEvent) {
//       PlayerHarvestBlockEvent phbe = (PlayerHarvestBlockEvent) event;
//       for (ItemStack is : phbe.getItemsHarvested()) {
//         // 20% chance to drop extra crops
//         if (rng.nextInt(5) == 0) {
//           is.setAmount(is.getAmount() + 1);
//         }
//       }
//     } else if (event instanceof BlockBreakEvent) {
//       BlockBreakEvent bbe = (BlockBreakEvent) event;
//
//     }
//   }
// }
