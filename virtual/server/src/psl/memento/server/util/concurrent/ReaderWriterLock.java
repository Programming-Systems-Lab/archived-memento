package psl.memento.server.util.concurrent;

/**
 * Defines a reader/writer lock which controls access to a shared resource.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface ReaderWriterLock
{
	/**
	 * Request a read lock. The calling thread will block until a read 
	 * operation can be performed safely. The lock must be released when the
	 * calling thread is finished reading.
	 **/
	public void getReadLock();
	
	/**
	 * Request a read lock without blocking. If the read lock is acquired,
	 * return true else return false.
	 * 
	 * @return <c>true</c> if the read lock is acquired else <c>false</c>
	 **/
	public boolean getImmediateReadLock();
	
	/**
	 * Release a read lock on this object.
	 **/
	public void releaseReadLock();
	
	
	/**
	 * Request a write lock. Calling thread will block until a write can be
	 * performed safely. Write lock must be released when the calling thread
	 * is finished writing.
	 **/
	public void getWriteLock();
	
	/**
	 * Request a write lock immediately without blocking.
	 * 
	 * @return <c>true</c> if the write lock is acquired else <c>false</c>
	 **/
	public boolean getImmediateWriteLock();
	
	/**
	 * Release the write lock.
	 **/
	public void releaseWriteLock();

}
