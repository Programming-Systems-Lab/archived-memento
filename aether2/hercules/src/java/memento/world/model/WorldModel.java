package memento.world.model;

import aether.server.framework.Identifiable;
import aether.server.framework.Advertising;

import java.util.Map;

/**
 * Component that describes the state of the world.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface WorldModel extends Advertising
{
	/**
	 * Create an object in the world.
	 *
	 * @param c    class of the object to create
	 * @return WorldObject instance of the given class
	 */
	public WorldObject create(Class c, Map params);

	/**
	 * Retrieve an object from the world by its guid.
	 *
	 * @param guid unique id of the object to retrieve
	 * @return WorldObject of the given type
	 */
 	public WorldObject retrieve(String guid);

	/**
	 * Destroy an object in the world.
	 *
	 * @param wo Object to destroy in the world
	 */
    public void destroy(WorldObject wo);

	/**
	 * Enter an object for the first time.
	 *
	 * @param wo WorldObject to enter the world
	 */
	public void enter(WorldObject wo);

	/**
	 * An object has exited.
	 *
	 * @param wo WorldObject that's exited the world
	 */
	public void exit(WorldObject wo);

	/**
	 * Add a listener to the world model.
	 *
	 * @param wml listener to the world to add
	 */
    public void addWorldModelListener(WorldModelListener wml);

	/**
	 * Remove a listener from the world model.
	 *
	 * @param wml listener to remove from the world
	 */
	public void removeWorldModelListener(WorldModelListener wml);
}
