package memento.world.model;

import java.util.EventListener;

/**
 * Listener that can receive events about a certain world model.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface WorldModelListener extends EventListener
{
	/**
	 * Indicates that a WorldObject has just been created inside the model.
	 *
	 * @param wme event describing the creation
	 */
    public void objectCreated(WorldModelEvent wme);

	/**
	 * Indicates that a WorldObject has just been destroyed in the model.
	 *
	 * @param wme event describing the destruction
	 */
	public void objectDestroyed(WorldModelEvent wme);

	/**
	 * Object entered the world model.
	 *
	 * @param wme event describing the entry
	 */
	public void objectEntered(WorldModelEvent wme);

	/**
	 * Object exited the world.
	 *
	 * @param wme event describing the exit
	 */
	public void objectExited(WorldModelEvent wme);

}
