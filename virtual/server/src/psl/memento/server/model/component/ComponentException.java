package psl.memento.server.model.component;

/**
 * Top-level exception for all component-related exceptions.
 * 
 * @author Buko Obele (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentException extends Exception
{
	public ComponentException()
	{
		; // do nothing
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
