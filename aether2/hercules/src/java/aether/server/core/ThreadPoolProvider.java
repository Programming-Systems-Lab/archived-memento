package aether.server.core;

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

import aether.server.core.ThreadPool;

/**
 * Provides a container-wide threadpool that can be used to process incoming
 * events and other units of work.
 *
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class ThreadPoolProvider extends DefaultComponent implements
		Initializable, Startable, Disposable, BeanContextServiceProvider
{
    private int numWorkers = 5;
    private PooledExecutor executor;
    private ThreadPool threadpool;

	private static final Logger log =
			Logger.getLogger(ThreadPoolProvider.class);

	public void initialize() throws ComponentException
	{
        // setup the threadpool
		executor = new PooledExecutor(new LinkedQueue());
		executor.setMinimumPoolSize(numWorkers);

		threadpool = new ThreadPoolImpl();
	}

	public void dispose() throws ComponentException
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
			throw new ComponentException(msg, ie);
		}

		threadpool = null;
	}

	public void start() throws ComponentException
	{
		if (!getContainer().addService(ThreadPool.class, this))
		{
			String msg = "couldn't register to provide ThreadPool service";
			throw new ComponentException(msg);
		}
	}

	public void stop() throws ComponentException
	{
		getContainer().revokeService(ThreadPool.class, this, true);
	}

	public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
											   Class serviceClass)
	{
		return null;
	}

	public Object getService(BeanContextServices bcs, Object requestor,
							 Class serviceClass, Object serviceSelector)
	{
		return threadpool;
	}

	public void releaseService(BeanContextServices bcs, Object requestor,
							   Object service)
	{
		; // do nothing
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

	/**
	 * A simple ThreadPool implementation.
	 */
	private class ThreadPoolImpl implements ThreadPool
	{
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
}
