package aether.util;

import java.net.InetAddress;

/**
 * Generates globally unique hex-escaped Strings of length 32 characters.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 **/
public class GuidFactory
{
    private static final int ip;

	static
	{
		int ipAddr = 0;
        try
		{
            ipAddr = BytesHelper
					.toInt(InetAddress.getLocalHost().getAddress());
		}
		catch (Exception e)
		{
            ipAddr = 0;
		}

		ip = ipAddr;
	}

	private static short counter = (short) 0;
	private static final int jvm = (int) ( System.currentTimeMillis() >>> 8 );
    private static final String sep = "";

	/**
	 * Unique across JVMs on this machine (unless they load this class
	 * in the same quater second - very unlikely)
	 */
	private static int getJVM()
	{
		return jvm;
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there
	 * are > Short.MAX_VALUE instances created in a millisecond)
	 */
	private static short getCount()
	{
		synchronized(GuidFactory.class)
		{
			if (counter<0) counter=0;
			return counter++;
		}
	}

	/**
	 * Unique in a local network
	 */
	private static int getIP()
	{
		return ip;
	}

	/**
	 * Unique down to millisecond
	 */
	private static short getHiTime()
	{
		return (short) ( System.currentTimeMillis() >>> 32 );
	}

	private static int getLoTime()
	{
		return (int) System.currentTimeMillis();
	}

	private static String format(int intval)
	{
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace( 8-formatted.length(), 8, formatted );
		return buf.toString();
	}

	private static String format(short shortval)
	{
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace( 4-formatted.length(), 4, formatted );
		return buf.toString();
	}

	public static String createId()
	{
		return new StringBuffer(36)
				.append( format( getIP() ) ).append(sep)
				.append( format( getJVM() ) ).append(sep)
				.append( format( getHiTime() ) ).append(sep)
				.append( format( getLoTime() ) ).append(sep)
				.append( format( getCount() ) )
				.toString();
	}

	public static void main(String[] args) throws Exception
	{
		for (int i = 0; i < 20; ++i)
		{
			System.out.println("Guid: " + createId() );
		}
	}
}