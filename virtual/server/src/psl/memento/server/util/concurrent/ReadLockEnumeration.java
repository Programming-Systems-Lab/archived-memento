package psl.memento.server.util.concurrent;

import java.util.Iterator;

import psl.memento.server.container.persistence.PersistentObject;

/**
 * A ReadLockEnumeration is used for enumerating over collections which are 
 * part of a PersistentObject. 
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ReadLockEnumeration
{
	private PersistentObject source;
	private Iterator iter;
	
	/**
	 * Construct a new read-locking enumeration. This enumeration will acquire
	 * a read lock on the persistent object holding the collection thereby
	 * allowing the enumeration to be completed safely.
	 * 
	 * @param source PersistentObject which holds the underlying collection as
	 *               part of its state
	 * @param iter   iterator which must be adopted
	 **/
	public ReadLockEnumeration(PersistentObject source, Iterator iter)
	{
		if ((source == null) || (iter == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.source = source;
		this.iter = iter;
		
		// acquire the read lock
		this.source.getReadLock();
	}
	
	/**
	 * Determine if the next call to nextElement can be performed safely.
	 * 
	 * @return <c>true</c> if the next call to <c>nextElement</c> can be 
	 *         performed safely else <c>false</c>
	 **/
	public boolean hasMoreElements()
	{
		return iter.hasNext();
	}
	
	/**
	 * Return the next object in the underlying collection.
	 * 
	 * @return next object in the underlying collection
	 **/
	public Object nextElement()
	{
		return iter.next();
	}
	
	/**
	 * This method must be called once the client has finished the enumeration
	 * or no longer needs the enumeration -- this method releases the read
	 * lock on the underlying persistent object.
	 **/
	public void dispose()
	{
		source.releaseReadLock();
	}
	
}
