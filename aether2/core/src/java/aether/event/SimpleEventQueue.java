package aether.event;

import java.util.LinkedList;

/**
 * A simple EventQueue using the LinkedList class. All Messages are simply
 * stored in memory until receieved. This class is threadsafe.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class SimpleEventQueue implements EventQueue
{
	private LinkedList list = new LinkedList();

	public synchronized void enqueue(Event msg)
	{
		if (msg == null)
		{
			String emsg = "msg can't be null";
			throw new IllegalArgumentException(emsg);
		}

		list.addLast(msg);
	}

	public synchronized Event dequeue()
	{
		if (list.isEmpty())
		{
			String msg = " queue is empty";
			throw new IllegalStateException(msg);
		}

        return (Event) list.removeFirst();
	}

    public synchronized void close()
    {
        list.clear();
    }
}
