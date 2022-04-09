package com.condor.voidaltars.altar.settings;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.condor.voidaltars.altar.AltarSettings;
import com.condor.voidaltars.constants.StringConstants;
import com.condor.voidaltars.constants.StringListConstants;
import com.condor.voidaltars.altar.SettingsType;

/**
 * Setting that controls whether outsiders are allowed
 * to perform sacrifices at this town's altars
 */
public class OutsiderSacrificeSetting extends AltarSettings {

  private static final ItemStack icon = new ItemStack(Material.CAMPFIRE);
  private static final String name = StringConstants.OUTSIDER_SACRIFICES_SETTING.get();
  private static final List<String> desc = StringListConstants.OUTSIDER_SACRIFICES_SETTING_DESC.get();

  boolean state;

  /**
   * Constructor for a new OutsiderSacrificeSetting
   * @param state  True if outsiders are allowed to sacrifice, False otherwise
   */
  public OutsiderSacrificeSetting(boolean state) {
    super(SettingsType.OUTSIDER_SACRIFICES, "outsiders_sacrifice", icon, name, desc);
    this.state = state;
  }

  @Override
  public String getStateString() {
    return (this.state) ? "ON" : "OFF";
  }

  @Override
  public Object getState() {
    return state;
  }

  /**
   * Sets the state to the state provided
   * @param newState  The new state
   */
  public void setState(boolean newState) {
    this.state = newState;
  }

  /**
   * Cycles the state of the setting
   */
  public void cycle() {
    this.state = !this.state;
  }
}
