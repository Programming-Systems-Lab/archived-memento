package aether.event;

import java.util.LinkedList;

/**
 * A BlockingEventQueue that can be used to synchronize event processing
 * between threads.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class BlockingEventQueue implements EventQueue
{
    private LinkedList list = new LinkedList();
	private boolean closed = false;

	public synchronized final void enqueue(Event msg)
	{
        if (msg == null)
		{
			String emsg = "msg can't be null";
			throw new IllegalArgumentException(emsg);
		}

		if (closed)
		{
			String emsg = "queue has been closed";
			throw new IllegalStateException(emsg);
		}

        list.addLast(msg);
        notify();
	}

	public synchronized final Event dequeue()
	{
        if (closed)
		{
			String msg = "queue has been closed";
			throw new IllegalStateException(msg);
		}

        try
		{
			// if the queue is empty, go to sleep
            while (list.isEmpty())
			{
                wait();

				// if woken up to a closed queue get the exception
                if (closed)
				{
                    String emsg = "queue has been closed";
					throw new IllegalStateException(emsg);
				}
			}

            return (Event) list.removeLast();
		}
		catch (Exception e)
		{
            // should never happen
            String msg = "internal error in " + getClass();
			throw new Error(msg);
		}
	}

    /**
	 * Dequeue a event but only wait at most a given amount of time.
	 *
	 * @param timeout maximum timeout to wait
	 * @return first Event on the queue
	 * @throws EventException
	 *         if the operation times out
	 */
    public synchronized final Event dequeue(long timeout)
			throws EventException
	{
        if (closed)
		{
			String msg = "queue has been closed";
			throw new IllegalStateException(msg);
		}

        if (timeout < 0)
		{
            timeout = Long.MAX_VALUE;
		}

        try
		{
			long expiration = System.currentTimeMillis() + timeout;
            while (list.isEmpty())
			{
                timeout = expiration - System.currentTimeMillis();

                if (timeout <= 0)
				{
					String msg = "dequeue timed out";
					throw new EventException(msg);
				}

				// wait the remaining time
                wait(timeout);

				if (closed)
				{
					String msg = "queue has been closed";
					throw new IllegalStateException(msg);
				}
			}

    		return (Event) list.removeFirst();
		}
		catch (Exception e)
		{
			// should never happen
			String msg = "internal error in " + getClass();
			throw new Error(msg);
		}
	}

    /**
	 * Close the queue. All threads blocking on the queue will be woken up
	 * and receive an IllegalStateException.
	 */
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
        notifyAll();
	}

	/**
	 * Delete all messages on the queue.
	 */
	public synchronized final void clear()
	{
        list.clear();
	}
}
