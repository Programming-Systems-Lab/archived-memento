package memento.world.model;

import java.util.EventObject;

/**
 * Describes a change to a world model.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldModelEvent extends EventObject
{
    private WorldObject cause;

	/**
	 * Construct a new WorldModelEvent.
	 *
	 * @param model WorldModel that generated this event
	 * @param cause WorldObject that caused this event
	 */
	public WorldModelEvent(WorldModel model, WorldObject cause)
	{
		super(model);

		if (cause == null)
		{
			String msg = "cause can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.cause = cause;
	}

	/**
	 * Get the cause of the event.
	 *
	 * @return WorldObject that's the primary concern of the event
	 */
	public WorldObject getCause()
	{
		return cause;
	}
}
