package memento.world.manager;

import aether.net.Publisher;
import aether.server.domain.Advertisement;
import memento.world.model.*;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Startable;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.DefaultComponent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A WorldView is responsible for monitoring changes in a WorldModel and
 * broadcasting them back out over Elvin so remote clients can update their
 * local copies of the world.
 *
 * TODO: test this class
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldView extends DefaultComponent
		implements Initializable, Disposable, Startable
{
    private Publisher publisher;
    private WorldModel worldModel;
    private WorldModelListener worldListener;


	public void initialize() throws ComponentException
	{
        this.publisher = (Publisher)
				requireService(this, this, Publisher.class, null, this);
	}

	public void dispose() throws ComponentException
	{
        getContainer().releaseService(this, this, publisher);
		publisher = null;

		worldModel = null;
	}

	public void start() throws ComponentException
	{
     	worldListener = new WorldViewListener();
        worldModel.addWorldModelListener(worldListener);
	}

	public void stop() throws ComponentException
	{
        worldModel.removeWorldModelListener(worldListener);
		worldListener = null;
	}


	public WorldModel getWorldModel()
	{
		return worldModel;
	}

	public void setWorldModel(WorldModel worldModel)
	{
		this.worldModel = worldModel;
	}

	private class WorldViewListener
			implements WorldModelListener, SectorListener,
			PropertyChangeListener
	{
		public void objectCreated(WorldModelEvent wme)
		{
            // depending on the type of object, we want to subscribe to it
			// and broadcast all of its events over elvin
		}

		public void objectDestroyed(WorldModelEvent wme)
		{
			// unsubscribe all of our listeners from it
		}

		public void entered(SectorEvent se)
		{
		}

		public void exited(SectorEvent se)
		{
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
		}

		public void objectEntered(WorldModelEvent wme)
		{
		}

		public void objectExited(WorldModelEvent wme)
		{
		}
	}
}
