package psl.memento.server.container.persistence;

import java.io.Serializable;

import psl.memento.server.util.Uid;
import psl.memento.server.util.concurrent.*;

/**
 * Indicates an object which may be persisted by the container.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class PersistentObject extends ReadWriteResource 
	implements Serializable
{
	private Uid persistenceID;
	
	/**
	 * Get the unique persistence ID.
	 * 
	 * @return persistence id which identifies this object
	 **/
	public Uid getPersistenceId()
	{
		return persistenceID;
	}
	
	/**
	 * Set the unique persistence ID.
	 * 
	 * @param uid id which identifies this object to the store
	 **/
	void setPersistenceId(Uid uid)
	{
		if (uid == null)
		{
			String msg = "uid cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.persistenceID = uid;
	}
}
