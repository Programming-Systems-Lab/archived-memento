package psl.memento.ether.event.session;

import psl.memento.ether.event.ComponentUrl;

/**
 * A SessionProvider is responsible for creating, managing and destroying all
 * the sessions within a container. It's also for timing out sessions and
 * freeing up resources associated with stale sessions.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface SessionProvider
{
	/**
	 * Retrieve a Session object associated with the given component. This
	 * method is called everytime a component generates an event so its
	 * frequency can be used to determine session staleness.
	 *
	 * @param compUrl URL of the component to retrieve the session for
	 * @return Session associated with the given component
	 */
	public Session getSession(ComponentUrl compUrl);
}
