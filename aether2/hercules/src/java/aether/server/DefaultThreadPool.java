package aether.server;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.framework.Startable;
import net.concedere.dundee.framework.Initializable;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import org.apache.log4j.Logger;

import java.beans.beancontext.BeanContextServiceProvider;
import java.beans.beancontext.BeanContextServices;
import java.util.Iterator;

import aether.server.ThreadPool;

/**
 * Provides a threadpool component that can be aggressively tuned.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultThreadPool implements ThreadPool
{
    private int numWorkers = 5;
    private PooledExecutor executor;

    private static final Logger log =
            Logger.getLogger(DefaultThreadPool.class);

    /**
     * Start the ThreadPool.
     */
    public void start()
    {
        // setup the threadpool
        executor = new PooledExecutor(new LinkedQueue());
        executor.setMinimumPoolSize(numWorkers);
    }

    /**
     * Shutdown the ThreadPool after all currently queued tasks are completed.
     */
    public void shutdown()
    {
        executor.setMinimumPoolSize(0);
        executor.setKeepAliveTime(10);
        executor.shutdownAfterProcessingCurrentlyQueuedTasks();

        try
        {
            // wait the pool to fully shutdown
            // todo: this a bug, for some reason it takes 60 seconds if we
            // completely wait
            executor.awaitTerminationAfterShutdown(1000);
            executor.shutdownNow();
            executor = null;
        }
        catch (InterruptedException ie)
        {
            String msg = "unexpected interrupted exception!";
            throw new Error(msg, ie);
        }
    }

    /**
     * Get the number of worker threads being used by the threadpool.
     *
     * @return number of workers being used by the pool
     */
    public int getNumWorkers()
    {
        return numWorkers;
    }

    /**
     * Set the number of worker threads being used by the pool.
     *
     * @param numWorkers number of workers being used by the pool
     */
    public void setNumWorkers(int numWorkers)
    {
        this.numWorkers = numWorkers;
    }

    public void execute(Runnable work)
    {
        try
        {
            executor.execute(work);
        }
        catch (InterruptedException e)
        {
            log.warn("unexpected interrupted exception", e);
        }
    }
}
