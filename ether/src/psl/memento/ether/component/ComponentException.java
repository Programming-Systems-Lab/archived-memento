package psl.memento.ether.component;

/**
 * Represents a general exception with the component framework.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentException extends Exception
{
	public ComponentException()
	{
		super();
	}

	public ComponentException(String msg)
	{
		super(msg);
	}

	public ComponentException(String msg, Throwable t)
	{
		super(msg, t);
	}

	public ComponentException(Throwable t)
	{
		super(t);
	}
}
