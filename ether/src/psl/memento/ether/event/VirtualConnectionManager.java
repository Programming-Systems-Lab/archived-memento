package psl.memento.ether.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the virtual connections to many different event hubs within the
 * network.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class VirtualConnectionManager
{
	private Map connectionMap = Collections.synchronizedMap(new HashMap());

	/**
	 * Determine if there is a virtual connection open to a specific server.
	 *
	 * @param host host of the event hub
	 * @param port port of the event hub
	 * @return <code>true</code> if there is a connection open to the given
	 *         server
	 **/
	public boolean isConnected(String host, int port)
	{
		if (host == null)
		{
			String msg = "host can't be null";
			throw new IllegalArgumentException(msg);
		}

		return connectionMap.containsKey(makeServerKey(host, port));
	}

	/**
	 * Open a virtual connection to an event hub.
	 *
	 * @param host host of the event hub
	 * @param port port of the event hub
	 **/
	public void openConnection(String host, int port)
	{
		if (host == null)
		{
			String msg = "host can't be null";
			throw new IllegalArgumentException(msg);
		}

		String serverKey = makeServerKey(host, port);

		if (connectionMap.containsKey(serverKey))
		{
			Integer connCount = (Integer) connectionMap.get(serverKey);
			connCount = new Integer(connCount.intValue() + 1);
			connectionMap.put(serverKey, connCount);
		}
		else
		{
			connectionMap.put(serverKey, new Integer(1));
		}
	}

	/**
	 * Close a virtual connection to an event hub.
	 *
	 * @param host host of the event hub
	 * @param port port of the event hub
	 **/
	public void closeConnection(String host, int port)
	{
		if (host == null)
		{
			String msg = "host can't be null";
			throw new IllegalArgumentException(msg);
		}

		String serverKey = makeServerKey(host, port);

		if (connectionMap.containsKey(serverKey))
		{
			Integer connCount = (Integer) connectionMap.get(serverKey);
			connCount = new Integer(connCount.intValue() - 1);

			if (connCount.intValue() > 0)
			{
				connectionMap.put(serverKey, connCount);
			}
			else
			{
				connectionMap.remove(serverKey);
			}
		}
	}

	/**
	 * Construct a unique key identifying a given EventHub.
	 *
	 * @param host host of the event hub
	 * @param port port of the event hub
	 * @return key identifying the event hub hosting the topic
	 **/
	private String makeServerKey(String host, int port)
	{
		return host + ":" + port;
	}

}
