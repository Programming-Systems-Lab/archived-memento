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
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class DefaultSocket implements Socket
{
	/**
	 * URI that represents the destination of the socket.
	 */
	protected String destination;

	/**
	 * Unique ID for this link.
	 */
	protected String linkId;

	/**
	 * True if this link is closed.
	 */
	protected boolean disconnected = true;

    /**
     * Timeout time for send operations.
     */
    protected long timeOut = DEFAULT_TIMEOUT;

	private Connection connection;
	private Consumer consumer;
	private Subscription subscription;
	private Response mostRecentResponse;
    private Request lastRequestSent;
	private NotificationListener notifListener;

	private static final Logger logger = Logger.getLogger(DefaultSocket.class);

     /**
	 * Construct a new DefaultSocket to some responding component on the network.
	 *
	 * @param conn Connection to the underlying event network
	 * @param dest destination to send requests to
	 */
	public DefaultSocket(Connection conn, String dest)
	{
        if ((conn == null) || (dest == null))
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        if (!conn.isOpen())
        {
            String msg = "conn must be an open connection";
            throw new IllegalArgumentException(msg);
        }

		this.connection = conn;
		this.destination = dest;
	}

	public synchronized void connect() throws IOException
	{
     	if (!disconnected)
		 {
			 String msg = "link already connected";
			 throw new IllegalStateException(msg);
		 }

		// calculate the link id
		this.linkId = GuidFactory.createId();

        // construct the subscription needed to listen for responses sent over
		// this link
		this.subscription = Request.createSubscriptionToReceiveResponses(linkId);
        this.notifListener = new LinkNotificationListener();
		this.subscription.addNotificationListener(notifListener);

		// now subscribe to all responses sent over this link
        this.consumer = new Consumer(connection.elvinConnection());
        this.consumer.addSubscription(subscription);
		this.disconnected = false;
	}

	public synchronized void disconnect() throws IOException
	{
		if (disconnected)
		{
			String msg = "link already closed";
			throw new IllegalStateException(msg);
		}

        // unsubscribe from responses and close the consumer
		consumer.close();

		// free all resources
        this.consumer = null;
		this.subscription = null;
        this.notifListener = null;
		this.mostRecentResponse = null;
		disconnected = true;
	}

    public boolean isConnected()
    {
        return disconnected;
    }

	public String getLinkId()
	{
		return linkId;
	}

	public String getDestination()
	{
		return destination;
	}

    public void setTimeOut(long timeout)
    {
        this.timeOut = timeout;
    }

    public long getTimeOut()
    {
        return timeOut;
    }

	public synchronized Response send(Request request) throws IOException
	{
		if (request == null)
		{
			String msg = "request can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (disconnected)
		{
			String msg = "link is closed";
			throw new IllegalStateException(msg);
		}

        // set the linkId, destination
		request.setLink(this.linkId);
		request.setDestination(this.destination);

		// now send the request!!
		connection.publish(request);

		// now send the request and block until the mostRecentResponse comes
        lastRequestSent = request;
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
			while (mostRecentResponse == null)
			{
                this.wait(timeOut);
			}
		}
		catch (InterruptedException ie)
		{
			// should never happen
			String msg = "internal error in " + getClass();
			throw new Error(msg, ie);
		}

        // ok we've woken up! did we wake up because we timed out?
		if (mostRecentResponse == null)
		{
            String msg = "request timed out";
			throw new IOException(msg);
		}

        // otherwise, let's save the given mostRecentResponse and send it while resetting
		// te state of the connection
        Response lastResponse = mostRecentResponse;
        this.mostRecentResponse = null;
        this.lastRequestSent = null;
        return lastResponse;
	}

	/**
	 * A special NotificationListener that will allow us to process all
	 * events sent to this DefaultSocket.
	 *
	 * @version $Revision: 1.2 $
	 */
	private class LinkNotificationListener implements NotificationListener
	{
		public void notificationAction(Notification notification)
		{
			// if this getNotification is a mostRecentResponse  let's process it
        	if (Event.isResponse(notification))
			{
				try
				{
					// obtain the lock on the outer object first!
					synchronized (DefaultSocket.this)
					{
            			Response response = new Response();
                        response.parse(notification);

                        // make sure that the response is a response to the
                        // last request sent -- note that the entire point is
                        // that it's possible to send out a single request and
                        // get multiple responses so the response should only
                        // be processed if it's the first response to the
                        // request and no response has been handled yet
                        if ((lastRequestSent == null) ||
                                (!lastRequestSent.getEventId().
                                equals(response.getResponseTo())))
                        {
                            logger.debug("Received Response " + response
                                         + " which wasn't a response to last" +
                                         " request sent");
                            return;
                        }

						// wake up any threads sleeping on this object, waiting
						// for a mostRecentResponse
                        DefaultSocket.this.mostRecentResponse = response;
						DefaultSocket.this.notify();
					}
				}
				catch (EventException me)
				{
                	// bad mostRecentResponse data, let's log it and ignore it
                    String msg = "receieved badly formed mostRecentResponse";
					logger.warn(msg, me);
				}
			}
            else
            {
                logger.warn("received notification " + notification
                            + " which isn't a mostRecentResponse");
            }
		}
	}
}
