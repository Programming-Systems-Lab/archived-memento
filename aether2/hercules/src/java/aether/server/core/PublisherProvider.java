package aether.server.core;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.framework.Startable;
import aether.net.Connection;
import aether.net.Publisher;
import aether.event.BlockingEventQueue;
import aether.event.Event;

import java.beans.beancontext.BeanContextServiceProvider;
import java.beans.beancontext.BeanContextServices;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Provides the publishing service to components in the container for sending
 * outgoing events. Because threads can block while publishing events and
 * multiple threads all trying to publish events over the same connection will
 * block needlessly so outgoing events need to be queued through this provider.
 *
 * TODO: test this class!
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class PublisherProvider extends DefaultComponent
		implements Initializable, Disposable, BeanContextServiceProvider,
		Startable
{
	private Connection connection;
	private PublishQueue queue = new PublishQueue();
	private EventDeliveryThread deliveryThread;

	private final static Logger log = Logger.getLogger(PublisherProvider.class);

	public void initialize() throws ComponentException
	{
        connection = (Connection)
				requireService(this, this, Connection.class, null, this);

		deliveryThread = new EventDeliveryThread();
        deliveryThread.setName(getClass() + ": Event Delivery Thread");
		deliveryThread.start();
	}

	public void dispose() throws ComponentException
	{
		deliveryThread.setRunning(false);
     	deliveryThread = null;
		queue = null;
	}

	public void start() throws ComponentException
	{
    	// register to provide the publisher service
		if (! getContainer().addService(Publisher.class, this))
		{
			String msg = "couldn't register to provide the publisher service";
			throw new ComponentException(msg);
		}
	}

	public void stop() throws ComponentException
	{
     	getContainer().releaseService(this, this, connection);

		getContainer().revokeService(Publisher.class, this, true);
	}

	public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
											   Class serviceClass)
	{
		return null;
	}

	public Object getService(BeanContextServices bcs, Object requestor,
							 Class serviceClass, Object serviceSelector)
	{
		return queue;
	}

	public void releaseService(BeanContextServices bcs, Object requestor,
							   Object service)
	{
		; // do nothing
	}

	/**
	 * A Publisher that enqueues the outgoing event to be delivered later.
	 */
    private class PublishQueue extends BlockingEventQueue implements Publisher
	{
		public void publish(Event event) throws IOException
		{
			this.enqueue(event);
		}
	}

	/**
	 * Single thread that actually publishes outgoing events.
	 */
	private class EventDeliveryThread extends Thread
	{
		private boolean running;

        public void run()
		{
        	while (running)
			{
				Event e = queue.dequeue();
				try
				{
					connection.publish(e);
				}
				catch (IOException ioe)
				{
					; // do something bad here
					log.error("Failed to publish event " + e, ioe);

					// todo: in the future, tell somebody a component has failed
				}
			}
		}

		public void start()
		{
			running = true;
			super.start();
		}

		public boolean isRunning()
		{
			return running;
		}

		public void setRunning(boolean running)
		{
			this.running = running;
		}
	}
}
