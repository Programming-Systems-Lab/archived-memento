package memento.world.manager;

import aether.AetherTestCase;
import aether.net.DefaultConnection;
import aether.net.Connection;
import aether.net.Monitor;
import aether.server.AetherContainer;
import aether.server.domain.Advertisement;
import aether.server.core.DefaultConnectionProvider;
import aether.server.core.PublisherProvider;
import aether.server.core.MonitorProvider;
import aether.server.core.ThreadPoolProvider;
import memento.world.model.WorldModel;
import memento.world.model.DefaultWorldModel;
import memento.world.model.WorldAdvertisement;

import java.util.Iterator;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldManagerTest extends AetherTestCase
{
    public void testWorldManagerInit() throws Exception
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

        WorldManager worldMan = new DefaultWorldManager();
		container.add(worldMan);

        DefaultWorldModel model = new DefaultWorldModel();
        Advertisement adv = new Advertisement();
		adv.set(WorldAdvertisement.RequestTopic, "aether://mcbain/830?request");
		adv.set(WorldAdvertisement.ModelTopic, "aether://mcbain/830");
        model.setAdvertisement(adv);
		container.add(model);

		// make sure all the components have been deployed
        boolean foundUI = false;
		for (Iterator i = container.iterator(); i.hasNext(); )
		{
            Object child = i.next();
            if (child instanceof WorldUI) { foundUI = true; break; }
		}
		assertTrue(foundUI);
	}
}
