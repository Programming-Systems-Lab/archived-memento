package aether.server.responder;

/**
 * Baseclass for Responder exceptions that occur during the asynchronous
 * response processing.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class RuntimeResponderException extends RuntimeException
{
	public RuntimeResponderException()
	{
		super();
	}

	public RuntimeResponderException(String s)
	{
		super(s);
	}

	public RuntimeResponderException(String s, Throwable t)
	{
		super(s, t);
	}

	public RuntimeResponderException(Throwable t)
	{
		super(t);
	}
}
