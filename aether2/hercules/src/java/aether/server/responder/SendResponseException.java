package aether.server.responder;

import java.io.IOException;

/**
 * Indicates that an exception occured while attempting to send a response.
 *
 * @author Buko O. (buko@concedere.net)
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
