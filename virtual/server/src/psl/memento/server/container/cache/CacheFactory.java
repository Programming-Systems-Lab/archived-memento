package psl.memento.server.container.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * Object 
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class CacheFactory
{
	/** different sizes for the caches **/
	private static final int SuperSize = 100;
	private static final int Large = 50;
	private static final int Medium = 25;
	private static final int Small = 10;
	
	private List cacheList = new ArrayList();
	
	/**
	 * Construct a new Cache with Small size.
	 *
	 * @return Cache which can be used for temporary storage of objects
	 **/
	public Cache createCache()
	{
		return createCache(CacheFactory.Small);
	}
	
	/**
	 * Create a new Cache with the given size. 
	 * 
	 * @param sizeDesc one of the size descriptor values defined in the 
	 *                 CacheFactory class
	 **/
	public Cache createCache(int sizeDesc)
	{
		Cache cache = new Cache(sizeDesc);
		cacheList.add(cache);
		return cache;
	}
		

}
