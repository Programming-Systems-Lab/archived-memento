package memento.world.model;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * Partial implementation of the WorldObject interface.
 *
 * TODO: think a lot more about the object destruction case
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class AbstractWorldObject implements WorldObject, Serializable
{
	/**
	 * GUID of this object.
	 */
	protected String guid;

	/**
	 * Size of the object.
	 */
	protected Dimension size;

	/**
	 * WorldModel that created this object.
	 */
	protected WorldModel worldModel;

	/**
	 * EventListenerList used to manage event listeners.
	 */
	protected PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public Dimension getSize()
	{
		return size;
	}

	public void setSize(Dimension dim)
	{
		Dimension oldSize = dim;
		this.size = dim;

		pcSupport.firePropertyChange(SizeProperty, oldSize, size);
	}

	public WorldModel getWorldModel()
	{
		return worldModel;
	}

	public void setWorldModel(WorldModel model)
	{
		WorldModel oldModel = this.worldModel;
		this.worldModel = model;

		pcSupport.firePropertyChange(WorldModelProperty, oldModel, model);
	}

	public void destroy()
	{
		; // do nothing
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl)
	{
		pcSupport.addPropertyChangeListener(pcl);
	}

	public void addPropertyChangeListener(String name,
										  PropertyChangeListener pcl)
	{
		pcSupport.addPropertyChangeListener(name, pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl)
	{
		pcSupport.removePropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(String name,
											 PropertyChangeListener pcl)
	{
		pcSupport.removePropertyChangeListener(name, pcl);
	}

}
