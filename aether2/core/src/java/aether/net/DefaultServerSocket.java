package aether.net;

import org.elvin.je4.Consumer;
import org.elvin.je4.Subscription;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Notification;
import org.apache.log4j.Logger;
import aether.event.*;

import java.io.IOException;

/**
 * Base implementation of the {@link ServerSocket} interface.
 *
 * Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class DefaultServerSocket implements ServerSocket
{
    /**
     * URI of the destination.
     */
    protected String destination;

    /**
     * ID of the component generating responses.
     */
    protected String sourceID;

    /**
     * EventHandler to be notified of incoming requests.
     */
    protected EventHandler eventHandler;

    private Consumer consumer;
    private Subscription subscription;
    private Connection connection;

    private static final Logger log =
            Logger.getLogger(DefaultServerSocket.class);

    /**
     * Construct a new DefaultServerSocket.
     *
     * @param dest  URI of the destination
     * @param srcID ID of the component generating responses
     * @param conn  Connection to the event network
     */
    public DefaultServerSocket(String dest, String srcID, Connection conn)
    {
        if ((dest == null) || (srcID == null) || (conn == null))
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        if (!conn.isOpen())
        {
            String msg = "conn isn't an open connection";
            throw new IllegalArgumentException(msg);
        }

        this.destination = dest;
        this.sourceID = srcID;
        this.connection = conn;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getSourceId()
    {
        return sourceID;
    }

    public void setEventHandler(EventHandler handler)
    {
        this.eventHandler = handler;
    }

    public EventHandler getEventHandler()
    {
        return eventHandler;
    }

    public synchronized void bind() throws IOException
    {
        // construct the subscription to receive requests sent to the
        // destination
        this.subscription =
                Message.createSubscriptionForDestination(destination);

        // when a request comes in, just hand it off to the EventHandler
        NotificationListener n = new NotificationListener()
        {
            public void notificationAction(Notification notification)
            {
                // make sure the notification is a request
                if (!Event.isRequest(notification))
                {
                    log.warn("Received notification " + notification
                             + " which isn't a request.");
                    return;
                }

                // try to parse it as a request
                Request request = new Request();
                try
                {
                    request.parse(notification);
                }
                catch (EventException ee)
                {
                    log.warn("Failed to parse notification " + notification
                             + "as a request.", ee);
                    return;
                }

                // pass it to the event handler
                eventHandler.handle(request);
            }
        };
        subscription.addNotificationListener(n);

        consumer = new Consumer(connection.elvinConnection());
        consumer.addSubscription(subscription);
    }

    public synchronized void unbind() throws IOException
    {
        consumer.close();
        consumer = null;
        subscription = null;
    }

    public void sendResponse(Request request, Response response)
            throws IOException
    {
        if ((request == null) || (response == null))
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        // preserve the link ID from the request
        response.setLink(request.getLink());

        // destination of the response is the source of the request
        response.setDestination(request.getSourceId());

        // set the source ID for the response
        response.setSourceId(sourceID);

        // now send the response over the network
        connection.publish(response);
    }
}

