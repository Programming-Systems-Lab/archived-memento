package aether.server.responder;

import aether.event.Request;
import aether.event.Response;

/**
 * A Responder is an object that will process requests and generate responses.
 * In order for Responders to receive requests they must utilize the
 * Switchboard service.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface Responder
{
	/**
	 * Proces an incoming request by setting variables on the approriate
	 * response.
	 *
	 * @param request  incoming request to be processed
	 * @param response response to be sent back
	 * @throws ResponderException
	 *         if a component-level error occurs
	 */
	public void respond(Request request, Response response)
			throws ResponderException;
}
