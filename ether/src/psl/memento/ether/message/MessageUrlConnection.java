package psl.memento.ether.message;

import psl.memento.ether.event.ComponentUrl;

/**
 * Represents a connection to a MessageUrl. Using this class, components can
 * send messages directly to remote components.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class MessageUrlConnection
{
	private MessageService msgService = MessageService.getInstance();
	private MessageUrl destAddress;
	private ComponentUrl source;
	private boolean connected = false;

	/**
	 * Construct a new MessageUrlConnection to connect to a remote component
	 * so that messages may be sent directly to that component.
	 *
	 * @param destAddress address of the component to connect to
	 * @param source      URL of the component sending the messages
	 */
	public MessageUrlConnection(MessageUrl destAddress, ComponentUrl source)
	{
		if ((destAddress == null) || (source == null))
		{
			String msg = "no param can be null";
			throw new IllegalArgumentException(msg);
		}

      this.destAddress = destAddress;
		this.source = source;
	}

	/**
	 * Open the connection to the remote component.
	 *
	 * @throws MessageException
	 *         if the connection couldn't be opened
	 */
	public void open() throws MessageException
	{
		if (connected)
		{
			String msg = "connection already open";
			throw new IllegalStateException(msg);
		}

		msgService.openConnection(destAddress, source);
		connected = true;
	}

	/**
	 * Close the connection to the remote component.
	 */
	public void close()
	{
		if (!connected)
		{
			String msg = "connection already closed";
			throw new IllegalStateException(msg);
		}

		msgService.closeConnection(destAddress);
	}

	/**
	 * Send a message over the connection.
	 *
	 * @param msg Message to send
	 * @throws MessageException
	 *         if the message couldn't be sent
	 */
	public void send(Message msg) throws MessageException
	{
		msgService.send(msg, destAddress, source);
	}

	/**
	 * Send a message over the connection which is part of a message transaction
	 * and register a callback to handle the eventual message response.
	 *
	 * @param msg      Message to send
	 * @param callback calback to handle the message response
	 * @throws MessageException
	 *         if the message couldn't be sent
	 */
	public void send(Message msg, Callback callback) throws MessageException
	{
		msgService.send(msg, destAddress, source, callback);
	}

	/**
	 * Register a MessageHandler to receive messages sent to a given
	 * component. The component must be a local component hosted within the
	 * current container.
	 *
	 * @param compUrl URL of the component to register for
	 * @param handler MessageHandler handler of the component for
	 */
	public void registerLocalHandler(ComponentUrl compUrl,
												MessageHandler handler)
	{
		msgService.getHandlerRegistry().register(compUrl, handler);
	}

	/**
	 * Unregister a MessageHandler so that it no longer receives messages
	 * sent to a given component.
	 *
	 * @param compUrl URL of the component to unregister for
	 * @param handler handler to unregister
	 */
	public void unregisterLocalHandler(ComponentUrl compUrl,
												  MessageHandler handler)
	{
      msgService.getHandlerRegistry().unregister(compUrl, handler);
	}

	/**
	 * Construct a new, empty message.
	 *
	 * @return new, empty message
	 */
	public Message newMessage()
	{
		return msgService.newMessage();
	}
}