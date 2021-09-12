package com.condor.voidaltars.altar;

import java.util.ArrayList;

import org.bukkit.event.Event;

import org.bukkit.inventory.ItemStack;

public abstract class Boon {
  private String name;
  private ArrayList<Class> triggers;
  private BoonType type;

  protected Boon(String name, ArrayList<Class> triggers, BoonType type) {
    this.name = name;
    this.lore = lore;
    this.triggers = triggers;
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  /**
   * Determines if the boon's function should be
   * activated. If it should be, it activates it.
   * @param event  The relevant event
   */
  public void eval(Event event) {
    boolean isTriggerEvent = false;
    for (Class trigger : triggers) {
      if (event.getClass().equals(trigger) || trigger.isAssignableFrom(event.getClass())) {
        isTriggerEvent = true;
        break;
      }
    }
    boolean isNecessary = isNecessary(event);

    if (isTriggerEvent && isNecessary) {
      execute(event);
    }
  }

  /**
   * Gets the type of the boon
   * @return The type of the boon
   */
  public BoonType getType() {
    return this.type;
  }

  /**
   * Determines if the boon's execution is necessary or not
   * based on the event
   * @param  event The triggering event
   * @return       True if is necessary, false if not
   */
  public abstract boolean isNecessary(Event event);

  /**
   * Performs the boon's function
   * @param event  The relevant event
   */
  public abstract void execute(Event event);

  /**
   * Returns an item to serve as the boon's icon
   * @return An ItemStack icon for the boon
   */
  public abstract ItemStack getIcon();
}
