package aether.server.responder;

import aether.event.Response;
import aether.event.Request;
import aether.server.framework.Identifiable;

/**
 * A Responder is an object that will process requests and generate responses.
 * In order for Responders to receive requests they must utilize the
 * Switchboard service.
 * <p />
 * Responders are full beans/components. When they are bound to a Switchboard
 * they will be added to the BeanContext/Container (unless they've already
 * been added before).
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Responder extends aether.server.framework.Identifiable
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

	/**
	 * Get the SwitchBoard service used by this Responder.
	 *
	 * @return SwitchBoard service used by this Responder
	 */
	public SwitchBoard getSwitchBoard();

	/**
	 * Set the SwitchBoard service used by this Responder.
	 *
	 * @param sb SwitchBoard service used by this responder
	 */
	public void setSwitchBoard(SwitchBoard sb);
}
