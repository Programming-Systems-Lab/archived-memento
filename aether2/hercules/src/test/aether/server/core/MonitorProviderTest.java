package aether.server.core;

import aether.AetherTestCase;
import aether.server.AetherContainer;
import aether.event.*;
import aether.net.Connection;
import aether.net.Monitor;
import aether.net.DefaultConnection;

import java.beans.beancontext.BeanContextChildSupport;

/**
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class MonitorProviderTest extends AetherTestCase
{

	public void testServiceProvider() throws Exception
	{
		AetherContainer container = new AetherContainer();
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		dcp.setDefaultConnection(conn);
		container.add(dcp);

		MonitorProvider mon = new MonitorProvider();
		container.addService(Monitor.class, mon);

		BeanContextChildSupport bean = new BeanContextChildSupport();
		container.add(bean);

        Monitor monitor = (Monitor)
				container.getService(bean, bean, Monitor.class, conn, bean);

		final EventQueue queue = new SimpleEventQueue();
        // set a new event handler that queues stuff
        monitor.addNoticeListener(new EventHandler()
		{
			public void handle(Event event)
			{
				queue.enqueue(event);
			}
		});

		// tell the monitor to watch '0'
		monitor.watch("0");

		Thread.sleep(500);

        // fire off a notice
		Notice n = new Notice();
		n.setSourceId("0");
		conn.publish(n);

		Thread.sleep(500);

        // make sure we got it back
		Notice received = (Notice) queue.dequeue();

		assertEquals(received.getSourceId(), "0");

		container.releaseService(bean, bean, monitor);
	}
}


