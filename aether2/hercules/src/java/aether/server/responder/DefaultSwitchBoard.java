package aether.server.responder;

import aether.event.*;
import aether.net.Connection;
import aether.server.ThreadPool;
import org.apache.log4j.Logger;
import org.elvin.je4.Consumer;
import org.elvin.je4.Notification;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Subscription;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.*;

/**
 * Default implementation of the SwitchBoard interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultSwitchBoard implements SwitchBoard
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
     * Mapping from Responder objects to (Elvin) Subscription objects.
     */
    protected Map subMap = Collections.synchronizedMap(new IdentityHashMap());

    /**
     * ThreadPool used to execute the handling of requests.
     */
    protected ThreadPool threadPool;

    /**
     * Mapping from Destination IDs to Responder objects.
     */
    protected Map destMap = Collections.synchronizedMap(new HashMap());

    private Consumer consumer;
    private NotificationListener requestListener;

    private static final Logger log =
            Logger.getLogger(DefaultSwitchBoard.class);


    /**
     * Set the Connection used for event sending/receiving.
     *
     * @param conn Connection used for event sending/receiving
     */
    public void setConnection(Connection conn)
    {
        if (conn == null)
        {
            String msg = "conn can't be null";
            throw new IllegalArgumentException(msg);
        }

        this.connection = conn;
    }

    /**
     * Get the Connection used for event sending/receiving.
     *
     * @return Connecting used for event handling
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Set the ThreadPool to be used by the DefaultSwitchBoard in order to
     * handle incoming requests.
     *
     * @param threadPool ThreadPool used to handle incoming requests
     */
    public void setThreadPool(ThreadPool threadPool)
    {
        if (threadPool == null)
        {
            String msg = "threadPool can't be null";
            throw new IllegalArgumentException(msg);
        }

        this.threadPool = threadPool;
    }

    /**
     * Get the ThreadPool used to handle incoming requests.
     *
     * @return ThreadPool used to handle incoming requests
     */
    public ThreadPool getThreadPool()
    {
        return threadPool;
    }

    public void bind(final Responder responder, String dest)
            throws ResponderException, IOException
    {
        if ((responder == null) || (dest == null))
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

        // create a subscription to receive requests sent to this destination
        Subscription sub = Message.createSubscriptionForDestination(dest);

        // construct a notification listener that will queue the requests sent
        // to the subscription
        requestListener = new NotificationListener()
        {
            public void notificationAction(Notification notification)
            {
                threadPool.execute(
                        new RequestProcessor(responder, notification));
            }
        };
        sub.addNotificationListener(requestListener);

        // now add this subscription to the consumer
        synchronized (this) { consumer.addSubscription(sub); }

        // now put this subscription in the map, corresponding to the
        // responder reference
        subMap.put(responder, sub);

        // put the responder in the destination map, corresponding to its
        // dest
        destMap.put(dest, responder);

        // now fire the event
        fireResponderBound(responder, dest);
    }

    public void unbind(Responder responder, String dest) throws ResponderException,
            IOException
    {
        if ((responder == null) || (dest == null))
        {
            String msg = "no param can be null";
            throw new IllegalArgumentException(msg);
        }

        // make sure that this responder has actually been bound
        if (destMap.containsKey(dest))
        {
            Responder oldResp = (Responder) destMap.get(dest);

            if (oldResp != responder)
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
        Subscription sub = (Subscription) subMap.get(responder);

        // now stop subscribing to this responder
        synchronized (this) { consumer.removeSubscription(sub); }

        // remove the subscription and the destination binding
        subMap.remove(responder);

        // remove the destination binding
        destMap.remove(dest);

        // fire the unbinding event
        fireResponderUnbound(responder, dest);
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

    public void initialize()
    {
        // create the consumer
        consumer = new Consumer(connection.elvinConnection());
    }

    public void dispose()
    {
        // clear the subscription map
        subMap.clear();
        subMap = null;

        // close the consumer and all subscriptions to it
        consumer.close();
        consumer = null;

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

    /**
     * Represents a unit of work that must be completed to process some request.
     * RequestExecutors are usually queued up and then executed asynchronously in
     * the request processing threads.
     *
     * @author Buko O. (buko@concedere.net)
     * @version 0.1
     **/
    private class RequestProcessor implements Runnable
    {
        private Responder responder;
        private Notification notifcation;

        /**
         * Construct a new RequestProcessor to process the given request.
         *
         * @param resp  Responder that generates the response
         * @param notif Notification containing Request data
         */
        public RequestProcessor(Responder resp, Notification notif)
        {
            if ((resp == null) || (notif == null))
            {
                String msg = "no parameter can be null";
                throw new IllegalArgumentException(msg);
            }

            this.responder = resp;
            this.notifcation = notif;
        }

        /**
         * Execute the contained request by running it.
         */
        public void run()
        {
            Request request = null;

            if (Event.isRequest(notifcation))
            {
                try
                {
                    request = new Request();
                    request.parse(notifcation);
                }
                catch (EventException ee)
                {
                    log.warn("received bad request data", ee);
                    ee.printStackTrace();
                }

                // construct the necessary Response object
                final Response response =
                        request.createResponse(responder.getResponderId());

                // ask the responder to process it
                try
                {
                    responder.respond(request, response);
                }
                catch (ResponderException re)
                {
                    throw new RespondFailedException(re);
                }

                // instead of sending the response back in the request
                // handling thread, in order to increase throughput we queue
                // up the actual sending
                threadPool.execute(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            connection.publish(response);
                        }
                        catch (IOException ioe)
                        {
                            log.warn("Failed to send response "
                                     + response, ioe);
                        }
                    }
                });
            }
            else
            {
                // received a notification that wasn't a request
                log.warn("recieved non-request notification: " + notifcation);
            }
        }
    }

}
