package psl.memento.server.util.concurrent;

/**
 * Indicates that an operation has timed out.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class TimeoutException extends Exception
{
	public TimeoutException()
	{
		super();
	}
	
	public TimeoutException(String msg)
	{
		super(msg);
	}
	
	public TimeoutException(String msg, Throwable t)
	{
		super(msg, t);
	}
	
	public TimeoutException(Throwable t)
	{
		super(t);
	}

}
