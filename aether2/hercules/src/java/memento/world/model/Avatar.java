package memento.world.model;

/**
 * Basic implementation of the Avatar interface.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class Avatar extends LocatableWorldObject
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
