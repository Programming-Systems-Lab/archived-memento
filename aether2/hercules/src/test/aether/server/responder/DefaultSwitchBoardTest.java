package aether.server.responder;

import aether.AetherTestCase;
import aether.server.ConnectionFactory;
import aether.event.Request;
import aether.event.Response;
import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.DefaultLink;
import aether.net.Link;
import net.concedere.dundee.DefaultContainer;
import net.concedere.dundee.Container;

import java.beans.beancontext.BeanContextChildSupport;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultSwitchBoardTest extends AetherTestCase
{

	public void setUp() throws Exception
	{

	}

	public void testServiceProvider() throws Exception
	{
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
        DefaultContainer container =  new DefaultContainer();
        ConnectionFactory cf = new ConnectionFactory();
		cf.setDefaultConnection(conn);
		container.add(cf);

		SwitchBoard sb = new DefaultSwitchBoard();
		container.add(sb);

		assertTrue(container.hasService(SwitchBoard.class));

		BeanContextChildSupport bean = new BeanContextChildSupport();
		container.add(bean);


        SwitchBoard sb2 = (SwitchBoard) container.getService(bean, bean,
															SwitchBoard.class,
															null, bean);
		assertNotNull(sb2);

		container.remove(sb);
		container.remove(cf);

		assertFalse(container.hasService(SwitchBoard.class));
	}

	public void testSwitchBoard() throws Exception
	{
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
        ConnectionFactory cf = new ConnectionFactory();
		cf.setDefaultConnection(conn);
		Container container = new DefaultContainer();
		container.add(cf);

		// construct a new switchboard
		DefaultSwitchBoard sb = new DefaultSwitchBoard();
    	container.add(sb);

		// bind an test responder to it
		Responder responder = new AbstractResponder()
		{
			public void respond(Request request, Response response)
					throws ResponderException
			{
				// assertEquals(request.getVerb(), Request.Get);
                response.setCode(200);
			}
		};
        responder.setGuid("0");
		sb.bind(responder, "0");

		// give a little time for the responder to set its subscriptions
        Thread.sleep(200);

		// create a new link and send some requests
		Link link = new DefaultLink(getElvinHost(), getElvinPort(), "0");
		link.connect();
		Request r = link.createRequest(Request.Get);
		r.setSourceId("1");
		Response response = link.send(r);
		assertEquals(response.getCode(), 200);

		// shutdown everything
		container.remove(sb);
		container.remove(cf);
		link.close();
	}

	public void tearDown() throws Exception
	{
	}
}
