package psl.memento.server.util.concurrent;

import java.util.LinkedList;

/**
 * Implements a basic ReaderWriterLock so threads operating on in-memory
 * objects do not trample one another.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public final class HolubReaderWriterLock implements ReaderWriterLock
{
	private int activeReaders;
	private int waitingReaders;
	private int activeWriters;
	
	private final LinkedList writerLocks = new LinkedList();
	
	/**
	 * Request a read lock. The calling thread will block until a read 
	 * operation can be performed safely. 
	 **/
	public synchronized void getReadLock()
	{
		// if there are no active writers, increment the reader count
		if ((activeWriters == 0) && (writerLocks.size() == 0))
		{
			activeReaders += 1;
		}
		else
		{
			// increment the waiting readers count and block until
			// notifyReaders() is called
			waitingReaders += 1;
			
			try
			{
				wait();
			}
			catch (InterruptedException ie)
			{
				// squash the exception. should never happen.
				ie.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	/**
	 * Requests a read lock immediately without blocking. If the read lock is
	 * acquired, returns true else it returns false. 
	 * 
	 * @return <c>true</c> if the read lock is acquired else <c>false</c>
	 **/
	public synchronized boolean getImmediateReadLock() 
	{
		if ((activeWriters == 0) && (writerLocks.size() == 0))
		{
			activeReaders += 1;
			return true;
		}
	
		return false;
	}
	
	/**
	 * Release a read lock on this object.
	 **/
	public synchronized void releaseReadLock()
	{
		if (--activeReaders == 0)
		{
			notifyWriters();
		}
	}

	/**
	 * Acquire a write lock. Block until the write operation can be performed
	 * safely. Write requests are guaranteed to be executed in the order
	 * received. Pending read requests take precedence over pending write
	 * requests.
	 **/
	public void getWriteLock() 
	{
		Object lock = new Object();
		synchronized (lock)
		{
			synchronized(this)
			{
				boolean okayToWrite = writerLocks.size() == 0 && 
					activeReaders == 0 && activeWriters == 0;
				
				if (okayToWrite)
				{
					++activeWriters;
					return;
				}
				
				writerLocks.addLast(lock);
			}
			
			try
			{
				lock.wait();
			}
			catch (InterruptedException ie)
			{
				// squash the exception, should never happen
				ie.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	/**
	 * Requests a write lock immediately. If the write lock is acquired then
	 * true is returned else false is returned.
	 * 
	 * @return <c>true</c> if the write lock is acquired else <c>false</c>
	 **/
	public synchronized boolean getImmediateWriteLock()
	{
		if ((writerLocks.size() == 0) && (activeReaders == 0) && 
			(activeWriters == 0))
		{
			activeWriters += 1;
			return true;
		}
	
		return false;
	}
	
	/**
	 * Release the write lock.
	 **/
	public synchronized void releaseWriteLock()
	{
		activeWriters -= 1;
		if (waitingReaders > 0) // notify waiting readers
		{
			notifyReaders();
		}
		else
		{
			notifyWriters();
		}
	}

	/**
	 * Notify and wake up all the threads waiting to read.
	 **/
	private void notifyReaders()
	{
		activeReaders += waitingReaders;
		waitingReaders = 0;
		notifyAll();
	}			
	
	/**
	 * Notify and wake up the writing thread which has been waiting the
	 * longest.
	 **/
	private void notifyWriters()
	{
		if (writerLocks.size() > 0)
		{
			Object oldest = writerLocks.removeFirst();
			activeWriters += 1;
			synchronized (oldest)
			{
				oldest.notify();
			}
		}
	}
}
