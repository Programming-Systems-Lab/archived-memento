package memento.world.model;

/**
 * Indicates an object in the world that has a location.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface LocatableWorldObject extends WorldObject
{
	/**
	 * Name of bound property 'location'.
	 */
	public static final String LocationProperty = "location";

	/**
	 * Name of the bound 'position' property.
	 */
	public static final String PositionProperty = "position";

	/**
	 * Get the current location of the object.
	 *
	 * @return current location of the object
	 */
	public Sector getLocation();

	/**
	 * Set the current location of the object.
	 *
	 * @param sector current location of the object
	 */
	public void setLocation(Sector sector);

	/**
	 * Get the position of the object in the world.
	 *
	 * @return position of the object in the world
	 */
  	public Position getPosition();

	/**
	 * Set the position of the object in the world.
	 *
	 * @param pos position of the object in the world
	 */
	public void setPosition(Position pos);
}
