package psl.memento.ether.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Adapt a java.util.Iterator to an java.util.Enumeration.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class IteratorEnumerationAdapter implements Enumeration
{
	private Iterator iter;

	/**
	 * Construct a new adapter with the given iterator.
	 *
	 * @param iter Iterator to adapt to the Enumeration interface
	 */
	public IteratorEnumerationAdapter(Iterator iter)
	{
		if (iter == null)
		{
			String msg = "iter can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.iter = iter;
	}

   /**
	 * Determine if there are more elements left in the iterator.
	 *
	 * @return <code>true</code> if there are more elements in the enumeration
	 */
	public boolean hasMoreElements()
	{
		return iter.hasNext();
	}

	/**
	 * Get the next element in the enumeration.
	 *
	 * @return next object in the enmeration
	 */
	public Object nextElement()
	{
		return iter.next();
	}


}
