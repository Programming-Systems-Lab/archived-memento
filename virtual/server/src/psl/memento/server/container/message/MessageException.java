package psl.memento.server.container.message;

/**
 * General exception concerning the messaging system.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class MessageException extends Exception
{
	public MessageException()
	{
		super();
	}
	
	public MessageException(String msg)
	{
		super(msg);
	}
	
	public MessageException(String msg, Throwable t)
	{
		super(msg, t);
	}
	
	public MessageException(Throwable t)
	{
		super(t);
	}

}
