package aether.server.domain;

import aether.AetherTestCase;
import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.Monitor;
import aether.server.AetherContainer;
import aether.server.core.DefaultConnectionProvider;
import aether.server.ManagedPublisher;
import aether.server.DefaultThreadPool;
import memento.world.model.WorldModel;
import memento.world.model.WorldAdvertisement;

/**
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class DomainAgentTest extends AetherTestCase
{
	public void testDomainAgentRemoting() throws Exception
	{
		AetherContainer container = new AetherContainer();

		Connection conn = new DefaultConnection(getElvinHost(), getElvinPort());
		DefaultConnectionProvider dcp = new DefaultConnectionProvider();
		dcp.setDefaultConnection(conn);
		container.add(dcp);

		ManagedPublisher pub = new ManagedPublisher();
		container.add(pub);

		MonitorProvider mon = new MonitorProvider();
		container.addService(Monitor.class, mon);

		DefaultThreadPool tpp = new DefaultThreadPool();
		container.add(tpp);

		// before we add the world manager, add the domain agent
		DomainAgent da = new DomainAgent();
		da.setDomainTopic("aether://cs.columbia.edu/domain");
		DomainInfo di = new DomainInfo();
		di.setAuthority("buko@cs.columbia.edu");
		di.setDomainName("Columbia CS Dept");
		da.setDomainInfo(di);
		container.add(da);


		WorldManager worldMan = new DefaultWorldManager();
		container.add(worldMan);

		WorldModel model = new WorldModel();
		Advertisement adv = new Advertisement();
		adv.set(WorldAdvertisement.RequestTopic, "aether://mcbain/830?request");
		adv.set(WorldAdvertisement.ModelTopic, "aether://mcbain/830/objcts/323524352354");
		adv.set("replication-priority", "3");
		model.setAdvertisement(adv);
		container.add(model);

        while (true);
	}
}
