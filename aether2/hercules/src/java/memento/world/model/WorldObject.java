package memento.world.model;

import aether.server.framework.Identifiable;

import java.beans.PropertyChangeListener;

/**
 * Defines an object in the world.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface WorldObject extends Identifiable
{

	/**
	 * Name of the bound size property.
	 */
	public static final String SizeProperty = "size";

	/**
	 * Property indicating the WorldModel this object belongs to.
	 */
	public static final String WorldModelProperty = "world-model";

   /**
	* Get the size of the object.
	*/
	public Dimension getSize();

	/**
	 * Set the size of the object.
	 */
	public void setSize(Dimension dim);

	/**
	 * Get the WorldModel this object belongs to.
	 */
	public WorldModel getWorldModel();

	/**
	 * Set the WorldModel this object belongs to.
	 */
	public void setWorldModel(WorldModel model);

	/**
	 * Called when the WorldObject is about to be destroyed.
	 */
	public void destroy();

	/**
	 * Add a listener to know when the world object changes.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl);

	/**
	 * Add a listener to a specific property.
	 *
	 * @param name name of the property to monitor
	 * @param pcl
	 */
	public void addPropertyChangeListener(String name,
										  PropertyChangeListener pcl);

	/**
	 * Remove a listener.
	 *
	 * @param pcl listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl);

	/**
	 * Remove a listener from a specific property.
	 *
	 * @param name name of the property to monitor
	 * @param pcl  listener to remove
	 */
	public void removePropertyChangeListener(String name,
											 PropertyChangeListener pcl);
}
