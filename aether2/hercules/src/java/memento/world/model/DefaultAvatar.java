package memento.world.model;

/**
 * Basic implementation of the Avatar interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultAvatar extends DefaultLocatableWorldObject
		implements Avatar
{
	private String guid;

	public String getClientGuid()
	{
		return guid;
	}

	public void setClientGuid(String guid)
	{
		this.guid = guid;
	}
}
