package aether.server.responder;


/**
 * Indicates a general error within the Responder subsystem.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class ResponderException extends net.concedere.dundee.ComponentException
{
    public ResponderException()
	{
		super();
	}

	public ResponderException(String msg)
	{
		super(msg);
	}

	public ResponderException(String msg, Throwable t)
	{
		super(msg, t);
	}

	public ResponderException(Throwable t)
	{
		super(t);
	}
}
