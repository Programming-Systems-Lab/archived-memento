package psl.memento.server.util.concurrent;

import java.util.Iterator;

import psl.memento.server.container.persistence.PersistentObject;

/**
 * A WriteLockIterator is a special Iterator used for iterating over collections
 * that are part of a PersistentObject. When a WriteLockIterator is retrieved
 * a WriteLock is acquired on the underlying PersistentObject. This write lock
 * stays in effect until the WriteLockIterator is disposed because the 
 * iteration is finished.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public final class WriteLockIterator
{
	private PersistentObject source;
	private Iterator iter;
	
	/**
	 * Construct a new iterator which acquires a write lock for the duration
	 * of its existence.
	 * 
	 * @param source PersistentObject which the write lock will be acquired on
	 * @param iter   Iterator which will do the actual iteration and will be
	 *               adapted
	 **/
	public WriteLockIterator(PersistentObject source, Iterator iter)
	{
		if ((source == null) || (iter == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.source = source;
		this.iter = iter;
		
		// acquire the write lock -- this makes this constructor a blocking 
		// call -- is this could?
		source.getWriteLock();
	}
	
	/**
	 * Determine whether there are more objects left to iterate over in the 
	 * set.
	 * 
	 * @return whether the next call to next() can be performed safely
	 **/
	public boolean hasNext()
	{
		return iter.hasNext();
	}
	
	/**
	 * Retrieve the next object in the collection.
	 * 
	 * @return next object in the iteration
	 * @throws NoSuchElementException
	 *         if there are no more elements in the collection
	 **/
	public Object next()
	{
		return iter.next();
	}
	
	/**
	 * Remove the last element returned by the iterator from the underlying
	 * collection. 
	 *
	 * @see java.util.Iterator
	 **/
	public void remove()
	{
		iter.remove();
	}
	
	/**
	 * This method <em>must</em> be called after the iteration has completed
	 * or the client no longer needs the iterator object. Calling this method
	 * releases the write lock on the underlying persistent object.
	 **/
	public void dispose()
	{
		source.releaseWriteLock();
	}
}
