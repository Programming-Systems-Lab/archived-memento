package psl.memento.ether.cache;

/**
 * A CacheProvider object is responsible for creating and managing server side
 * caches.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class CacheProvider
{
   /** different sizes for the caches **/
   public static final int Largest = 50;
	public static final int Large = 25;
	public static final int Medium = 15;
	public static final int Small = 10;

	private static CacheProvider singleton;

   /**
	 * Construct a new Cache with the default Medium size.
	 *
	 * @return server-side, managed cache
	 */
	public Cache createCache()
	{
		return createCache(CacheProvider.Medium);
	}

	/**
	 * Get the single, container-wide cache provider.
	 *
	 * @return single, container-wide cache provider
	 */
	public static CacheProvider getInstance()
	{
		return singleton;
	}

	/**
	 * Set the single, container-wide cache provider.
	 *
	 * @param prov single, container-wide cache provider
	 */
	public static void setInstance(CacheProvider prov)
	{
		if (prov == null)
		{
			String msg = "prov can't be null";
			throw new IllegalArgumentException(msg);
		}

		singleton = prov;
	}

	/**
	 * Construct a new Cache with the given size descriptor.
	 *
	 * @param sizeDesc one of the static constants of this interface describing
	 *                 the size of the cache to construct
	 * @return Cache with the given size
	 */
	public abstract Cache createCache(int sizeDesc);
}
