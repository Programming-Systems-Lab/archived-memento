package psl.memento.ether.message;

import psl.memento.ether.event.Event;
import psl.memento.ether.event.EventHandler;

/**
 * Class responsible for dispatching incoming messages to registered
 * message handlers.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class MessageDispatcher implements EventHandler
{
	private MessageHandlerRegistry registry;

	/**
	 * Construct a new MessageDispatcher to dispatch incoming messages.
	 *
	 * @param registry MessageHandlerRegistry for registered message handlers
	 */
	public MessageDispatcher(MessageHandlerRegistry registry)
	{
		if (registry == null)
		{
			String msg = "registry can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.registry = registry;
	}

   /**
	 * Construct a Message wrapper around a given event/message and dispatch
	 * it.
	 *
	 * @param event event containing the message data
	 */
	public void handleEvent(Event event)
	{
		if (event == null)
		{
			String msg = "event can't be null";
			throw new IllegalArgumentException(msg);
		}

      Message msg = new Message(event);

		// if a message is received containing a transaction ID, first see if
		// there's a callback waiting to handle this message response
		if (msg.getTransactionId() != null)
		{
			Callback callback = this.registry.remove(msg.getTransactionId());
			if (callback != null)
			{
				callback.handleMessage(msg);
				return;
			}
		}

		// dispatch the message to all handlers registered to the component
		// url
		MessageHandler[] handlers =
				  registry.getHandlers(msg.getReceiver().getComponentUrl());

		for (int i = 0; i < handlers.length; ++i)
		{
			handlers[i].handleMessage(msg);
		}
	}
}
