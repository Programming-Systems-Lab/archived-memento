package aether.server.core;

import aether.AetherTestCase;
import aether.event.Request;
import aether.event.Response;
import aether.event.EventHandler;
import aether.event.Attribute;
import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.Link;

import java.beans.beancontext.BeanContextChildSupport;

import org.elvin.je4.Consumer;
import org.elvin.je4.Subscription;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Notification;
import net.concedere.dundee.Container;
import net.concedere.dundee.DefaultContainer;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class LinkProviderTest extends AetherTestCase
{
    private Container container;
	private LinkProvider provider;
	private Connection conn;
	private AutoResponder responder;

	public void setUp() throws Exception
	{
		responder = new AutoResponder();
    	container = new DefaultContainer();
		provider = new LinkProvider();
		container.addService(Link.class, provider);

		conn = new DefaultConnection(getElvinHost(), getElvinPort());
		conn.open();
	}

	public void testLinkProvider() throws Exception
	{
        BeanContextChildSupport bean = new BeanContextChildSupport();
		container.add(bean);

		// create the params
		LinkProvider.Request params = new LinkProvider.Request(conn, "0");

		Link link = (Link) container.getService(bean, bean, Link.class,
												params, bean);

		assertNotNull(link);

		Request req = link.createRequest(Request.Get);
		Response resp = link.send(req);

     	assertEquals(resp.getCode(), 200);

		// release the service
		container.releaseService(bean, bean, link);

		// make sure the connection is still open
		assertTrue(conn.isOpen());
	}

	public void tearDown() throws Exception
	{
		responder.stop();
		conn.close();
	}

	private class AutoResponder
	{
		private EventHandler handler;
        private Connection conn;
       	private Consumer consumer;

		public AutoResponder() throws Exception
		{
    		this.conn = new DefaultConnection(getElvinHost(), getElvinPort());
			conn.open();

			// subscribe to all requests
            this.consumer = new Consumer(conn.elvinConnection());
			Subscription sub =
					new Subscription("regex(" + Attribute.Message.Destination +
									 ", \"*\")");
			sub.addNotificationListener(new NotificationListener()
			{
				public void notificationAction(Notification notification)
				{
					try
					{
						Request req = new Request(notification);
						Response resp = new Response();
						resp.setLink(req.getLink());

						resp.setCode(200);
						resp.setReasonLine("AutoResponder Response!");
						conn.publish(resp);
					}
					catch (Exception e)
					{
						String msg = "autoresponder failed";
						throw new Error(msg, e);
					}
				}
			});  // sub.addNotificationListener

			consumer.addSubscription(sub);
		}

		public void stop() throws Exception
		{
			consumer.close();
			conn.close();
		}


	}
}
