package aether.server.domain;

import aether.AetherTestCase;
import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.Monitor;
import aether.server.AetherContainer;
import aether.server.core.DefaultConnectionProvider;
import aether.server.core.PublisherProvider;
import aether.server.core.MonitorProvider;
import aether.server.core.ThreadPoolProvider;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class RemoteDomainAgentTest extends AetherTestCase
{
	public void testRemoteDA() throws Exception
	{
        AetherContainer container = new AetherContainer();

		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		dcp.setDefaultConnection(conn);
		container.add(dcp);

		PublisherProvider pub = new PublisherProvider();
		container.add(pub);

		MonitorProvider mon = new MonitorProvider();
		container.addService(Monitor.class, mon);

		ThreadPoolProvider tpp = new ThreadPoolProvider();
		container.add(tpp);

		// before we add the world manager, add the domain agent
		DomainAgent da = new DomainAgent();
		da.setDomainTopic("aether://cs.columbia.edu/domain");
		DomainInfo di = new DomainInfo();
		di.setAuthority("buko@cs.columbia.edu");
		di.setDomainName("Columbia CS Dept");
		da.setDomainInfo(di);
		container.add(da);

		while (true) Thread.yield();
	}
}
