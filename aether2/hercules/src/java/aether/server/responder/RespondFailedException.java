package aether.server.responder;

/**
 * Indicates that a Responder.respond method threw a component-level exception
 * while processing the (Request,Response) pair.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class RespondFailedException extends RuntimeResponderException
{
    public RespondFailedException()
	{
		super();
	}

	public RespondFailedException(String s)
	{
		super(s);
	}

	public RespondFailedException(String s, Throwable t)
	{
		super(s, t);
	}

	public RespondFailedException(Throwable t)
	{
		super(t);
	}
}
