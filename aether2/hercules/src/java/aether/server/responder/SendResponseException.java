package aether.server.responder;

/**
 * Indicates that an exception occured while attempting to send a response.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class SendResponseException extends RuntimeResponderException
{
	public SendResponseException()
	{
		super();
	}

	public SendResponseException(String s)
	{
		super(s);
	}

	public SendResponseException(String s, Throwable t)
	{
		super(s, t);
	}

	public SendResponseException(Throwable t)
	{
		super(t);
	}
}
