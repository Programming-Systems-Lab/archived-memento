package psl.memento.server.container.event;

import EDU.oswego.cs.dl.util.concurrent.*;

/**
 * A threadpool responsible for dispatching incoming events to the proper
 * event handlers.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class EventDispatcher 
{
	private EventHandlerManager handlerManager;	
	private PooledExecutor pool;
	
	/**
	 * Construct a new EventDispatcher responsible for dispatching all incoming
	 * events to the registered handlers.
	 * 
	 * @param ehm EventHandlerManager which manages the handler subscriptions
	 **/
	public EventDispatcher(EventHandlerManager ehm)
	{
		if (ehm == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.handlerManager = ehm;
		
		// construct the new thread pool with 5 dispatch threads living forever
		pool = new PooledExecutor(new LinkedQueue());
		pool.setKeepAliveTime(-1);
		pool.createThreads(5);
	}
	
	/**
	 * Dispatch an event to all handlers subscribed to the topic which the 
	 * event was published on.
	 * 
	 * @param event event to dispatch to all internal handlers
	 **/
	public void dispatch(Event event)
	{
		if (event == null)
		{
			String msg = "event cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		// create a new DispatchEventCommand and put it on the queue
		try
		{
			pool.execute(new DispatchEventCommand(event));
		}
		catch (InterruptedException ie)
		{
			// should never happen
			ie.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * DispatchEventCommands are the runnable objects placed on the threadpool
	 * queue which actually remove events from the queue and dispatch them to
	 * all subscribed handlers.
	 * 
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 **/
	private class DispatchEventCommand implements Runnable
	{
		private Event event;
		
		/**
		 * Construct a new command to dispatch a single event. 
		 * 
		 * @param event to dispatch
		 **/
		public DispatchEventCommand(Event event)
		{
			this.event = event;
		}
		
		/**
		 * Actually perform the dispatching of events on an event dispatcher
		 * pooled thread.
		 **/
		public void run()
		{
			// get the next event from the queue and dispatch it
			dispatch(event);
		}
			
		/**
		 * Dispatch an event by asking all handlers to to handle the event.
		 * 
		 * @param event event to dispatch
		 **/
		private void dispatch(Event event)
		{
			EventHandler[] handlers = handlerManager.getHandlers(event.getTopic());
			
			for (int i = 0; i < handlers.length; ++i)
			{
				handlers[i].handleEvent(event);
			}
		}
	}

}
