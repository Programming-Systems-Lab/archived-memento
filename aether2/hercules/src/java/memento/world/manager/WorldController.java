package memento.world.manager;

import aether.event.Event;
import aether.event.EventHandler;
import aether.event.Notice;
import aether.net.MulticastSocket;
import aether.server.ThreadPool;
import memento.world.model.WorldAdvertisement;
import memento.world.model.WorldModel;

import java.io.IOException;


/**
 * A WorldController handles all incoming requests to modify a world model.
 * It receives incoming client request events and it translates
 * the request events into a series of commands that're executed against the
 * world model.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class WorldController
{
    private WorldModel worldModel;
    private MulticastSocket multicastSocket;
    private ThreadPool threadpool;
    private EventHandler handler;

    public MulticastSocket getMulticastSocket()
    {
        return multicastSocket;
    }

    public void setMulticastSocket(MulticastSocket multicastSocket)
    {
        this.multicastSocket = multicastSocket;
    }

    public ThreadPool getThreadpool()
    {
        return threadpool;
    }

    public void setThreadpool(ThreadPool threadpool)
    {
        this.threadpool = threadpool;
    }

    public WorldModel getWorldModel()
    {
        return worldModel;
    }

    public void setWorldModel(WorldModel worldModel)
    {
        this.worldModel = worldModel;
    }

    public synchronized void start() throws IOException
    {
        // add a new listener to the multicastSocket
        handler = new EventHandler()
        {
            public void handle(Event event)
            {
                threadpool.execute(new ControllerExecutor(worldModel,
                                                          (Notice) event));
            }
        };

        multicastSocket.open();
        multicastSocket.addEventHandler(handler);

        // tell the multicastSocket to begin watching the world's topic
        multicastSocket.subscribe(
                (String) worldModel.getAdvertisement().
                         get(WorldAdvertisement.RequestTopic));
    }

    public synchronized void stop() throws IOException
    {
        multicastSocket.removeEventHandler(handler);
        handler = null;
        multicastSocket.unsubscribe(WorldAdvertisement.RequestTopic);
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
