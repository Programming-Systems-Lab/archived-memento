package psl.memento.ether.cache;

import java.util.*;

/**
 * Represents a server-provided cache.
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
	 * @param numItems number of items to hold in the cache
	 */
	public Cache(int numItems)
	{
      cacheMap = Collections.synchronizedMap(new HashMap());
	}

   /**
	 * Retrieve an object from the cache.
	 *
	 * @param key key which the object is stored under
	 * @return Object mapped to <code>key</code> or <code>null</code>
	 */
	public Object get(String key)
	{
      if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

      if (cacheMap.containsKey(key))
		{
         CacheInfo ci = (CacheInfo) cacheMap.get(key);

         // make sure it hasn't expired yet
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
	 * Insert an object into the cache.
	 *
	 * @param key      key which the object is stored under
	 * @param ob       object to store in the cache
	 * @param expires  time in the future that the object should expire from the
	 *                 cache
	 * @param priority priority to describe how expensive the object is
	 */
	public void insert(String key, Object ob, Date expires,
							 CachePriority priority)
	{
      if ((key == null) || (ob == null))
		{
			String msg = "key or ob can't be null";
			throw new IllegalArgumentException(msg);
		}

      CacheInfo ci = new CacheInfo(ob, expires, priority);
      cacheMap.put(key, ci);
	}

   /**
	 * Insert an object into the cache
	 *
	 * @param key key which the object is stored under
	 * @param ob  object to store in the cache
	 */
	public void insert(String key, Object ob)
	{
      insert(key, ob, null, null);
	}

   /**
	 * Store an object in the cache with the given priority.
	 *
	 * @param key      key to store the object under
	 * @param ob       object to store in the cache
	 * @param priority priority describing object's expense
	 */
   public void insert(String key, Object ob, CachePriority priority)
	{
      insert(key, ob, null, priority);
	}

   /**
	 * Insert an object in the cache which expires at a given time in the future.
	 *
	 * @param key     key to store the object under
	 * @param ob      object to store in the cache
	 * @param expires time in the future the object should expire
	 */
   public void insert(String key, Object ob, Date expires)
	{
      insert(key, ob, expires, null);
	}

   /**
	 * Remove an object from the cache.
	 *
	 * @param key key the object is stored under
	 */
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
	 * Remove all objects in the cache which have expired or whose priority
	 * is less than the given priority.
	 *
	 * @param priority the cutoff priority
	 */
	synchronized void remove(CachePriority priority)
	{
      Iterator keyIter = cacheMap.keySet().iterator();
		while (keyIter.hasNext())
		{
			Object key = keyIter.next();
			CacheInfo ci = (CacheInfo) cacheMap.get(key);

			// if it's expired remove the object
			if ((ci.expires != null) &&
				 (ci.expires.getTime() > System.currentTimeMillis()))
			{
				cacheMap.remove(key);
				continue;
			}

			// if it's priority is less than the given one remove it
			if ((priority != null) && (priority.isLessThanOrEqual(ci.priority)))
			{
				cacheMap.remove(key);
				continue;
			}
		}
	}

   /**
	 * Basic helper class used to track objects in the cache.
	 *
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 */
	private class CacheInfo
	{
		public Object object;
		public Date expires;
		public CachePriority priority;

		public CacheInfo(Object object, Date expires, CachePriority priority)
		{
			this.object = object;
			this.expires = expires;
			this.priority = priority;
		}
	}
}
