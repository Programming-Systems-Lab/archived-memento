package psl.memento.server.container.cache;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a server-side cache.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Cache
{
	private Map cacheMap;
	
	/**
	 * Construct a new Cache optimized to hold a given number of items.
	 * 
	 * @param numItems number of items to be stored in the cache
	 **/
	Cache(int numItems)
	{
		cacheMap = Collections.synchronizedMap(new HashMap(numItems));
	}
	
	/**
	 * Retrieve an object from this cache.
	 * 
	 * @param key key which the object was stored under
	 * @return Object mapped to <c>key</c> or <c>null</c> if the object is 
	 *         no longer in the cache
	 **/
	public Object get(String key)
	{
		if (cacheMap.containsKey(key))
		{
			CacheInfo ci = (CacheInfo) cacheMap.get(key);
			
			// make sure the object hasn't expired
			if ((ci.expires != null) && 
				(ci.expires.getTime() > System.currentTimeMillis()))
			{
				remove(key);
				return null;
			}
			
			return ci.object;
		}
		else
		{
			return null;
		}
	}
				
	/**
	 * Store an object in a cache.
	 * 
	 * @param key name to store the object under
	 * @param ob  object to store in the cache
	 **/
	public void insert(String key, Object ob)
	{
		insert(key, ob, null, null);
	}
	
	/**
	 * Store an object in the cache.
	 * 
	 * @param key      key to store the object under
	 * @param ob       Object to store in the cache
	 * @param priority priority to store the object under
	 **/
	public void insert(String key, Object ob, CachePriority priority)
	{
		insert(key, ob, null, priority);
	}	
	
	/**
	 * Insert an object into the cache which is only good for a certain amount
	 * of time before it expires.
	 * 
	 * @param key     key to store the object under
	 * @param ob      object to store in the cache
	 * @param expires time when the object expires
	 **/
	public void insert(String key, Object ob, Date expires)
	{
		insert(key, ob, expires, null);
	}
	
	/**
	 * Insert an object into the cache with the appropriate properties.
	 * 
	 * @param key      key to store the object under
	 * @param ob       object to store in the cache
	 * @param expires  time in the future that the object should expire 
	 * @param priority priority to store the object under
	 **/
	public void insert(String key, Object ob, Date expires, 
		CachePriority priority)
	{
		if ((key == null) || (ob == null))
		{
			String msg = "key nor ob can be null";
			throw new IllegalArgumentException(msg);
		}
		
		CacheInfo ci = new CacheInfo(ob, expires, priority);
		cacheMap.put(key, ci);
	}
	
	/**
	 * Remove an object from the cache.
	 * 
	 * @param key key which the object is stored under
	 **/
	public void remove(String key)
	{
		if (key != null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		cacheMap.remove(key);
	}
	
	/**
	 * Remove all objects in the cache that have either expired or were stored
	 * under a priority which is at or below the given priority. This method
	 * may be called if the server begins to run low on memory and it must
	 * begin reducing the caches.
	 * 
	 * @param priority the cutoff priority 
	 **/
	void remove(CachePriority priority)
	{
		priority = priority;
		; // todo!
	}
	
	/**
	 * A helper class which contains metadata about objects stored in the
	 * cache.
	 * 
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 **/
	private class CacheInfo 
	{
		public CacheInfo(Object object, Date expires, CachePriority priority)
		{
			this.object = object;
			this.expires = expires;
			this.priority = priority;
		}
		
		public Object object;
		public Date expires;
		public CachePriority priority;
	}
}
