package psl.memento.server.container.persistence;

/**
 * Indicates a general error in the persisence framework.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class PersistenceException extends Exception
{
	public PersistenceException()
	{
		super();
	}
	
	public PersistenceException(String msg)
	{
		super(msg);
	}
	
	public PersistenceException(String msg, Throwable t)
	{
		super(msg, t);
	}
	
	public PersistenceException(Throwable t)
	{
		super(t);
	}

}
