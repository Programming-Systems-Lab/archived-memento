package aether.event;

/**
 * Implements a generic exception within the Aether framework.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class EventException extends Exception
{
	public EventException()
	{
		super();
	}

	public EventException(String s)
	{
		super(s);
	}

	public EventException(String s, Throwable t)
	{
		super(s, t);
	}

	public EventException(Throwable t)
	{
		super(t);
	}
}
