package com.condor.voidaltars.altar;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.palmergames.bukkit.towny.object.Town;

/**
 * Represents a boon -- a persistent benefit chosen
 * by a mayor or high priest that affects their entire town
 */
public abstract class Boon {
  private String name;
  private String description;
  private ArrayList<Class> triggers;
  private BoonType type;
  protected ArrayList<Town> registeredTowns;

  /**
   * Constructor for a boon
   * @param name         The name of the boon (used in GUI)
   * @param description  The description of the boon (used in right-click)
   * @param triggers     The events that trigger the boon's effects
   * @param type         The type of boon
   */
  protected Boon(String name, String description, ArrayList<Class> triggers, BoonType type) {
    this.name = name;
    // this.lore = lore;
    this.description = description;
    this.triggers = triggers;
    this.type = type;
    this.registeredTowns = new ArrayList<>();
  }

  /**
   * Gets the name of the boon
   * @return The name of the boon
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the description of the boon
   * @return The description of the boon
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Returns true if the town has this boon active
   * @param  town The town to be evaluated
   * @return      True if the town has this boon active, false otherwise
   */
  public boolean isRegistered(Town town) {
    return registeredTowns.contains(town);
  }

  /**
   * Adds a new town to the boon's list of
   * registered towns, making it active.
   * @param town  The town that has taken the boon
   */
  public void addTown(Town town) {
    registeredTowns.add(town);
  }

  /**
   * Removes a town from the boon's list of
   * registered towns, making it inactive.
   * @param town  The town that has lost the boon
   */
  public void removeTown(Town town) {
    registeredTowns.remove(town);
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
