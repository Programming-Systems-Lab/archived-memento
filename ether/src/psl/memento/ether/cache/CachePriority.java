package psl.memento.ether.cache;

/**
 * A CachePriority represents how expensive it is to construct an object.
 * Objects which are expensive to construct (and thus have higher cache
 * priorities) will be cached longer than objects which are less expensive
 * to construct (and thus have lower cache priorities).
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
		if (priority == null)
		{
			return false;
		}
		else
		{
			return this.priority <= priority.priority;
		}
	}

}