package aether.server;

import aether.event.Event;
import aether.net.Connection;
import aether.net.Publisher;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Provides the publishing service to components in the container for sending
 * outgoing events. This component provides queued publishing (publish
 * operations will not block). In the future, this publisher may also provide
 * transactional publishing by saving outgoing events to a transactional
 * persistence layer.
 *
 * TODO: enforce stop() method contract
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class ManagedPublisher implements Publisher
{
    private Connection connection;
    private ThreadPool threadPool;

    private final static Logger log = Logger.getLogger(ManagedPublisher.class);

    /**
     * Set the Connection to be used by the ManagedPublisher.
     *
     * @param conn Connection to be used by the ManagedPublisher
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
     * Get the Connection for the Publisher.
     *
     * @return Connection for the Publisher
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Set the ThreadPool to be used by the Publisher.
     *
     * @param pool ThreadPool to be used by the Publisher
     */
    public void setThreadPool(ThreadPool pool)
    {
        if (pool == null)
        {
            String msg = "ThreadPool can't be null";
            throw new IllegalArgumentException(msg);
        }

        this.threadPool = pool;
    }

    /**
     * Get the ThreadPool used to actually deliver events.
     *
     * @return ThreadPool used to deliver events
     */
    public ThreadPool getThreadPool()
    {
        return threadPool;
    }

    public void publish(final Event event) throws IOException
    {
        Runnable deliver = new Runnable()
        {
            public void run()
            {
                try
                {
                    connection.publish(event);
                }
                catch (IOException ioe)
                {
                    log.warn("Failed to deliver event " + event, ioe);
                }
            }
        };
        threadPool.execute(deliver);
    }
}
