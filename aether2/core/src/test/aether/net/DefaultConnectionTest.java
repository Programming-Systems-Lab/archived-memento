package aether.net;

import aether.AetherTestCase;
import aether.event.*;
import org.elvin.je4.Consumer;
import org.elvin.je4.Subscription;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Notification;

/**
 *
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class DefaultConnectionTest extends AetherTestCase
{
	private DefaultConnection conn;
	private EventQueue queue;

	public void setUp() throws Exception
	{
		queue = new SimpleEventQueue();
		conn = new DefaultConnection(getElvinHost(), getElvinPort());
		conn.open();
	}

	public void testSendRequest() throws Exception
	{
		Request r = new Request();
		r.setVerb("GET");

		// try out a header
		r.setHeader("User-Agent", "JUnit Test");

		// try out a query parameter
		r.setParameter("services", "all");
		r.setParameter("client", "rich");
		conn.publish(r);
	}

	public void testSendResponse() throws Exception
	{
		Response r = new Response();
		r.setCode(200);
		r.setReasonLine("OK");
		r.setHeader("Location", "COOLIO!!!");
		conn.publish(r);
	}

	public void testSendNotice() throws Exception
	{
		Notice n = new Notice();
		n.setHeader("Priority", "High");
		conn.publish(n);
	}

	public void testReceieve() throws Exception
	{
		// subscribe to everything
		Consumer consumer = new Consumer(conn.elvinConnection());
		Subscription sub = new Subscription("require(" + Attribute.Event.EVENT_ID + ")");
		sub.addNotificationListener(new NotificationListener()
		{
			public void notificationAction(Notification notification)
			{
				try
				{
					if (Event.isNotice(notification))
						queue.enqueue(new Notice(notification));
					else if (Event.isRequest(notification))
						queue.enqueue(new Request(notification));
					else if (Event.isResponse(notification))
						queue.enqueue(new Response(notification));
				}
				catch (EventException e)
				{

				}
			}
		});
		consumer.addSubscription(sub);

		// send a test notice
		Notice n = new Notice();
		n.setHeader("Priority", "High");
		conn.publish(n);

		// send a test request
		Request r = new Request();
		r.setVerb("GET");
		r.setHeader("User-Agent", "JUnit Test");
		r.setParameter("services", "all");
		r.setParameter("client", "rich");

		conn.publish(r);

		// wait for the event to come back!
		Thread.sleep(1000);

		// first event should be the getNotification
		Notice not = (Notice) queue.dequeue();

		assertEquals(not.getHeader("Priority"), "High");

		Request req = (Request) queue.dequeue();
		assertEquals(req.getParameter("client"), "rich");

	}

	public void tearDown() throws Exception
	{
		conn.close();
		conn = null;
		queue = null;
	}
}
