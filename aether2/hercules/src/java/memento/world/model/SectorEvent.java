package memento.world.model;

import java.util.EventObject;

/**
 * Describes changes in a Sector.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class SectorEvent extends EventObject
{
	private WorldObject cause;

	/**
	 * Construct a new SectorEvent.
	 *
	 * @param sector Sector that generated the event
	 * @param cause  WorldObject that initiated the change
	 */
	public SectorEvent(Sector sector, WorldObject cause)
	{
		super(sector);
		if (cause == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.cause = cause;
	}

	/**
	 * Get the WorldObject that initiated the event.
	 *
	 * @return world object that initiated the event
	 */
	public WorldObject getCause()
	{
		return cause;
	}
}
