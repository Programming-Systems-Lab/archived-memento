package psl.memento.server.container.event;

/**
 * Indicates a general error in the event subsystem.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class EventException extends Exception
{
	public EventException()
	{
		super();
	}
	
	public EventException(String msg)
	{
		super(msg);
	}
	
	public EventException(String msg, Throwable t)
	{
		super(msg, t);
	}
	
	public EventException(Throwable t)
	{
		super(t);
	}

}
