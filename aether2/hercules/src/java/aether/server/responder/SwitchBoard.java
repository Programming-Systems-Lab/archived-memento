package aether.server.responder;

import aether.net.Connection;

import java.io.IOException;

/**
 * A SwitchBoard dispatches incoming requests to the appropriate Responders.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface SwitchBoard
{
	/**
	 * Bind a Responder against the given destination. Any requests sent to
	 * the destination will be routed to the Responder.
	 *
	 * @param r    Responder to bind against the destination
	 * @param dest destination to bind the Responder to
	 * @throws ResponderException
	 *         if a Responder is already bound to <code>dest</code>
	 * @throws IOException
	 *         if the subscription binding process fails
	 */
    public void bind(Responder r, String dest) throws ResponderException,
			IOException;

	/**
	 * Unbind a Responder against a given destination so it will no longer
	 * process requests sent to that destination.
	 *
	 * @param r    Responder to unbind
	 * @param dest destination to unbind the Responder from
	 * @throws ResponderException
	 *         if the Responder can't be unbound
	 */
	public void unbind(Responder r, String dest) throws ResponderException,
			IOException;

	/**
	 * Add a SwitchBoardListener interested in monitoring this SwitchBoard.
	 *
	 * @param sbl SwitchBoardListener to receive events
	 */
	public void addSwitchBoardListener(SwitchBoardListener sbl);

	/**
	 * Remove a SwitchBoardListener no longer interested in monitoring this
	 * SwitchBoard.
	 *
	 * @param sbl SwitchBoardListener to receive events
	 */
	public void removeSwitchBoardListener(SwitchBoardListener sbl);
}
