package psl.memento.server.util.concurrent;

/**
 * Represents a readable and writable resource in a heavily multithreaded 
 * environment. Threads must acquire read or write locks on the resource before
 * accessing its state data or changing its states.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class ReadWriteResource
{
	private ReaderWriterLock lock;
	private long lastChangeTime = -1;
	
	/**
	 * Default constructor.
	 **/
	protected ReadWriteResource()
	{
		lock = createReaderWriterLock();
	}
	
	/**
	 * The last time a change was made to the state of this PersistentObject.
	 * Subclasses must call this method at the end of every mutator function
	 * which changes the object.
	 **/
	protected void changed()
	{
		this.lastChangeTime = System.currentTimeMillis();
	}
	
	/**
	 * Get the last time this PersistentObject was changed.
	 * 
	 * @return last time this persistent object was changed
	 **/
	long getLastChangeTime()
	{
		return lastChangeTime;
	}
	
	/**
	 * Acquire a read lock on this object. The calling thread will block until
	 * a read operation can be performed safely. Once this method returns
	 * only accessor methods can be called on this object. Once the calling
	 * thread is finished reading the lock must be released.
	 **/
	public void getReadLock()
	{
		lock.getReadLock();
	}
	
	/**
	 * Acquire a read lock on this object without blocking. If the lock is
	 * acquired then true is returned else false is returned. If this method
	 * returns true only accessor methods may be called on this object. Once
	 * the calling thread is finished reading the lock must be released.
	 **/
	public boolean getImmediateReadLock()
	{
		return lock.getImmediateReadLock();
	}
	
	/**
	 * Release a read lock on this object.
	 **/
	public void releaseReadLock()
	{
		lock.releaseReadLock();
	}
	
	/**
	 * Acquire a write lock on this object. The calling thread will block until
	 * it is safe to write (or read) to this object. Once the calling thread
	 * is done writing it must release the write lock.
	 **/
	public void getWriteLock()
	{
		lock.getWriteLock();
	}
	
	/**
	 * Acquire a write lock on this object without blocking. If the lock cannot
	 * be acquired when the method is called then false is returned; if the 
	 * lock is acquired then true is returned.
	 **/
	public boolean getImmediateWriteLock()
	{
		return lock.getImmediateWriteLock();
	}
	
	/**
	 * Release a write lock on this object.
	 **/
	public void releaseWriteLock()
	{
		lock.releaseWriteLock();
	}
	
	/**
	 * Construct the ReaderWriterLock used to control access to this persistent
	 * object in memory by multiple threads.
	 * 
	 * @return ReaderWriterLock used to control access to this persistent 
	 *         object
	 **/
	protected ReaderWriterLock createReaderWriterLock()
	{
		return new HolubReaderWriterLock();
	}
}
