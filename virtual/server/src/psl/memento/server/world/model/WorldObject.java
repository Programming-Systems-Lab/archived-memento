package psl.memento.server.world.model;

import psl.memento.server.container.persistence.PersistentObject;
import psl.memento.server.util.Uid;

/**
 * Represents an object in the world.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class WorldObject extends PersistentObject
{
	/**
	 * Retrieve the instance ID which uniquely identifies this object in the 
	 * world.
	 * 
	 * @return unique id which uniquely identifies this object in the world
	 **/
	public Uid getInstanceId()
	{
		return super.getPersistenceId();
	}
}
