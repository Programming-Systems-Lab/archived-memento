package aether.server.core;

import aether.AetherTestCase;
import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.server.AetherContainer;

import java.beans.beancontext.BeanContextChildSupport;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultConnectionProviderTest extends AetherTestCase
{
	public void testConnectionService() throws Exception
	{
    	AetherContainer container = new AetherContainer();
        DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		assertFalse(conn.isOpen());

		dcp.setDefaultConnection(conn);

		container.add(dcp);

		assertTrue(container.hasService(Connection.class));
		assertTrue(conn.isOpen());

		container.remove(dcp);

		assertFalse(container.hasService(Connection.class));
		assertFalse(conn.isOpen());
	}

	public void testServiceAcquire() throws Exception
	{
		AetherContainer container = new AetherContainer();
        DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());

		dcp.setDefaultConnection(conn);
		container.add(dcp);

		BeanContextChildSupport child = new BeanContextChildSupport();
		container.add(child);

		Connection def = (Connection) container
				.getService(child, child, Connection.class, null, child);
		assertNotNull(def);
		assertTrue(def.isOpen());

		container.releaseService(child, child, def);
	}
}
