package memento.world.model;

import java.util.EventListener;

/**
 * Object capable of listening to events from a Sector.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface SectorListener extends EventListener
{
	/**
	 * An object has entered a sector.
	 *
	 * @param se event describing the change
	 */
	public void entered(SectorEvent se);

	/**
	 * An object has left the sector.
	 *
	 * @param se event describing the change
	 */
	public void exited(SectorEvent se);
}
