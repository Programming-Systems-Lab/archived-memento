package psl.memento.ether.util;

import java.net.*;
import java.rmi.server.UID;
import java.security.SecureRandom;

/**
 * A globally unique identifier.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Uid
{
	private static String localIpAddress;
	private String guid;
		private static SecureRandom randomizer = new SecureRandom();

	/**
	 * Compute the local ip address.
	 **/
	static
	{
		try
		{
			localIpAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException uhe)
		{
			localIpAddress = "localhost" + new SecureRandom().nextDouble();
		}
	}

	/**
	 * Default constructor.
	 **/
	public Uid()
	{
		UID uid = new UID();
		StringBuffer buf = new StringBuffer();
		buf.append(localIpAddress).append(':').append(uid.toString());
		buf.append(":" + randomizer.nextLong());
		guid = buf.toString();
	}

	/**
	 * Construct a UniqueId from an existing string.
	 *
	 * @param guid an existing unique id string
	 **/
	public Uid(String guid)
	{
		this.guid = guid;
	}

	/**
	 * Get the string representation of the UniqueId.
	 *
	 * @return string representation of the unique id
	 **/
	public String toString()
	{
		return guid;
	}

	/**
	 * @see java.lang.Object
	 **/
	public boolean equals(Object o)
	{
		if (o instanceof Uid)
		{
			return guid.equals(((Uid) o).guid);
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see java.lang.Object
	 **/
	public int hashCode()
	{
		return guid.hashCode();
	}
}
