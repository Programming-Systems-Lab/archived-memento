package psl.memento.server.world.model;

/**
 * Represents a connection from one Portal to another.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Portal extends LocatableWorldObject
{
	private Sector destination;
	
	/**
	 * Get the Sector this portal leads from. This is also the current
	 * location of the portal.
	 * 
	 * @return Sector this portal leads from
	 **/
	public Sector getSourceSector()
	{
		return this.getCurrentLocation();
	}
	
	/**
	 * Set the Sector this portal shall lead from. This also sets the current
	 * location of the sector.
	 * 
	 * @param source Sector this portal will lead from
	 **/
	public void setSourceSector(Sector source)
	{		
		this.setCurrentLocation(source);
	}
	
	/**
	 * Get the destination of this portal.
	 * 
	 * @return destination of the portal
	 **/
	public Sector getDestinationSector()
	{
		return destination;
	}
	
	/**
	 * Set the destination of this portal.
	 * 
	 * @param dest Destination sector of this portal
	 **/
	public void setDestinationSector(Sector dest)
	{
		this.destination = dest;
	}
}
