package psl.memento.pervasive.recommendation.util;

import java.util.*;

/**
 * This class allows for the easy fabrication of Iterators that do not support the remove() method. 
 * This class is used to protect Iterators that are sometimes returned and to make them effectively read-only.
 */
public class NoRemoveIterator implements Iterator {

	private Iterator _i;

	/**
	 * Creation from a normal Iterator
	 * @param i
	 */
	public NoRemoveIterator(Iterator i) {
		_i = i;
	}

	/**
	 * Same as the passed Iterator's hasNext()
	 */
	public boolean hasNext() {
		return _i.hasNext();
	}

	/**
	 * Same as the passed Iterator's next()
	 */
	public Object next() {
		return _i.next();
	}

	/**
	 * No support for this operation
	 * @throws UnsupportedOperationException whenever this method is invoked
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
