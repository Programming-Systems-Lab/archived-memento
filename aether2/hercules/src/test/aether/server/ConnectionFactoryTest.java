package aether.server;

import aether.AetherTestCase;
import aether.server.ConnectionFactory;
import aether.net.Connection;
import aether.net.Publisher;
import aether.net.DefaultConnection;

import java.util.Map;
import java.util.HashMap;
import java.beans.beancontext.BeanContextChildSupport;

import net.concedere.dundee.DefaultContainer;
import net.concedere.dundee.Container;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class ConnectionFactoryTest extends AetherTestCase
{
    private Container container;
	private ConnectionFactory connFactory = new ConnectionFactory();

	public void setUp() throws Exception
	{
		container = new DefaultContainer();

		connFactory.setDefaultConnection(
				new DefaultConnection(getElvinHost(), getElvinPort()));
		container.add(connFactory);

	}

	public void testDefaultConnection() throws Exception
	{
		BeanContextChildSupport bean = new BeanContextChildSupport();
		container.add(bean);

		Connection conn =
				(Connection) container.getService(bean, bean,Connection.class,
												  null, bean);

        assertEquals(conn.getHost(), getElvinHost());
		assertEquals(conn.getPort(), getElvinPort());

		container.releaseService(bean, bean, conn);
	}

	public void testPublisher() throws Exception
	{
		BeanContextChildSupport bean = new BeanContextChildSupport();
		container.add(bean);

		// get's the default connection that is a publisher!
		Publisher pub = (Publisher)
				container.getService(bean, bean, Publisher.class, null, bean);

		assertNotNull(pub);

		container.releaseService(bean, bean, pub);
	}

	public void testCreateConnection() throws Exception
	{
		BeanContextChildSupport bean = new BeanContextChildSupport();
		container.add(bean);

		// create the params
		ConnectionFactory.Request params = new
				ConnectionFactory.Request(getElvinHost(), getElvinPort());

        Connection conn = (Connection)
				container.getService(bean, bean, Connection.class,
									 params, bean);

		assertNotNull(conn);
		assertTrue(conn.isOpen());

		container.releaseService(bean, bean, conn);
	}

	public void tearDown() throws Exception
	{
		connFactory.stop();
	}
}