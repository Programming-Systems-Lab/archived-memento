package aether.server.responder;

import java.util.EventObject;

/**
 * Indicates an event that occured within a Switchboard object.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class SwitchBoardEvent extends EventObject
{
	private Responder responder;
	private String dest;

	/**
	 * Construct a new SwitchBoardEvent indicating the Switchboard, Responder
	 * and destination that were involved.
	 *
	 * @param sb   SwitchBoard that generated the event
	 * @param resp Responder this event concerns
	 * @param dest  Destination that the Responder is linked to
	 */
	public SwitchBoardEvent(SwitchBoard sb, Responder resp, String dest)
	{
		super(sb);

		if ((resp == null) || (dest == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.responder = resp;
		this.dest = dest;
	}

	/**
	 * Get the SwitchBoard that generated this event.
	 *
	 * @return SwitchBoard that generated this event
	 */
	public SwitchBoard getSwitchBoard()
	{
		return (SwitchBoard) source;
	}

	/**
	 * Get the Responder that caused this event.
	 *
	 * @return Responder that caused this event
	 */
	public Responder getResponder()
	{
		return responder;
	}

	/**
	 * Get the destination part of this event.
	 *
	 * @return destination that the Responder was linked to
	 */
	public String getDestination()
	{
		return dest;
	}
}
