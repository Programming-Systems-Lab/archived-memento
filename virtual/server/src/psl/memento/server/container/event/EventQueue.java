package psl.memento.server.container.event;

import java.util.LinkedList;

import psl.memento.server.util.concurrent.TimeoutException;

/**
 * A simple blocking queue to hold incoming event objects.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class EventQueue
{
	private LinkedList list = new LinkedList();
	private boolean closed = false;
	
	/**
	 * Place an event on the queue.
	 * 
	 * @param event event to put on the queue
	 **/
	public synchronized final void enqueue(Event event)
	{
		if (event == null)
		{
			String msg = "event can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (closed)
		{
			String msg = "queue has been closed";
			throw new IllegalStateException(msg);
		}
		
		list.addLast(event);
		
		// wake up any blocking threads
		notify();
	}
	
	/**
	 * Remove an event from the queue. If the queue is empty the calling thread
	 * will block until another thread places an event on the queue.
	 * 
	 * @return oldest event on the queue
	 * @throws IllegalStateException
	 *         if the queue is closed before another event is placed on it
	 **/
	public synchronized final Event dequeue()
	{
		if (closed)
		{
			String msg = "queue has been closed";
			throw new IllegalStateException(msg);
		}
		
		try
		{
			while (list.isEmpty())
			{
				// if the list is empty, block
				wait();
				
				// if woken up to a closed queue, throw an exception
				if (closed)
				{
					String msg = "queue has been closed";
					throw new IllegalStateException(msg);
				}
			}
			
			return (Event) list.removeFirst();
		}
		catch (Exception ex)
		{
			// should never happen
			System.out.println("EventQueue exploded");
			ex.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Remove an event from the queue. If the queue is empty the calling 
	 * thread will block until an event is placed on the queue. The calling
	 * thread will only block for a given amount of milliseconds.
	 * 
	 * @param timeout number of milliseconds to block for
	 * @return oldest Event on the queue
	 * @throws IllegalStateException
	 *         if the event is close
	 * @throws TimeoutException
	 *         if the operation times out
	 **/
	public synchronized final Event dequeue(long timeout) 
		throws TimeoutException
	{
		if (closed)
		{
			String msg = "queue has been closed";
			throw new IllegalStateException(msg);
		}
		
		// sanitize timeout
		if (timeout < 0)
		{
			timeout = Long.MAX_VALUE;
		}
		
		try
		{
			// figure out when we need to stop waiting
			long expiration = System.currentTimeMillis() + timeout;
			while (list.isEmpty())
			{
				// figure out how much time left to wait for
				timeout = expiration - System.currentTimeMillis();
				
				// if we've waited to long throw the exception
				if (timeout <= 0)
				{
					throw new TimeoutException();
				}
				
				// otherwise wait for the given timeout time
				wait(timeout);
				
				
				// if woken up to a closed queue throw the exception
				if (closed)
				{
					String msg = "queue has been closed";
					throw new IllegalStateException(msg);
				}
				
			}
		}
		catch (InterruptedException ie)
		{
			// should never happen
			System.out.println("EventQueue exploded");
			ie.printStackTrace();
			System.exit(-1);
		}
		
		// we've acquired the spin lock so the condition is satisfied and
		// we can go ahead
		return (Event) list.removeFirst();
	}
	
	/**
	 * Close the queue so it's unusable. After this method has been called all
	 * operations on the queue will throw an IllegalStateException.
	 **/
	public final void close()
	{
		if (closed)
		{
			String msg = "queue has been closed";
			throw new IllegalStateException(msg);
		}
		
		closed = true;
		list.clear();
		list = null;
		
		// wake up all threads
		notifyAll();
	}
	
	/**
	 * Delete all events currently on the queue.
	 **/
	public synchronized final void clear()
	{
		list.clear();
	}
}
