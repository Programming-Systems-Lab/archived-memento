package aether.server.responder;

import aether.component.DefaultComponent;

/**
 * Partial implementation of the Responder interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public abstract class AbstractResponder extends DefaultComponent
		implements Responder
{
	/**
	 * GUID of the responder.
	 */
	protected String guid;

	/**
	 * SwitchBoard used by this Responder.
	 */
	protected SwitchBoard switchBoard;

	public SwitchBoard getSwitchBoard()
	{
		return switchBoard;
	}

	public void setSwitchBoard(SwitchBoard sb)
	{
		this.switchBoard = sb;
	}

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}
}
