package psl.memento.server.util.concurrent;

import EDU.oswego.cs.dl.util.concurrent.ReentrantWriterPreferenceReadWriteLock;

/**
 * A writer-preferenced ReaderWriterLock which allows both readers and writers
 * to reacquire read or write locks. Readers are not allowed until all write
 * locks held by the writing thread have been released. For each time a lock
 * is acquired it must be released.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ReentrantReaderWriterLock implements ReaderWriterLock
{
	private ReentrantWriterPreferenceReadWriteLock lock =
		new ReentrantWriterPreferenceReadWriteLock();
		
	/**
	 * @see psl.memento.server.util.concurrent.ReaderWriterLock#getReadLock()
	 */
	public void getReadLock()
	{
		try
		{
			lock.readLock().acquire();
		}
		catch (InterruptedException ie)
		{
			// squash the exception
			ie.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * @see psl.memento.server.util.concurrent.ReaderWriterLock#getImmediateReadLock()
	 */
	public boolean getImmediateReadLock()
	{
		try
		{
			return lock.readLock().attempt(-1L);
		}
		catch (InterruptedException ie)
		{
			// squash the exception
			ie.printStackTrace();
			System.exit(-1);
			return false;
		}
	}

	/**
	 * @see psl.memento.server.util.concurrent.ReaderWriterLock#releaseReadLock()
	 */
	public void releaseReadLock()
	{
		lock.readLock().release();
	}

	/**
	 * @see psl.memento.server.util.concurrent.ReaderWriterLock#getWriteLock()
	 */
	public void getWriteLock()
	{
		try
		{
			lock.writeLock().acquire();
		}
		catch (InterruptedException ie)
		{
			// squash the exception
			ie.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * @see psl.memento.server.util.concurrent.ReaderWriterLock#getImmediateWriteLock()
	 */
	public boolean getImmediateWriteLock()
	{
		try
		{
			return lock.writeLock().attempt(-1);
		}
		catch (InterruptedException ie)
		{
			// squash the exception
			ie.printStackTrace();
			System.exit(-1);
			return false;
		}
	}

	/**
	 * @see psl.memento.server.util.concurrent.ReaderWriterLock#releaseWriteLock()
	 */
	public void releaseWriteLock()
	{
		lock.writeLock().release();
	}

}
