package psl.memento.server.container.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import psl.memento.server.util.Uid;

/**
 * Represent's a client session with the server.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Session
{
	private Map vals = Collections.synchronizedMap(new HashMap());
	private Uid ownerId;
	
	/**
	 * Construct a new Session for a given client.
	 * 
	 * @param ownerID entity id for the user this session belongs to
	 **/
	public Session(Uid ownerId)
	{
		if (ownerId == null)
		{
			String msg = "ownerId can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.ownerId = ownerId;
	}
	
	/**
	 * Get the entity ID of the client this session was created for.
	 * 
	 * @return entity id of the client this session belongs to
	 **/
	public Uid getOwner()
	{
		return ownerId;
	}
	
	/**
	 * Store an object in the session. It will be stored until removed or until
	 * the Session is destroyed.
	 * 
	 * @param key key to store the object under
	 * @param val value to store in the session
	 **/ 
	public void putValue(String key, Object val)
	{
		if ((key == null) || (val == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		vals.put(key, val);
	}

	/**
	 * Get an object from the session.
	 * 
	 * @param key key which the object was stored under
	 * @return Object stored under <c>key</c> or <c>null</c>
	 **/
	public Object getValue(String key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		return vals.get(key);
	}
	
	/**
	 * Remove an object from the session
	 * 
	 * @param key key which the object is stored under
	 * @return Object stored under <c>key</c> or <c>null</c>
	 **/
	public Object remove(String key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		return vals.remove(key);
	}
}
