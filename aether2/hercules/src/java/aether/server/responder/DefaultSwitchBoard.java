package aether.server.responder;

import aether.event.Event;
import aether.event.EventHandler;
import aether.event.Request;
import aether.event.Response;
import aether.net.Connection;
import aether.net.DefaultServerSocket;
import aether.net.ServerSocket;
import aether.server.ThreadPool;
import org.apache.log4j.Logger;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.*;

/**
 * Default implementation of the SwitchBoard interface.
 *
 * @author Buko O. (aso22@columbia.edu)
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
     * Mapping from Responder objects to ServerSocket objects.
     */
    protected Map socketMap =
            Collections.synchronizedMap(new IdentityHashMap());

    /**
     * ThreadPool used to execute the handling of requests.
     */
    protected ThreadPool threadPool;

    /**
     * Mapping from Destination IDs to Responder objects.
     */
    protected Map destMap = Collections.synchronizedMap(new HashMap());

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

        if (isBound(dest))
        {
            String msg = "responder already bound to dest " + dest;
            throw new ResponderException(msg);
        }

        // create a new ServerSocket to receive requests to this destination
        ServerSocket serverSocket = new DefaultServerSocket(dest, connection);

        // construct an event handler that will process requests sent to the
        // server socket
        EventHandler requestHandler = new EventHandler()
        {
            public void handle(Event event)
            {
                threadPool.execute(
                        new RequestProcessor(responder, (Request) event));
            }
        };
        serverSocket.setEventHandler(requestHandler);
        serverSocket.bind();

        // now put this subscription in the map, corresponding to the
        // responder reference
        socketMap.put(responder, serverSocket);

        // put the responder in the destination map, corresponding to its
        // dest
        destMap.put(dest, responder);

        // now fire the event
        fireResponderBound(responder, dest);
    }

    private boolean isBound(String destination)
    {
        return destMap.containsKey(destination);
    }

    public void unbind(Responder responder, String dest)
            throws ResponderException, IOException
    {
        if ((responder == null) || (dest == null))
        {
            String msg = "no param can be null";
            throw new IllegalArgumentException(msg);
        }

        // make sure that this responder has actually been bound
        if (isBound(dest))
        {
            Responder oldResp = (Responder) destMap.get(dest);

            if (oldResp != responder)
            {
                String msg = "given Responder is not bound to dest " + dest;
                throw new ResponderException(msg);
            }

            // close the server socket
            ServerSocket socket = (ServerSocket) socketMap.remove(responder);
            socket.unbind();

            // remove it from the destination map
            destMap.remove(dest);

            // fire the unbinding event
            fireResponderUnbound(responder, dest);
        }
        else
        {
            String msg = "no Responder bound to dest " + dest;
            throw new ResponderException(msg);
        }
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
    }

    public void dispose()
    {
        // iterate over the still bound Responders and unbind each one
        // --- note that unbinding a Responder makes changes to the collections
        // --- being iterated so we have to make a copy
        Map destMapCopy = new HashMap(destMap);

        // now fire unbinding events for all the still-bound responders
        for (Iterator i = destMapCopy.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            String dest = (String) entry.getKey();
            Responder r =  (Responder)  entry.getValue();

            try
            {
                unbind(r, dest);
            }
            catch (Exception e)
            {
                String msg = "encountered unexpected error unbinding " +
                        "responder=" + r + " from dest=" + dest;
                log.warn(msg, e);
            }
        }

        destMap = null;
    }

    /**
     * Represents a unit of work that must be completed to process some request.
     * RequestExecutors are usually queued up and then executed asynchronously in
     * the request processing threads.
     *
     * @author Buko O. (aso22@columbia.edu)
     * @version 0.1
     **/
    private class RequestProcessor implements Runnable
    {
        private Responder responder;
        private Request request;

        /**
         * Construct a new RequestProcessor to process the given request.
         *
         * @param resp Responder that generates the response
         * @param req  Request Event to be processed
         */
        public RequestProcessor(Responder resp, Request req)
        {
            if ((resp == null) || (req == null))
            {
                String msg = "no parameter can be null";
                throw new IllegalArgumentException(msg);
            }

            this.responder = resp;
            this.request = req;
        }

        /**
         * Execute the contained request by running it.
         */
        public void run()
        {
            // construct the necessary Response object
            final Response response = new Response(request);

            // ask the responder to process it
            try
            {
                responder.respond(request, response);
            }
            catch (ResponderException re)
            {
                String msg = "responder " + responder + " failed to " +
                        "process request " + request;
                log.warn(msg, re);

                // todo: in the future when a responder fails to process
                // --- an event it should probably be reloaded somehow
                // --- like servlets
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
    }
}

