package psl.memento.server.container.cache;

/**
 * A CachePriority represents how expensive it is to construct an object 
 * stored in the cache. Objects with a high cache priority are very expensive
 * to create and should be cached as long as possible while objects with a low
 * cache priority are cheap and can be removed if the server begins to run out
 * of memory.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class CachePriority
{
	private int priority;
	
	public static final CachePriority Highest = new CachePriority(5);
	public static final CachePriority High = new CachePriority(4);
	public static final CachePriority Normal = new CachePriority(3);
	public static final CachePriority Low = new CachePriority(2);
	public static final CachePriority Lowest = new CachePriority(1);
	
	/**
	 * Private constructor to ensure illegal values aren't made.
	 **/
	private CachePriority(int priority)
	{
		this.priority = priority; // do nothing
	}
	
	/**
	 * Determine if one priority is less than or equal to the given priority.
	 * 
	 * @param priority to test against
	 **/
	boolean isLessThanOrEqual(CachePriority priority)
	{
		return this.priority <= priority.priority;
	}

}
