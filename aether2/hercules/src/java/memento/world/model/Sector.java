package memento.world.model;

import java.util.Enumeration;

/**
 * Represents a sector in the chime world.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Sector extends WorldObject
{
	/**
	 * Add a LocatableWorldObject to this Sector.
	 *
	 * @param lwo object that now exists in this sector
	 */
    public void add(LocatableWorldObject lwo);

	/**
	 * Remove an object from this sector.
	 *
	 * @param lwo object that has left this sector
	 */
    public void remove(LocatableWorldObject lwo);

	/**
	 * Retrieve an enumeration of all the objects in the sector.
	 *
	 * @return enumeration of all the objects in the sector
	 */
    public Enumeration contents();

    /**
	 * Add a portal that leads from this sector.
	 *
	 * @param p portal to place in this sector
	 */
    public void add(Portal p);

	/**
	 * Remove a portal that leads from this sector.
	 *
	 * @param p portal to remove
	 */
    public void remove(Portal p);

	/**
	 * Retrieve an enumeration over all the portals in this sector.
	 *
	 * @return enumeration over all the portals in the sector
	 */
    public Enumeration portals();

	/**
	 * Add a listener to receive special Sector events.
	 *
	 * @param sl listener for sector events
	 */
	public void addSectorListener(SectorListener sl);

	/**
	 * Remove a listener from getting Sector events.
	 *
	 * @param sl listener to remove
	 */
	public void removeSectorListener(SectorListener sl);
}
