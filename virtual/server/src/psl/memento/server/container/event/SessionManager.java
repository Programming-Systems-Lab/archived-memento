package psl.memento.server.container.event;

import java.util.HashMap;
import java.util.Map;

import psl.memento.server.util.Uid;

/**
 * Responsible for creating, destroying and persisting Sessions.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class SessionManager
{
	private Map sessions = new HashMap();
	
	/**
	 * Retrieve a Session for a given client. A Session may be destroyed at any
	 * time to conserve memory or if it times out so truly persistent values
	 * should be cached and not stored in the session. 
	 * 
	 * @param entity entity id of the entity to retrieve the session for
	 **/
	public synchronized Session getSession(Uid entity)
	{
		if (entity == null)
		{
			String msg = "entity can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (sessions.containsKey(entity))
		{
			return (Session) sessions.get(entity);
		}
		else
		{
			Session sess = new Session(entity);
			sessions.put(entity, sess);
			return sess;
		}
	}
}
