package psl.memento.ether.event.session;

import psl.memento.ether.event.ComponentUrl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a component's session history with this container.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Session
{
	private Map valMap = Collections.synchronizedMap(new HashMap());
	private ComponentUrl owner;

	/**
	 * Construct a new Session for the given component.
	 *
	 * @param owner component which this session belongs to
	 */
	public Session(ComponentUrl owner)
	{
		if (owner == null)
		{
			String msg = "owner can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.owner = owner;
	}

	/**
	 * Retrieve the URL of the component which owns this session.
	 *
	 * @return URL of the component which owns this component
	 */
	public ComponentUrl getOwner()
	{
		return owner;
	}

	/**
	 * Retrieve an object from the session.
	 *
	 * @param key key the object was stored under
	 * @return object stored under the given key or <code>null</code>
	 */
	public Object get(String key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

		return valMap.get(key);
	}

	/**
	 * Store an object in the session.
	 *
	 * @param key key to store the object was under
	 * @param ob  object to store under the given key
	 */
	public void put(String key, Object ob)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

		valMap.put(key, ob);
	}

	/**
	 * Remove an object from the session.
	 *
	 * @param key key the boject was stored under
	 */
	public void remove(String key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

		valMap.remove(key);
	}
}
