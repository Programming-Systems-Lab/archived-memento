package memento.world.model;

/**
 * Basic implementation of the Portal interface.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class Portal extends LocatableWorldObject
{
	/**
	 * Destination of the portal.
	 */
	protected Sector dest;

	/**
	 * Whether the portal is open.
	 */
	protected boolean open;

	public Sector getDestination()
	{
		return dest;
	}

	public boolean isOpen()
	{
		return open;
	}

	public void setDestination(Sector sector)
	{
		this.dest = sector;
	}

	public void setOpen(boolean open)
	{
		this.open = open;
	}
}
