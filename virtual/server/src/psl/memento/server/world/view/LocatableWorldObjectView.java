package psl.memento.server.world.view;

/**
 * Represents a view of a LocatableWorldObject which is located within a 
 * sector.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class LocatableWorldObjectView extends View
{
	private int x;
	private int y;
	private int z;
	private int width;
	private int height;
	private int length;
	private SectorView location;
	
	/**
	 * Get the x-coordinate of the object in the world.
	 * 
	 * @return x-coordinate of this object
	 **/
	public int getX()
	{
		return x;
	}
	
	/**
	 * Set the x-coordinate of the object.
	 * 
	 * @param x the x-coordinate of this object
	 **/
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 * Get the y-coordinate of this object.
	 * 
	 * @return y-coordinate of this object
	 **/
	public int getY()
	{
		return y;
	}
	
	/**
	 * Set the y-coordinate of this object.
	 * 
	 * @param y the y-coordinate of this object
	 **/
	public void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 * Get the z-coordinate of this object.
	 * 
	 * @return the z-coordinate of this object
	 **/
	public int getZ()
	{
		return z;
	}
	
	/**
	 * Set the z-coordinate of this object.
	 * 
	 * @param z the z-coordinate of this object
	 **/
	public void setZ(int z)
	{
		this.z = z;
	}
	
	/**
	 * Get the width of this view.
	 * 
	 * @return width of this object
	 **/
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Set the width of this view.
	 * 
	 * @param width the width of this object
	 **/
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	/**
	 * Get the height of this object.
	 * 
	 * @return height of this object
	 **/
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Set the height of this object.
	 * 
	 * @param height height of this object
	 **/
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 * Get the length of this object.
	 * 
	 * @return length of this object
	 **/
	public int getLength()
	{
		return length;
	}
	
	/**
	 * Set the length of this object.
	 * 
	 * @param length the length of this object
	 **/
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * Get a view of the Sector which is the current location of this view.
	 * 
	 * @return view of the Sector which is the current location of this view
	 **/
	public SectorView getCurrentLocationView()
	{
		return location;
	}
	
	/**
	 * Set the view of the Sector which is the current location of this view.
	 * 
	 * @param view view of the Sector which is the current location of this
	 *             object
	 **/
	public void setCurrentLocationView(SectorView view)
	{
		this.location = view;
	}
}
