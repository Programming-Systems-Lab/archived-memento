package aether.net;

import aether.AetherTestCase;
import aether.event.EventHandler;
import aether.event.Attribute;
import aether.event.Request;
import aether.event.Response;
import org.elvin.je4.Consumer;
import org.elvin.je4.Subscription;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Notification;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultLinkTest extends AetherTestCase
{
	private AutoResponder responder;

	public void setUp() throws Exception
	{
		responder = new AutoResponder();
	}

	public void testLink() throws Exception
	{
		Link link = new DefaultLink(getElvinHost(), getElvinPort(), "0");
        link.connect();

		Request req = link.createRequest(Request.Get);
		req.setSourceId("1");

		Response resp = link.send(req);
 		assertEquals(resp.getCode(), 200);
	}


	public void tearDown() throws Exception
	{
		responder.stop();
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
					new Subscription("regex(" + Attribute.Message.DESTINATION +
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
						resp.setReasonLine("AutoResponder RESPONSE!");
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
