package aether.server.responder;

import org.elvin.je4.Notification;
import org.apache.log4j.Logger;
import aether.net.Connection;
import aether.event.Event;
import aether.event.Request;
import aether.event.Response;
import aether.event.EventException;

import java.io.IOException;

/**
 * Represents a unit of work that must be completed to process some request.
 * RequestExecutors are usually queued up and then executed asynchronously in
 * the request processing threads.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class RequestExecutor implements Runnable
{
    private Responder responder;
	private Notification notifcation;
	private Connection connection;

	private static final Logger log = Logger.getLogger(RequestExecutor.class);

	/**
	 * Construct a new RequestExecutor to process the given request.
	 *
	 * @param resp  Responder that generates the response
	 * @param notif Notification containing Request data
	 * @param conn  Connection to send the response over
	 */
	public RequestExecutor(Responder resp, Notification notif, Connection conn)
	{
		if ((resp == null) || (notif == null) || (conn == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.responder = resp;
		this.notifcation = notif;
		this.connection = conn;
	}

	/**
	 * Execute the contained request by running it.
	 */
	public void run()
	{
		Request request = null;

        if (Event.isRequest(notifcation))
		{
			try
			{
				request = new Request(notifcation);
			}
			catch (EventException ee)
			{
				log.warn("received bad request data", ee);
				ee.printStackTrace();
			}

			// construct the necessary Response object
			Response response = Request.createResponse(request,
												   responder.getGuid());

			// ask the responder to process it
			try
			{
            	responder.respond(request, response);
			}
            catch (ResponderException re)
			{
				throw new RespondFailedException(re);
			}

			// now send the response back
			try
			{
				connection.publish(response);
			}
            catch (IOException ioe)
			{
                throw new SendResponseException(ioe);
			}
		}
		else
		{
			// received a notification that wasn't a request
			log.warn("recieved non-request notification: " + notifcation);
		}
	}
}
