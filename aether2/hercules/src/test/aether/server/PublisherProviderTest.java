package aether.server;

import aether.AetherTestCase;
import aether.event.Notice;
import aether.event.EventQueue;
import aether.event.BlockingEventQueue;
import aether.event.Attribute;
import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.Publisher;
import aether.server.AetherContainer;
import aether.server.ManagedPublisher;

import java.beans.beancontext.BeanContextChildSupport;

import org.elvin.je4.Consumer;
import org.elvin.je4.Subscription;

/**
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class PublisherProviderTest extends AetherTestCase
{
    public void testService() throws Exception
	{
		AetherContainer container = new AetherContainer();

		DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		dcp.setDefaultConnection(conn);

		ManagedPublisher pub = new ManagedPublisher();

		container.add(dcp);
		container.add(pub);

		assertTrue(container.hasService(Publisher.class));


		container.remove(pub);
		container.remove(dcp);

		assertFalse(container.hasService(Publisher.class));

	}

	public void testPublish() throws Exception
	{
    	AetherContainer container = new AetherContainer();

		DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		dcp.setDefaultConnection(conn);

		ManagedPublisher pub = new ManagedPublisher();

		container.add(dcp);
		container.add(pub);

		BeanContextChildSupport bean = new BeanContextChildSupport();
        container.add(bean);

		Publisher publisher = (Publisher) container
				.getService(bean, bean, Publisher.class, null, bean);
		assertNotNull(publisher);


		Notice n = new Notice();
        publisher.publish(n);

		container.remove(pub);
		container.remove(dcp);
	}
}
