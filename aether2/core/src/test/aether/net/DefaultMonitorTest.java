package aether.net;

import aether.AetherTestCase;
import aether.event.*;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultMonitorTest extends AetherTestCase
{
    private DefaultMonitor monitor;
	private EventQueue listQueue;
	private EventHandler handler;


    public void setUp() throws Exception
	{
		listQueue = new SimpleEventQueue();
		handler = new EventHandler()
		{
			public void handle(Event msg)
			{
				listQueue.enqueue(msg);
			}
		};

		monitor = new DefaultMonitor(elvinHost, elvinPort);
		monitor.addNoticeListener(handler);
		monitor.open();
	}

	public void testWatch() throws Exception
	{
        // subscribe to the fake resource
		monitor.watch("0");

		Thread.sleep(500);

    	// send some test notices from a fake resource
		Connection msgConn = new DefaultConnection(elvinHost, elvinPort);
		msgConn.open();

		Notice n = new Notice();
		n.setSourceId("0");
        n.setHeader("SequenceId", "0");
		msgConn.publish(n);

		Notice n2 = new Notice();
		n.setSourceId("0");
        n.setHeader("SequenceId", "1");
		msgConn.publish(n);

		// now wait a little bit, then see if we got them
		Thread.sleep(500);

		// dequeue any messages we receieved and make sure they're the ones
		// sent
		Event m1 = listQueue.dequeue();
		assertEquals(m1.getHeader("SequenceId"), "0");

		Event m2 = listQueue.dequeue();
		assertEquals(m2.getHeader("SequenceId"), "1");

		msgConn.close();
	}

	public void testSubscribe() throws Exception
	{
   		Monitor mon = new DefaultMonitor(getElvinHost(), getElvinPort());
        mon.open();
		final EventQueue queue = new SimpleEventQueue();
		mon.addNoticeListener(new EventHandler()
		{
			public void handle(Event msg)
			{
				queue.enqueue(msg);
			}
		});
        mon.subscribe("topic://test");

        Thread.sleep(200);

		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		conn.open();

        Notice notice = new Notice();
		notice.setTopicId("topic://test");
		notice.setHeader("SequenceId", "0");
		conn.publish(notice);

		notice = new Notice();
		notice.setTopicId("topic://test");
		notice.setHeader("SequenceId", "1");
        conn.publish(notice);

        Thread.sleep(200);

        Event e1 = queue.dequeue();
		assertEquals(e1.getHeader("SequenceId"), "0");
		Event e2 = queue.dequeue();
		assertEquals(e2.getHeader("SequenceId"), "1");

		mon.close();
		conn.close();
	}

	public void tearDown() throws Exception
	{
        monitor.close();
	}


}
