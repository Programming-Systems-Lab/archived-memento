package memento.world.model;

/**
 * Basic implementation of the Portal interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultPortal extends DefaultLocatableWorldObject implements Portal
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
		boolean oldVal = this.open;
		this.open = open;

        pcSupport.firePropertyChange(OpenProperty, oldVal, open);
	}
}
