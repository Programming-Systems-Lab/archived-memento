package psl.memento.ether.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple cache provider which stores all caches in memory.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class DefaultCacheProvider extends CacheProvider
{
	private List cacheList = Collections.synchronizedList(new ArrayList());

	/**
	 * Construct a new Cache with the given size descriptor.
	 *
	 * @param sizeDesc size descriptor for the cache
	 * @return Cache with the given size
	 */
	public Cache createCache(int sizeDesc)
	{
		Cache cache = new Cache(sizeDesc);
		cacheList.add(cache);
		return cache;
	}

}
