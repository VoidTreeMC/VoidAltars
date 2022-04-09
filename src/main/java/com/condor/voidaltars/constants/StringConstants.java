package com.condor.voidaltars.constants;

import org.bukkit.ChatColor;

/**
 * Contains a list of single-line string constants
 * used internally by much of the GUI code and
 * user-interaction code
 */
public enum StringConstants {
  // Boons
  END_BLESSING_NAME("Blessing of the End"),
  END_BLESSING_DESCRIPTION("The gods protect everyone in your town from the void. If you fall into the void, you will wake up in bed."),
  NETHER_BLESSING_NAME("Blessing of the Nether"),
  NETHER_BLESSING_DESCRIPTION("The gods protect everyone in your town from the flame. No players or mobs in your town will take damage from fire, lava or hot floors."),
  RANCHER_BLESSING_NAME("Blessing of the Rancher"),
  RANCHER_BLESSING_DESCRIPTION("The gods bless your herds. Whenever a livestock mob dies, you get extra drops."),
  SENTINEL_BLESSING_NAME("Blessing of the Sentinel"),
  SENTINEL_BLESSING_DESCRIPTION("The gods protect your small livestock. Foxes and cats are friendly to chickens and rabbits in your town and do not eat them."),
  FORGE_BLESSING_NAME("Blessing of the Forge"),
  FORGE_BLESSING_DESCRIPTION("The gods bestow power on your forge. Your items smelt faster and your fuel lasts longer."),
  BEEKEEPER_BLESSING_NAME("Blessing of the Beekeeper"),
  BEEKEEPER_BLESSING_DESCRIPTION("The gods bless your hives. Inside your town bees are never hostile. You have a chance for two bees when you breed them and a chance of extra bee products when you harvest by hand."),
  FREEZE_BLESSING_NAME("Blessing of the Freeze"),
  FREEZE_BLESSING_DESCRIPTION("The gods bless your town with eternal winter. Ice, snow, and other cold blocks will not melt while this boon is active."),
  THAW_BLESSING_NAME("Blessing of the Thaw"),
  THAW_BLESSING_DESCRIPTION("The gods bless your town with spring on the coldest days. Snow will not collect on the ground and ice will not freeze while this boon is active."),
  PEACE_BLESSING_NAME("Blessing of the Peace"),
  PEACE_BLESSING_DESCRIPTION("The gods bless your town with an aura of peace. Inside your town normally hostile mobs will not attack players. If you attack a mob, however, it will retaliate."),
  PEST_CONTROL_BLESSING_NAME("Blessing of Pest Control"),
  PEST_CONTROL_BLESSING_DESCRIPTION("Iron golems no longer spawn in your town. Neither do cats."),
  SLIME_BLESSING_NAME("Blessing of the Slime"),
  SLIME_BLESSING_DESCRIPTION("The gods bless your feet with a bouncy substance. Inside your town you will bounce when you would otherwise take fall damage."),
  // User-visible error messages
  ALTAR_NO_LONGER_VALID("This is no longer a valid altar. The gods are not pleased."),
  MUST_BE_IN_TOWN_TO_CREATE_ALTAR_THERE("You can only create altars in a town that you own."),
  NO_PERMISSIONS_TO_CREATE_ALTAR("You must be a mayor or a high-priest of this town to create an altar here."),
  NO_PERMISSIONS_TO_BREAK_ALTAR("You must be a mayor or a high-priest of this town to move/destroy an altar in this town."),
  NO_PERMISSIONS_TO_CHANGE_BOONS("You must be a mayor or a high-priest to change your town's altar boons."),
  NO_PERMISSIONS_TO_CHANGE_SETTINGS("You must be a mayor or a high-priest to change your town's altar settings."),
  NO_DUPLICATE_ALTARS("Your town already has an altar of that type. You cannot create another one of the same type."),
  NO_ALTARS_IN_THE_WILD("You must be in a town for this altar to function."),
  NOT_MEMBER_OF_ALTARS_TOWN("You cannot change the boons of another town's altar."),
  TOWN_HAS_UNCLAIMED_ALTAR("Your town has just unclaimed its altar! Claim it back to get the boons again."),
  ALTAR_HAS_BEEN_MOVED("Your town's altar has been successfully moved!"),
  // Altar names
  FARM_ALTAR_NAME("Agriculture Altar"),
  MINING_ALTAR_NAME("Mining Altar"),
  OCEAN_ALTAR_NAME("Ocean Altar"),
  NETHER_ALTAR_NAME("Nether Altar"),
  // URLs
  ALTAR_HELP_WEBPAGE("" + ChatColor.GOLD + ChatColor.UNDERLINE + "https://www.voidtreemc.com/voidtree-altar-help"),
  // GUI strings
  SACRIFICES_REMAINING_TO_PLEASE("Sacrifices remaining to please the gods: "),
  SACRIFICES_REMAINING_TO_LEVEL("Sacrifices remaining to level up altar: "),
  GODS_ARE_PLEASED("The gods are pleased."),
  ALTAR_MAX_LEVEL("Your altar is at max level."),
  NO_DUPLICATE_BOONS("The gods already bless the town with this boon. It may not be selected twice."),
  NO_MORE_SACRIFICES(ChatColor.YELLOW + "The gods are more than pleased with your town. They will not accept any more sacrifices until the next time period."),
  RANDOM_REWARD("" + ChatColor.YELLOW + "Congratulations! The gods have blessed you with"),
  ALTAR_HELP_URL("https://www.voidtreemc.com/voidtree-altar-help/"),
  SERVER_NAME("" + ChatColor.GRAY + "Void" + ChatColor.RED + "Tree" + ChatColor.RESET),
  OUTSIDER_SACRIFICES_SETTING("Outsider Sacrifices"),
  SACRIFICE_PERMS_DENIED("This town does not allow outsiders to sacrifice at its altars.");

  String str;

  /**
   * Constructor for a StringConstants enum
   * @param str  The string
   */
  StringConstants(String str) {
    this.str = str;
  }

  public String get() {
    return this.str;
  }

  public void set(String newStr) {
    this.str = newStr;
  }
}
