package aether.net;

import aether.event.Event;
import aether.event.EventException;
import aether.event.Request;
import aether.event.Response;
import aether.util.GuidFactory;
import org.apache.log4j.Logger;
import org.elvin.je4.Consumer;
import org.elvin.je4.Notification;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Subscription;

import java.io.IOException;

/**
 * Represents a conversation with a specific resource on an Aether host.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public abstract class AbstractLink implements Link
{
	/**
	 * GUID of the last component to send a response or the end point of this
	 * link.
	 */
	protected String destination;

	/**
	 * Unique ID for this link.
	 */
	protected String linkId;

	/**
	 * True if this link is closed.
	 */
	protected boolean closed = true;

	/**
	 * Underlying Connection used to send and receive.
	 */
	protected Connection connection;


	private Consumer consumer;
	private Subscription subscription;
	private Response response;
	private boolean timedOut;
	private NotificationListener notifListener =
			new LinkNotificationListener();

	private static final Logger logger = Logger.getLogger(AbstractLink.class);

	public void connect() throws IOException
	{
     	if (!closed)
		 {
			 String msg = "link already connected";
			 throw new IllegalStateException(msg);
		 }

		// open the connection if we have to
		if (!connection.isOpen()) connection.open();

		// calculate the link id
		this.linkId = GuidFactory.createId();

        // construct the subscription needed to listen for responses sent over
		// this link
		this.subscription = Response.createSubscriptionToReceive(linkId);
		this.subscription.addNotificationListener(notifListener);

		// now subscribe to all responses sent over this link
        this.consumer = new Consumer(connection.elvinConnection());
        this.consumer.addSubscription(subscription);
		this.closed = false;
	}

	public synchronized void close() throws IOException
	{
		if (closed)
		{
			String msg = "session already closed";
			throw new IllegalStateException(msg);
		}

        // unsubscribe from responses and close the consumer
		consumer.removeSubscription(this.subscription);
		consumer.close();

		// free all resources. Note that the Connection is not closed here!
		// it's the job of the subclass to close the connection
        this.consumer = null;
		this.subscription = null;
		this.response = null;
		closed = true;
	}

	public String getLinkId()
	{
		return linkId;
	}

	public String getDestination()
	{
		return destination;
	}

	public Request createRequest(String verb)
	{
		if (verb == null)
		{
			String msg = "verb cannot be null";
			throw new IllegalArgumentException(msg);
		}

		if (closed)
		{
			String msg = "link is closed";
			throw new IllegalStateException(msg);
		}

		Request request = new Request();

		// set the verb, linkId, destination
		request.setVerb(verb);
		request.setLink(this.linkId);
		request.setDestination(this.destination);

		return request;
	}


	public synchronized Response send(Request req) throws IOException
	{
		if (req == null)
		{
			String msg = "request can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (closed)
		{
			String msg = "link is closed";
			throw new IllegalStateException(msg);
		}

        // make sure that this is a request that we created!!

		// now send the request!!
		connection.publish(req);

		// now send the request and block until the response comes
		return blockUntilResponse();
	}

	/**
	 * After a request has been sent, the calling thread must be blocked
	 * until a response event is received. This method causes the blocking to
	 * occur.
	 *
	 * @return Response of the request
	 * @throws IOException
	 *         if something goes wrong
	 */
	protected synchronized Response blockUntilResponse()
			throws IOException
	{

        // go into a spin block and just block until either a response comes
		// or the request is timed out
        try
		{
			while ((response == null) && (!timedOut))
			{
                this.wait();
			}
		}
		catch (InterruptedException ie)
		{
			// should never happen
			String msg = "internal error in " + getClass();
			throw new Error(msg, ie);
		}

        // ok we've woken up! did we wake up because we timed out?
		if (timedOut)
		{
            timedOut = false;
            String msg = "request timed out";
			throw new IOException(msg);
		}

        // otherwise, let's save the given response and send it while resetting
		// te state of the connection
        Response lastResponse = response;
        this.response = null;
        return lastResponse;
	}

	/**
	 * Timeout this connection.
	 */
	public synchronized void timeOut()
	{
		if (closed)
		{
			String msg = "link is closed";
			throw new IllegalStateException(msg);
		}

        timedOut = true;

		// wake up any thread waiting on the connection
		notify();
	}

	/**
	 * A special NotificationListener that will allow us to process all
	 * events sent to this AbstractLink.
	 *
	 * @author Buko O. (buko@concedere.net)
	 * @version 0.1
	 */
	private class LinkNotificationListener implements NotificationListener
	{
		public void notificationAction(Notification notification)
		{
			// if this notification is a response  let's process it
        	if (Event.isResponse(notification))
			{
				try
				{
					// obtain the lock on the outer object first!
					synchronized (AbstractLink.this)
					{
            			response = new Response(notification);

						// wake up any threads sleeping on this object, waiting
						// for a response
						AbstractLink.this.notify();
					}
				}
				catch (EventException me)
				{
                	// bad response data, let's log it and ignore it
                    String msg = "receieved badly formed response";
					throw new Error(msg, me);
				}
			}
		}
	}
}
