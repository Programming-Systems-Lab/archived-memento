package psl.memento.server.world.view;

import psl.memento.server.container.persistence.PersistentObject;
import psl.memento.server.world.model.WorldObject;

/**
 * Defines how a WorldObject will appear to a specific group of users within
 * the system.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class View extends PersistentObject
{
	private WorldObject wobj;
	
	/**
	 * Get the WorldObject which this view is describing.
	 * 
	 * @return WorldObject this view is describing
	 **/
	public WorldObject getModel()
	{
		return wobj;
	}
	
	/**
	 * Set the WorldObject that this view should describe.
	 * 
	 * @param wobj WorldObject this view should describe
	 **/
	public void setModel(WorldObject wobj)
	{
		this.wobj = wobj;
	}
}
