package aether.server.responder;

import java.util.EventListener;

/**
 * Indicates an object wishing to listen to events on the SwitchBoard.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface SwitchBoardListener extends EventListener
{
	/**
	 * Invoked when a Responder was bound to a SwitchBoard.
	 *
	 * @param sbe SwitchBoardEvent describing the binding
 	 */
	public void responderBound(SwitchBoardEvent sbe);

	/**
	 * Invoked when a Responder is unbound from a SwitchBoard.
	 *
	 * @param sbe SwitchBoardEvent describing the unbinding
	 */
	public void responderUnbound(SwitchBoardEvent sbe);
}
