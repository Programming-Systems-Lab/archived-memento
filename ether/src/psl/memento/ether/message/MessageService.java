package psl.memento.ether.message;

import psl.memento.ether.event.ComponentUrl;
import psl.memento.ether.event.EventException;
import psl.memento.ether.event.TopicUrl;
import psl.memento.ether.event.TopicUrlConnection;
import psl.memento.ether.util.CounterMap;
import psl.memento.ether.util.Uid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Sends Messages to to specific components distributed within the network
 * and routes incoming messages.
 * <p>
 * Note that because the MessageService depends on a working EventService it
 * <em>must</em> be started after an EventService instance has been started.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class MessageService
{
	// connection and topic used to receive messages from the messaging topic
	private TopicUrlConnection msgTopicConn;
	private TopicUrl msgTopicUrl;

	private MessageDispatcher dispatcher;
	private MessageHandlerRegistry handlerRegistry =
			new MessageHandlerRegistry();
	private Map connMap = Collections.synchronizedMap(new HashMap());
	private CounterMap connCounter = new CounterMap();

	private static MessageService singleton = null;

	/**
	 * Construct a new MessageService.
	 *
	 * @param msgTopic Topic used to receive incoming messages
	 * @throws MessageException
	 *         if the message service can't be started
	 */
	public MessageService(TopicUrl msgTopic) throws MessageException
	{
		if (msgTopic == null)
		{
			String msg = "no param can be null";
			throw new IllegalArgumentException(msg);
		}

		this.msgTopicUrl = msgTopic;

		// create the dispatcher
		dispatcher = new MessageDispatcher(handlerRegistry);
	}

	/**
	 * Start the message service and open the necessary connections.
	 *
	 * @throws MessageException
	 *         if the service can't be started
	 */
	public void start() throws MessageException
	{
		// open a connection to the message topic
		msgTopicConn = new TopicUrlConnection(msgTopicUrl, null);
		try
		{
			msgTopicConn.open();

			// subscribe the message dispatcher
			msgTopicConn.subscribe(dispatcher);
		}
		catch (EventException ee)
		{
			String msg = "couldn't open connection to msg topic " +
					msgTopicUrl.toString();
			throw new MessageException(msg, ee);
		}
	}

	/**
	 * Stop the MessageService.
	 */
	public void stop()
	{
		msgTopicConn.close();
	}

	/**
	 * Get the singleton MessageService instance.
	 *
	 * @return singleton MessageService instance
	 */
	public static MessageService getInstance()
	{
		return singleton;
	}

	/**
	 * Set the singleton MessageService. This method should only be called by
	 * the Container.
	 *
	 * @param msgService singleton MessageService for a container
	 */
	public static void setInstance(MessageService msgService)
	{
		if (msgService == null)
		{
			String msg = "msgService can't be null";
			throw new IllegalArgumentException(msg);
		}

		singleton = msgService;
	}

	/**
	 * Open a message connection to a component at the given address.
	 *
	 * @param address message url of the component to open a connection to
	 * @param source  url of the Component using this connection to send messages
	 * @throws MessageException
	 *         if the connection couldn't be opened
	 */
	void openConnection(MessageUrl address, ComponentUrl source)
			throws MessageException
	{
		if ((address == null) || (source == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		// if there are no open connections to this component then make one
		if (connCounter.getCount(address) < 1)
		{
			// make a real connection to the destination component's messaging
			// topic
			TopicUrlConnection topicCon = null;
			try
			{
				topicCon =
						new TopicUrlConnection(address.getMessageTopicUrl(), source);
				topicCon.open();
			}
			catch (EventException ee)
			{
				String msg = "couldn't open connection to " + address;
				throw new MessageException(msg);
			}
			finally
			{
				if (topicCon != null)
				{
					topicCon.close();
				}
			}

			// store the connection
			connMap.put(address, topicCon);
		}

		// increment the connection counter
		connCounter.increment(address);
	}

	/**
	 * Close a connection to a remote component.
	 *
	 * @param address message url of the remote component to close the connection
	 *                to
	 */
	void closeConnection(MessageUrl address)
	{
		if (address == null)
		{
			String msg = "address can't be null";
			throw new IllegalArgumentException(msg);
		}

		// if there is a single connection left to this address close the real
		// one
		if (connCounter.getCount(address) == 1)
		{
			TopicUrlConnection conn = (TopicUrlConnection) connMap.get(address);
			conn.close();
		}

		// decrement the connection count
		connCounter.decrement(address);
	}

	/**
	 * Send a Message to a component at the given address.
	 *
	 * @param msg      Message to send
	 * @param address  message url of the receiver
	 * @param sender   ComponentUrl of the component sending this message
	 * @throws MessageException
	 *         if the message can't be sent
	 */
	void send(Message msg, MessageUrl address, ComponentUrl sender)
			throws MessageException
	{
		if ((msg == null) || (address == null) || (sender == null))
		{
			String emsg = "no parameter can be null";
			throw new IllegalArgumentException(emsg);
		}

		// set the receiver of the message
		msg.setReceiver(address);

		// construct the message url for the component sending it
		msg.setSender(new MessageUrl(msgTopicUrl, sender));

		// make sure a connection exists to this remote component
		if (connCounter.getCount(address) < 1)
		{
			String emsg = "no connection exists to " + address;
			throw new IllegalStateException(emsg);
		}

		// send the message to the correct topic
		TopicUrlConnection conn = null;
		try
		{
			conn = (TopicUrlConnection) connMap.get(address);
			conn.publish(msg.getUnderlyingEvent(), sender);
		}
		catch (EventException ee)
		{
			String emsg = "couldn't send message to " + address;
			throw new MessageException(emsg);
		}
	}

	/**
	 * Send a message which is part of a transaction requiring a callback. Note
	 * that when a message is sent containing a transaction ID it is the
	 * responsibility of the remote container and receiver to ensure that the
	 * message's response message contains the same transaction ID.
	 *
	 * @param msg      Message to send
	 * @param address  address to send the message to
	 * @param url      URL of the component sending the message
	 * @param callback Callback to handle the eventual response to the message
	 */
	void send(Message msg, MessageUrl address, ComponentUrl sender,
			  Callback callback) throws MessageException
	{
		// set the transaction id if the message doesn't already have one
		// indicating this is the start of a new message transaction
		if (msg.getTransactionId() == null)
		{
			Uid txId = new Uid();
			msg.setTransactionId(txId);
		}

		// first try to send the message normally
		send(msg, address, sender);

		// register the callback
		handlerRegistry.register(msg.getTransactionId(), callback);
	}

	/**
	 * Get the message handler registry used to register message handlers.
	 *
	 * @return MessageHandlerRegistry to rgister handlers
	 */
	MessageHandlerRegistry getHandlerRegistry()
	{
		return handlerRegistry;
	}

	/**
	 * Construct a new, empty message.
	 *
	 * @return new empty message
	 */
	public Message newMessage()
	{
		return new Message(this.msgTopicConn.newEvent());
	}
}
