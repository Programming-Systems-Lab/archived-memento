package memento.world.manager;

import memento.world.model.WorldModel;
import memento.world.model.WorldAdvertisement;
import aether.net.Monitor;
import aether.net.Connection;
import aether.server.domain.Advertisement;
import aether.server.ThreadPool;
import aether.event.EventHandler;
import aether.event.Event;
import aether.event.Notice;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.framework.Startable;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.DefaultComponent;

import java.io.IOException;


/**
 * A WorldController handles all incoming requests to modify a world model.
 * It receives incoming client request events (over Elvin) and it translates
 * the request events into a series of commands that're executed against the
 * world model.
 *
 * TODO: test this class
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldController extends DefaultComponent
		implements Initializable, Disposable, Startable
{
	private WorldModel worldModel;
	private Monitor monitor;
	private Connection connection;
	private ThreadPool threadpool;
	private EventHandler handler;

	public void initialize() throws ComponentException
	{
		this.connection = (Connection)
				requireService(this, this, Connection.class, null, this);

		this.monitor = (Monitor)
				requireService(this, this, Monitor.class, connection, this);

		this.threadpool = (ThreadPool)
				requireService(this, this, ThreadPool.class, null, this);
	}

	public synchronized void dispose() throws ComponentException
	{
		getContainer().releaseService(this, this, monitor);
		monitor = null;

		// release our resources
		getContainer().releaseService(this, this, connection);
		connection = null;

		getContainer().releaseService(this, this, threadpool);
		threadpool = null;

		// nullify our state
		worldModel = null;
	}

	public void start() throws ComponentException
	{
		// add a new listener to the monitor
		handler = new ControllerEventHandler();
		monitor.addNoticeListener(handler);

		// tell the monitor to begin watching the world's topic
		try
		{
			monitor.subscribe((String)
					worldModel.getAdvertisement()
					.get(WorldAdvertisement.RequestTopic));
		}
		catch (IOException ioe)
		{
			String msg = "controller couldn't subscribe to world request " +
					"topic";
			throw new ComponentException(msg, ioe);
		}
	}

	public void stop() throws ComponentException
	{
		monitor.removeNoticeListener(handler);
		handler = null;

		try
		{
			monitor.unsubscribe(WorldAdvertisement.RequestTopic);
		}
		catch (IOException ioe)
		{
			String msg = "couldn't unsubscribe from world request topic";
			throw new ComponentException(msg, ioe);
		}
	}


	public WorldModel getWorldModel()
	{
		return worldModel;
	}

	public void setWorldModel(WorldModel worldModel)
	{
		this.worldModel = worldModel;
	}

	/**
	 * Special event handler that queues the processing of incoming events so
	 * the notification thread is not kept busy.
	 */
	private class ControllerEventHandler implements EventHandler
	{
		public void handle(Event event)
		{
			threadpool.execute(new ControllerExecutor(worldModel,
													  (Notice) event));
		}
	}

	/**
	 * Special class that does the actual processing of events.
	 */
	private class ControllerExecutor implements Runnable
	{
		private Notice notice;
		private WorldModel worldModel;

		public ControllerExecutor(WorldModel model, Notice notice)
		{
			this.worldModel = model;
			this.notice = notice;
		}

		public void run()
		{
			// process the incoming event that was sent to the world
			// TODO: implement this method
		}
	}

}
