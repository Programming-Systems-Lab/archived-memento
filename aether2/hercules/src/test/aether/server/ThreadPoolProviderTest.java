package aether.server;

import aether.AetherTestCase;
import aether.server.AetherContainer;
import aether.server.ThreadPool;
import aether.server.DefaultThreadPool;

import java.beans.beancontext.BeanContextChildSupport;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class ThreadPoolProviderTest extends AetherTestCase
{
    public void testThreadPoolService() throws Exception
	{
        AetherContainer container = new AetherContainer();
        DefaultThreadPool tpp = new DefaultThreadPool();
        container.add(tpp);

        assertTrue(container.hasService(ThreadPool.class));

        container.remove(tpp);

		assertFalse(container.hasService(ThreadPool.class));
	}

	public void testThreadPoolWork() throws Exception
	{
		AetherContainer container = new AetherContainer();
        DefaultThreadPool tpp = new DefaultThreadPool();
        container.add(tpp);

        BeanContextChildSupport bccs = new BeanContextChildSupport();
		container.add(bccs);

      	ThreadPool pool = (ThreadPool)
				  container.getService(bccs, bccs, ThreadPool.class, null,
									   bccs);

		assertNotNull(pool);

		final boolean[] gotRun = new boolean[] { false };
        Runnable run = new Runnable()
		{
			public void run()
			{
				gotRun[0] = true;
				synchronized (gotRun) { gotRun.notify(); };
			}
		};

        pool.execute(run);
		synchronized (gotRun) { gotRun.wait(); }

		assertTrue(gotRun[0]);
        container.remove(tpp);
	}
}
