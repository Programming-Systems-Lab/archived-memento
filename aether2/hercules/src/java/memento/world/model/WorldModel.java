package memento.world.model;

import java.util.*;
import java.beans.PropertyChangeSupport;

import aether.server.domain.Advertisement;

/**
 * Default implementation of the WorldModel interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldModel
{
	/**
	 * GUID that identifies this world model
	 */
	protected String guid;

	/**
	 * Map of all the objects in the world to their GUID.
	 */
	protected Map worldObjectMap = Collections.synchronizedMap(new HashMap());

	/**
	 * Advertisement to be generated by this world.
	 */
	protected Advertisement advertisement;

	/**
	 * EventListenerList used to manage event listeners.
	 */
	protected PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	public String getUID()
	{
		return guid;
	}

	public void setUID(String guid)
	{
		this.guid = guid;
	}

	public Advertisement getAdvertisement()
	{
		return advertisement;
	}

	public void setAdvertisement(Advertisement adv)
	{
		this.advertisement = adv;
	}

	public WorldObject retrieve(String guid)
	{
		if (guid == null)
		{
			String msg = "uid can't be null";
			throw new IllegalArgumentException(msg);
		}

        if (!worldObjectMap.containsKey(guid))
		{
			String msg = "no world object with uid exists";
			throw new NoSuchElementException(msg);
		}

		return (WorldObject) worldObjectMap.get(guid);
	}

	public void remove(WorldObject wo)
	{
        if (wo == null)
		{
			String msg = "wo can't be null";
			throw new IllegalArgumentException(msg);
		}

        if (wo.getWorldModel() != this)
		{
			String msg = "WorldModel doesn't own this object";
			throw new IllegalArgumentException(msg);
		}

        worldObjectMap.remove(wo.getUID());
        wo.setWorldModel(null);
	}
}
