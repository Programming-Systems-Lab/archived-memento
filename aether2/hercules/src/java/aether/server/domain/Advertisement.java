package aether.server.domain;

import java.util.*;
import java.io.Serializable;

/**
 * Represents an Advertisement generated by a component when it joins
 * the container.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class Advertisement implements Serializable
{
	private Map valMap = Collections.synchronizedMap(new HashMap());

	/**
	 * Get a value stored in the advertisement.
	 *
	 * @param name name the value is stored in
	 * @return object that <code>name</code> is mapped to
	 */
    public Object get(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (!valMap.containsKey(name))
		{
			String msg = "no such value as " + name;
			throw new NoSuchElementException(msg);
		}

		return valMap.get(name);
	}

	/**
	 * Store a value in the advertisement.
	 *
	 * @param name name that the value should be stored under
	 * @param val  value that the advertisement should be stored under
	 */
    public void set(String name, Object val)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

        valMap.put(name, val);
	}

	/**
	 * Return an enumeration of the keys in the advertisement.
	 *
	 * @return enumeration of the keys in the advertisement
	 */
    public Enumeration names()
	{
		return Collections.enumeration(valMap.keySet());
	}

	public int hashCode()
	{
		return valMap.hashCode();
	}

	public boolean equals(Object obj)
	{
		return valMap.equals(obj);
	}

	public String toString()
	{
		return getClass().getName() + "[valMap=" + valMap.toString() + "]";
	}
}
