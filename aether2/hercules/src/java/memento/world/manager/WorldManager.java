package memento.world.manager;

import memento.world.model.WorldModel;

/**
 * The WorldManager component is responsible for managing all the world models
 * that have joined a container. Each time a WorldModel joins a container the
 * WorldManager is responsible for processing its advertisement so that it is
 * properly managed.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface WorldManager
{
	/**
	 * Register a world to be managed by this manager.
	 *
	 * @param model model to be managed by this manager
	 * @return <code>true</code> iff the model is succeffully managed
	 */
	public boolean manage(WorldModel model);

	/**
	 * Unregister a world to be managed by this manager.
	 *
	 * @param model model to stop managing
	 * @return <code>true</code> iff the model is successfully unregistered
	 */
	public boolean unmanage(WorldModel model);

	/**
	 * Add a listener to this manager so its activities may be monitored.
	 *
	 * @param wml listener to add
	 */
    public void addWorldManagerListener(WorldManagerListener wml);

	/**
	 * Remove a listener from the manager.
	 *
	 * @param wml listener to remove from the manager
	 */
	public void removeWorldManagerListener(WorldManagerListener wml);
}
