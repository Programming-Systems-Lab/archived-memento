package aether.server.responder;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import aether.event.Message;
import aether.net.Connection;
import org.apache.log4j.Logger;
import org.elvin.je4.Consumer;
import org.elvin.je4.Subscription;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.*;
import java.beans.beancontext.BeanContextServiceProvider;
import java.beans.beancontext.BeanContextServices;

import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.ServiceDependencyException;
import net.concedere.dundee.AbstractProvider;

/**
 * Default implementation of the SwitchBoard interface.
 * <p />
 * By default, when this component is added to a Container it will register
 * itself to provide the 'SwitchBoard' service. If you don't want it to do
 * this you must set the 'serviceProvider' property to false.
 *
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultSwitchBoard extends DefaultComponent
		implements SwitchBoard, Disposable, Initializable
{
	/**
	 * Connection use to route incoming events.
	 */
	protected Connection connection;

	/**
	 * EventListenerList used to store SwitchBoard listeners.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Mapping between Responders and Subscriptions.
	 */
	protected Map subMap = Collections.synchronizedMap(new HashMap());

	/**
	 * Executor used to process the RequestExecutors.
	 */
	protected PooledExecutor threadPool;

	/**
	 * Number of request processing threads that should be used to process
	 * incoming requests.
	 */
	protected int numRequestWorkers = 5;

	/**
	 * Determine whether this component is a service provider.
	 */
	protected boolean serviceProvider = true;

	private BeanContextServiceProvider spi = null;

	/**
	 * Mapping between destinations and Responders.
	 */
	protected Map destMap = Collections.synchronizedMap(new HashMap());

	private Consumer consumer;

	private static final Logger log =
			Logger.getLogger(DefaultSwitchBoard.class);

	/**
	 * Construct a new DefaultSwitchBoard.
	 */
	public DefaultSwitchBoard()
	{
        ; // do nothing
	}

	/**
	 * Get the number of threads used to process incoming requests. By default,
	 * this is 5.
	 *
	 * @return number of threads used to process incoming requests
	 */
    public int getRequestWorkerThreads()
	{
		return numRequestWorkers;
	}

	/**
	 * Set the number of threads used to process incoming requests.
	 *
	 * @param num number of threads used to process incoming requests
	 */
	public void setRequestWorkerThreads(int num)
	{
		this.numRequestWorkers = num;
	}

	/**
	 * Determine whether this component is also a service provider. By default
	 * true.
	 *
	 * @return whether this component is also a service provider
	 */
	public boolean isServiceProvider()
	{
		return serviceProvider;
	}

	/**
	 * Determine whether this class should register itself as a service
	 * provider.
	 *
	 * @param provide whether this class should register itself as service
	 *        provider
	 */
	public void setServiceProvider(boolean provide)
	{
		this.serviceProvider = provide;
	}

	public void bind(Responder r, String dest) throws ResponderException,
			IOException
	{
		if ((r == null) || (dest == null))
		{
			String msg = "no param can be null";
			throw new IllegalArgumentException(msg);
		}

		// make sure that there isn't already a responder bound to dest
		if (destMap.containsKey(dest))
		{
            String msg = "responder already bound to dest " + dest;
			throw new ResponderException(msg);
		}

        // create a subscription to receive requests to this destination
		Subscription sub = Message.createSubForDestination(dest);

		// create a new notification listener
		sub.addNotificationListener(
				new SwitchboardNotificationListener(r, connection,
													threadPool));

		// now add this subscription to the consumer
		synchronized (this) { consumer.addSubscription(sub); }

		// now put this subscription in the map, corresponding to the
		// responder's uid
		subMap.put(r.getGuid(), sub);

		// put the responder in the destination map, corresponding to its
		// dest
		destMap.put(dest, r);

		// now fire the event
		fireResponderBound(r, dest);
	}

	public void unbind(Responder r, String dest) throws ResponderException,
			IOException
	{
        if ((r == null) || (dest == null))
		{
			String msg = "no param can be null";
			throw new IllegalArgumentException(msg);
		}

       	// make sure that this responder has actually been bound
        if (destMap.containsKey(dest))
		{
			Responder responder = (Responder) destMap.get(dest);

			if (responder != r)
			{
				String msg = "given Responder is not bound to dest " + dest;
				throw new ResponderException(msg);
			}
		}
		else
		{
			String msg = "no Responder bound to dest " + dest;
			throw new ResponderException(msg);
		}

        // get the subscription that this responder was bound to
        Subscription sub = (Subscription) subMap.get(r.getGuid());

		// now stop subscribing to this responder
        synchronized (this) { consumer.removeSubscription(sub); }

        // remove the subscription and the destination binding
		subMap.remove(r.getGuid());

		// remove the destination binding
		destMap.remove(dest);

		// fire the unbinding event
		fireResponderUnbound(r, dest);
	}

	public void addSwitchBoardListener(SwitchBoardListener sbl)
	{
		listenerList.add(SwitchBoardListener.class, sbl);
	}

	public void removeSwitchBoardListener(SwitchBoardListener sbl)
	{
		listenerList.remove(SwitchBoardListener.class, sbl);
	}

	/**
	 * Fire a SwitchBoardEvent when a Responder is bound.
	 *
	 * @param resp Responder that was bound
	 * @param dest Destination the Responder was bound to
	 */
	protected void fireResponderBound(Responder resp, String dest)
	{
		SwitchBoardEvent sbe = new SwitchBoardEvent(this, resp, dest);

        Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
            if (listeners[i] == SwitchBoardListener.class)
			{
				((SwitchBoardListener) listeners[i + 1]).responderBound(sbe);
			}
		}
	}

    /**
	 * Fired when a Responder is unbound from the SwitchBoard.
	 *
	 * @param resp Responder being unbound
	 * @param dest Destination the Responder was unbound from
	 */
	protected void fireResponderUnbound(Responder resp, String dest)
	{
		SwitchBoardEvent sbe = new SwitchBoardEvent(this, resp, dest);

        Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
            if (listeners[i] == SwitchBoardListener.class)
			{
				((SwitchBoardListener) listeners[i + 1]).responderUnbound(sbe);
			}
		}
	}

	public void initialize() throws ComponentException
	{
		// set up the threadpool
		threadPool = new PooledExecutor(new LinkedQueue());
		threadPool.setMinimumPoolSize(numRequestWorkers);

		// look for a connection from this container
		try
		{
			this.connection = (Connection) getContainer()
					.getService(this, this, Connection.class, null, this);
		}
		catch (TooManyListenersException tle)
		{
			; // ignore it, temporarily
		}
		if (connection == null)
		{
			String msg = "couldn't get a Connection object";
			throw new ServiceDependencyException(msg, Connection.class);
		}

		// create the consumer
		consumer = new Consumer(connection.elvinConnection());

		// register as a service provider
		if (serviceProvider)
		{
			spi = new SwitchBoardServiceProvider();

			if (! getContainer().addService(SwitchBoard.class, spi))
			{
				String msg = "couldn't register as SwitchBoard provider";
				throw new ComponentException(msg);
			}
		}
	}

	public void dispose() throws ComponentException
	{
		if (serviceProvider)
		{
			getContainer().revokeService(SwitchBoard.class, spi, true);
		}

		// clear the subscription map
		subMap.clear();
		subMap = null;

		// close the consumer and all subscriptions to it
		consumer.close();
		consumer = null;

			// release the connection
		getContainer().releaseService(this, this, connection);
		connection = null;

		// shutdown the threadpool
		threadPool.setMinimumPoolSize(0);
		threadPool.setKeepAliveTime(10);
		threadPool.shutdownAfterProcessingCurrentlyQueuedTasks();

		try
		{
    		// wait for the threadpool to fully shutdown, but only wait
			// 1 second at max
			// TODO: this is a bug, may not be enuf time to process all queued requests
			// in the future drain() the threadpool and execute them yourself!
			threadPool.awaitTerminationAfterShutdown();
			threadPool.shutdownNow();
			threadPool = null;
		}
		catch (InterruptedException ie)
		{
			log.warn("received unexpected interrupted exception " + ie);
		}

		// now fire unbinding events for all the still-bound responders
        for (Iterator i = destMap.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry me = (Map.Entry) i.next();
			String dest = (String) me.getKey();
			Responder r = (Responder) me.getValue();

			fireResponderUnbound(r, dest);

			i.remove();
		}
		destMap = null;
	}

	private class SwitchBoardServiceProvider extends AbstractProvider
	{
		public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
												   Class serviceClass)
		{
			return null;
		}

		public void releaseService(BeanContextServices bcs, Object requestor,
								   Object service)
		{
			; // do nothing
		}

		public Object getService(BeanContextServices bcs, Object requestor,
								 Class serviceClass, Object serviceSelector)
		{
			return DefaultSwitchBoard.this;
		}
	}
}
