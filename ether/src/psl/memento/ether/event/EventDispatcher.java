package psl.memento.ether.event;

import EDU.oswego.cs.dl.util.concurrent.*;

/**
 * Object responsible for dispatching incoming events to the subscribed
 * event handlers.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */

class EventDispatcher
{
   private EventHandlerRegistry handlerRegistry;
   private PooledExecutor threadpool;

   /**
	 * Construct a new EventDispatcher.
	 *
	 * @param ehr EventHandlerRegistry which tracks subscriptions
	 */
   public EventDispatcher(EventHandlerRegistry ehr)
	{
		if (ehr == null)
		{
			String msg = "ehr can't be null";
			throw new IllegalArgumentException(msg);
		}

      this.handlerRegistry = ehr;

      // construct a new threadpool with 5 dispatch threads that live forever
      threadpool = new PooledExecutor(new LinkedQueue());
      threadpool.setKeepAliveTime(-1);
      threadpool.createThreads(5);
	}

   /**
	 * Dispatch an event to all handlers subsribed to a given topic.
	 *
	 * @param event event to dispatch to the subscribed handlers
	 */
   public void dispatch(Event event)
	{
		if (event == null)
		{
			String msg = "event can't be null";
			throw new IllegalArgumentException(msg);
		}

      // create a new DispatchEventCommand put it on the threadpool queue
      try
		{
			threadpool.execute(new DispatchEventCommand(event));
		}
		catch (InterruptedException ie)
		{
         // should never happen
			ie.printStackTrace();
         System.exit(-1);
		}
	}

	/**
	 * Stop the dispatcher after it has dispatched all receieved events.
	 */
	public void stop()
	{
		threadpool.shutdownAfterProcessingCurrentlyQueuedTasks();
	}

   /**
	 * An internal Runnable class for use with the Threadpool. Does the actual
	 * dispatching of events.
	 *
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 */
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
			this.dispatch(event);
		}

		/**
		 * Dispatch an event by asking all handlers to to handle the event.
		 *
		 * @param event event to dispatch
		 **/
		private void dispatch(Event event)
		{
			EventHandler[] handlers =
				handlerRegistry.getHandlers(event.getTopic());

			for (int i = 0; i < handlers.length; ++i)
			{
				handlers[i].handleEvent(event);
			}
		}
	} // class DispatchEventCommand
}

