package memento.world.manager;

import java.util.EventListener;

/**
 * A Listener for retrieving events from a World Manager.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface WorldManagerListener extends EventListener
{
	/**
	 * Called when the WorldManager has begun actively managing a new
	 * world.
	 *
	 * @param wme event describing the new managing
	 */
 	public void managed(WorldManagerEvent wme);

	/**
	 * Called when a WorldManager is no longer managing a world.
	 *
	 * @param wme event describing the unmanaging
	 */
	public void unmanaged(WorldManagerEvent wme);
}
