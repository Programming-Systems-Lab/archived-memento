package psl.memento.ether.message;

import psl.memento.ether.event.Event;
import psl.memento.ether.util.Uid;

import java.net.MalformedURLException;

/**
 * Represents a Message that one component can send directly to another
 * component.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Message
{
   private MessageUrl sender;
	private MessageUrl receiver;
   private Event event;
	private Uid transactionId;

   /**
	 * Construct a new Message with the underlying event that contains the
	 * message data.
	 *
	 * @param event underlying Event used to transport the message data
	 */
	Message(Event event)
	{
		if (event == null)
		{
			String msg = "event can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.event = event;
	}

	/**
	 * Get the underlying event inside the Message.
	 *
	 * @return underlying Event used to transport the message
	 */
   Event getUnderlyingEvent()
	{
		return event;
	}

   /**
	 * Get the message URL of the component which sent this message.
	 *
	 * @return message url of the component which sent this message
	 */
	public MessageUrl getSender()
	{
      if (sender == null)
		{
         try
			{
				sender = new MessageUrl(get("message.sender.message-url"));
			}
			catch (MalformedURLException mue)
			{
				String msg = "illegal sender message url";
				throw new IllegalStateException(msg);
			}
		}

		return sender;
	}

	/**
	 * Set the message URL of the component which sent this message.
	 *
	 * @param sender URL of the component which sent this event
	 */
	public void setSender(MessageUrl sender)
	{
      if (sender == null)
		{
			String msg = "sender can't be null";
			throw new IllegalArgumentException(msg);
		}

      put("message.sender.message-url", sender.toUrl());
		this.sender = sender;
	}

	/**
	 * Get the message URL of the component which should receive this message.
	 *
	 * @return message URL of the component which should receive this message
	 */
	public MessageUrl getReceiver()
	{
		if (receiver == null)
		{
			try
			{
				receiver = new MessageUrl(get("message.receiver.message-url"));
			}
			catch (MalformedURLException mue)
			{
				String msg = "bad message receiver url";
				throw new IllegalStateException(msg);
			}
		}

		return receiver;
	}

   /**
	 * Set the message URL of the component which should receive this message.
	 *
	 * @param receiver message URL of the component which should receive this
	 *                 message
	 */
	public void setReceiver(MessageUrl receiver)
	{
		if (receiver == null)
		{
			String msg = "receiver can't be null";
			throw new IllegalArgumentException(msg);
		}

		put("message.receiver.message-url", receiver.toUrl());
		this.receiver = receiver;
	}

	/**
	 * Get the transaction ID of the message if it's part of a message
	 * transaction.
	 *
	 * @return transction ID of the message or <code>null</code>
	 */
	public Uid getTransactionId()
	{
		if (transactionId == null)
		{
			transactionId = new Uid(get("message.transaction-id"));
		}

		return transactionId;
	}

	/**
	 * Set the transaction ID of the message indicating it's part of a
	 * transaction.
	 *
	 * @param txId transaction ID of the message
	 */
	public void setTransactionId(Uid txId)
	{
		if (txId != null)
		{
			this.transactionId = txId;
			put("message.transaction-id", txId.toString());
		}
	}

	/**
	 * Create a response to this message. If a received essage is part of a
	 * transaction this message must be called to copy the transaction metadata
	 * into the response message.
	 *
	 * @return empty Message which should be sent as a response to this message
	 */
	public Message createResponse()
	{
		Message response = MessageService.getInstance().newMessage();

		// copy the transaction ID over to the response
		response.setTransactionId(transactionId);

		return response;
	}

   /**
	 * Retrieve a named value from the message.
	 *
	 * @param key name of the value to retreieve from the message
	 * @return value mapped to <code>key</code> or <code>null</code>
	 */
	public String get(String key)
	{
		return event.getString(key);
	}

	/**
	 * Store a named key,value pair in the message.
	 *
	 * @param key name of the value to store in the message
	 * @param val value to be mapped to <code>key</code>
	 */
	public void put(String key, String val)
	{
		event.put(key, val);
	}
}
