package memento.world.model;

/**
 * Basic implementation of LocatableWorldObject.
 *
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultLocatableWorldObject extends AbstractWorldObject
		implements LocatableWorldObject
{
	/**
	 * Current location.
	 */
	protected Sector sector;

	/**
	 * Position of the object in the world.
	 */
	protected Position position;

    /**
     * name of the object in the world
     */
    protected String name;

    /**
     * 3DS model file that represents the id
     */
    protected String model;


	public Sector getLocation()
	{
		return sector;
	}

	public void setLocation(Sector sector)
	{
		Sector oldValue = this.sector;
		this.sector = sector;

		pcSupport.firePropertyChange(LocationProperty, oldValue, sector);
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position pos)
	{
		Position oldPos = this.position;
		this.position = pos;

		pcSupport.firePropertyChange(PositionProperty, oldPos, pos);
	}

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
