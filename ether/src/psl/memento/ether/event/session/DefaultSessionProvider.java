package psl.memento.ether.event.session;

import psl.memento.ether.event.ComponentUrl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple, in-memory session provider.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class DefaultSessionProvider
{
	private Map sessMap = Collections.synchronizedMap(new HashMap());
	private long timeout = 10 * 60 * 1000;

	/**
	 * Get the map for the given component.
	 *
	 * @param compUrl URL of the component to retrieve the session for
	 * @return Session for component with the given url
	 */
	public Session getSession(ComponentUrl compUrl)
	{
		if (compUrl == null)
		{
			String msg = "compUrl can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (sessMap.containsKey(compUrl))
		{
			return ((SessionInfo) sessMap.get(compUrl)).session;
		}
		else
		{
			SessionInfo si = new SessionInfo(new Session(compUrl));
			sessMap.put(compUrl, si);
			return si.session;
		}
	}

	/**
	 * Remove all sessions which have timed out.
	 */
	private synchronized void removeTimeouts()
	{
		// loop over the sessions and see which ones have timed out
		Iterator keyIter = sessMap.keySet().iterator();
		while (keyIter.hasNext())
		{
			Object key = keyIter.next();
			SessionInfo si = (SessionInfo) sessMap.get(key);

			if (si.lastAccessTime + timeout >= System.currentTimeMillis())
			{
				sessMap.remove(key);
			}
		}
	}

	/**
	 * Helper class for keeping track of the age of sessions.
	 *
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 */
	private class SessionInfo
	{
		public long lastAccessTime = -1;
		public Session session;

		public SessionInfo(Session sess)
		{
			lastAccessTime = System.currentTimeMillis();
			this.session = sess;
		}
	}

}
