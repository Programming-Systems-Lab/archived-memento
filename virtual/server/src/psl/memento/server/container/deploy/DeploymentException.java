package psl.memento.server.container.deploy;

/**
 * Represents a general exception having to do with deployment.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class DeploymentException extends Exception
{
	public DeploymentException()
	{
		super();
	}
	
	public DeploymentException(String msg)
	{
		super(msg);
	}
	
	public DeploymentException(String msg, Throwable t)
	{
		super(msg, t);
	}
	
	public DeploymentException(Throwable t)
	{
		super(t);
	}
	

}
