package psl.memento.ether.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A map for associating integer counters with different keys. This class is
 * thread-safe.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class CounterMap
{
	private Map counterMap = Collections.synchronizedMap(new HashMap());

	/**
	 * Increment the counter associated with a given key.
	 *
	 * @param key key to increment the counter with
	 */
	public void increment(Object key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (counterMap.containsKey(key))
		{
			int[] count = (int[]) counterMap.get(key);
			count[0] += 1;
			counterMap.put(key, count);
		}
		else
		{
			int[] count = new int[0];
			count[0] = 1;
			counterMap.put(key, count);
		}
	}

	/**
	 * Decrement the count associated with a given key.
	 *
	 * @param key key to decrement the count for
	 */
	public void decrement(Object key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (counterMap.containsKey(key))
		{
			int[] count = (int[]) counterMap.get(key);
			count[0] -= 1;

			if (count[0] < 1)
			{
				counterMap.remove(key);
			}
			else
			{
				counterMap.put(key, count);
			}
		}
	}

	/**
	 * Get the count associated with a key.
	 *
	 * @param key key to get the associated count for
	 */
	public int getCount(Object key)
	{
		if (key == null)
		{
			String msg = "key can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (counterMap.containsKey(key))
		{
			return ((int[]) counterMap.get(key))[0];
		}
		else
		{
			return 0;
		}
	}
}
