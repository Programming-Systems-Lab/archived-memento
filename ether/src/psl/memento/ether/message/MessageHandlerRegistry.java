package psl.memento.ether.message;

import psl.memento.ether.event.ComponentUrl;
import psl.memento.ether.util.Uid;

import java.util.*;

/**
 * Object responsible for keeping track of MessageHandlers subscribed to
 * different components as well as callbacks involved in message transactions.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class MessageHandlerRegistry
{
	private Map addressMap = Collections.synchronizedMap(new HashMap());
	private Map msgTransactions = Collections.synchronizedMap(new HashMap());

	/**
	 * Register a MessageHandler to handle messages sent to a specific component.
	 *
	 * @param componentUrl url for the component to handle messages for, it must
	 *                     be a local component
	 * @param handler     handler to handle the incoming messages
	 */
	public void register(ComponentUrl componentUrl, MessageHandler handler)
	{
		if ((componentUrl == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		if (addressMap.containsKey(componentUrl))
		{
			Collection handlerCol = (Collection) addressMap.get(componentUrl);
			handlerCol.add(handler);
		}
		else
		{
			Collection handlerCol = Collections.synchronizedList(new ArrayList());
			handlerCol.add(handler);
			addressMap.put(componentUrl, handlerCol);
		}
	}

	/**
	 * Unregister a MessageHandler for handling incoming messages.
	 *
	 * @param compUrl url for the component to remove the handler for
	 * @param handler handler to unregister from receiving messages
	 */
	public void unregister(ComponentUrl componentUrl, MessageHandler handler)
	{
		if ((componentUrl == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		Collection handlerCol = (Collection) addressMap.get(componentUrl);
		if (handlerCol != null)
		{
			handlerCol.remove(handler);

			// remove the collection if it's empty
			if (handlerCol.isEmpty())
			{
				addressMap.remove(componentUrl);
			}
		}
	}

	/**
	 * Get all the MessageHandlers registered to receive messages for a given
	 * component.
	 *
	 * @param compUrl URL of the component to retrieve the handlers for
	 * @return array of MessageHandlers receiving messages for a given
	 *         component
	 */
	public MessageHandler[] getHandlers(ComponentUrl compUrl)
	{
		if (compUrl == null)
		{
			String msg = "compUrl can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (addressMap.containsKey(compUrl))
		{
			Collection handlerCol = (Collection) addressMap.get(compUrl);
			return (MessageHandler[]) handlerCol.toArray(new MessageHandler[0]);
		}
		else
		{
			return new MessageHandler[0];
		}
	}

	/**
	 * Register a Callback that should be matched to a specific
	 * message transaction.
	 * <p>
	 * Currently, only a single Callback is allowed to register for each
	 * message transaction.
	 *
	 * @param txID      unique ID of the message transaction the callback is
	 *                  listening to
	 * @param callback Callback to register
	 */
	public void register(Uid txId, Callback callback)
	{
		if ((txId == null) || (callback == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		// track what time the transaction was started
		TransactionInfo txInfo =
				new TransactionInfo(System.currentTimeMillis(), callback);
		msgTransactions.put(txId, txInfo);
	}

	/**
	 * Remove and retrieve the Callback instance waiting on a specific
	 * message transaction.
	 *
	 * @param txId unique ID of the message transaction
	 * @return Callback waiting on the local end of the transaction or
	 *         <code>null</code>
	 */
	public Callback remove(Uid txId)
	{
		if (txId == null)
		{
			String msg = "txId can't be null";
			throw new IllegalArgumentException(msg);
		}

		TransactionInfo txInfo = (TransactionInfo) msgTransactions.remove(txId);
		return txInfo.callback;
	}

	/**
	 * A small basic class which keeps information about message transactions.
	 *
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 */
	private class TransactionInfo
	{
		/**
		 * the time the message initiating/continuing the transaction was started
		 **/
		public long startTime = 0;

		/**
		 * Callback to handle the transaction response.
		 */
		public Callback callback;

		/**
		 * Construct a new transaction info object to track a given
		 * transaction.
		 *
		 * @param startTime time the transaction was started/continued
		 * @param callback  callback to handle the response
		 */
		public TransactionInfo(long startTime, Callback callback)
		{
			this.startTime = startTime;
			this.callback = callback;
		}
	}
}
