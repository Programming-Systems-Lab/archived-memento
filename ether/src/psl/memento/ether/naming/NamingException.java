package psl.memento.ether.naming;

/**
 * Signifies a general error with the naming system.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class NamingException extends Exception
{
	public NamingException()
	{
		super();
	}

	public NamingException(String msg)
	{
		super(msg);
	}

	public NamingException(String msg, Throwable t)
	{
		super(msg, t);
	}


	public NamingException(Throwable t)
	{
		super(t);
	}
}
