package memento.world.model;

/**
 * Indicates an object in the world that has a location.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class LocatableWorldObject extends WorldObject
{
    /**
     * Sector that's the location of the world object.
     */
	protected Sector location;

    /**
     * Position of the world object.
     */
    protected Position position;

    /**
     * Model file associated with the LWO.
     */
    protected String model;

	/**
	 * Get the current location of the object.
	 *
	 * @return current location of the object
	 */
	public Sector getLocation()
    {
        return location;
    }

	/**
	 * Set the current location of the object.
	 *
	 * @param sector current location of the object
	 */
	public void setLocation(Sector sector)
    {
        this.location = sector;
    }

	/**
	 * Get the position of the object in the world.
	 *
	 * @return position of the object in the world
	 */
  	public Position getPosition()
    {
        return position;
    }

	/**
	 * Set the position of the object in the world.
	 *
	 * @param pos position of the object in the world
	 */
	public void setPosition(Position pos)
    {
        this.position = pos;
    }


    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }
}
