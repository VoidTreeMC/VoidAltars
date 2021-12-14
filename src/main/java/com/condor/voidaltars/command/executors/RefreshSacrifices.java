// package com.condor.voidaltars.command.executors;
//
// import java.util.TreeMap;
// import java.util.ArrayList;
//
// import org.bukkit.command.CommandSender;
// import org.bukkit.entity.Player;
// import org.bukkit.inventory.ItemStack;
//
// import com.condor.voidaltars.command.CommandControl.FailureCode;
// import com.condor.voidaltars.constants.ConstantsLoader;
// import com.condor.voidaltars.command.CommandControl;
// import com.condor.voidaltars.altar.AltarMeta;
// import com.condor.voidaltars.altar.AltarManager;
// import com.condor.voidaltars.altar.Sacrifice;
// import com.condor.voidaltars.altar.SacrificeManager;
//
// import com.palmergames.bukkit.towny.TownyAPI;
// import com.palmergames.bukkit.towny.object.Town;
//
// public class RefreshSacrifices extends CommandControl {
//
//   public RefreshSacrifices(String name) {
// 		super(name,0);
// 	}
//
// 	@Override
// 	protected FailureCode execute(CommandSender sender, String label, String[] args) {
//     if (!sender.hasPermission("condor.altar.refreshsacrifices")) {
//       sender.sendMessage("You do not have permission to use this command.");
//       return FailureCode.PERMISSION_DENIED;
//     }
//
//     if (args.length < 1) {
//       sender.sendMessage("Please provide the town name");
//       return FailureCode.NOT_AN_ARGUMENT;
//     }
//
//     String townName = args[0];
//
//     Town town = TownyAPI.getInstance().getTown(townName);
//     if (town == null) {
//       sender.sendMessage("ERROR: Could not find a town by that name.");
//       return FailureCode.FAILURE;
//     }
//
//     AltarMeta altar = AltarManager.getAltarFromTown(town);
//     if (altar == null) {
//       sender.sendMessage("ERROR: Could not find an altar belonging to that town.");
//       return FailureCode.FAILURE;
//     }
//
//     // Do stuff here
//     ArrayList<Sacrifice> sacrifices = altar.getSacrifices();
//     int numSacrifices = altar.getLink().getSacrificesWanted();
//     sacrifices.clear();
//     for (int i = 0; i < numSacrifices; i++) {
//       sacrifices.add(SacrificeManager.getNewSacrifice(altar));
//     }
//
//     sender.sendMessage("Sacrifices refreshed.");
//
// 		return FailureCode.SUCCESS;
// 	}
//
// 	@Override
// 	protected FailureCode isNecessary(CommandSender sender, String label, String[] args) {
// 		return FailureCode.SUCCESS;
// 	}
// }
