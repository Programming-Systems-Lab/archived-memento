package psl.memento.server.world.model;

/**
 * Indicates a world object which has a location -- it can exist inside a 
 * sector. This can be used to rapidly find objects in the world.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class LocatableWorldObject extends WorldObject
{
	private Sector location;
	
	/**
	 * Get the current location of the world object.
	 * 
	 * @return current location of the world object
	 **/
	public Sector getCurrentLocation()
	{
		return location;
	}
	
	/**
	 * Set the current location of the object.
	 * 
	 * @param s Sector which is the new current location of the object
	 **/
	public void setCurrentLocation(Sector s)
	{
		this.location = s;
	}
}
