package memento.world.model;

/**
 * Indicates a portal in the world that leads from one Sector to another.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Portal extends LocatableWorldObject
{
	/**
	 * Name of the open property.
	 */
	public static final String OpenProperty = "open";

	/**
	 * Get the Sector this portal leads to.
	 *
	 * @return  Sector this portal leads to
	 */
    public Sector getDestination();

	/**
	 * Set the Sector this portal leads to.
	 *
	 * @param sector sector this portal leads to
	 */
	public void setDestination(Sector sector);

	/**
	 * Determine whether a portal is currently open and can be traversed.
	 *
	 * @return true if the portal is currently open
	 */
	public boolean isOpen();

	/**
	 * Set whether the portal is open.
	 *
	 * @param open true if the portal should be opened
	 */
	public void setOpen(boolean open);
}
