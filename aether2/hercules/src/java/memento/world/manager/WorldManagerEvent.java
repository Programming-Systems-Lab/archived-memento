package memento.world.manager;

import memento.world.model.WorldModel;

import java.util.EventObject;

/**
 * Event describing changes to the world manager.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldManagerEvent extends EventObject
{
    private WorldModel model;

	/**
	 * Construct a new WorldManagerEvent to describe a change in the manager.
	 *
	 * @param source WorldManager that generated this event
	 * @param model  WorldModel that's the focus of this event
	 */
	public WorldManagerEvent(WorldManager source, WorldModel model)
	{
		super(source);

		if (model == null)
		{
			String msg = "model can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.model = model;
	}

	/**
	 * Get the WorldModel that's the focus of this event.
	 *
	 * @return WorldModel that's the focus of this event
	 */
	public WorldModel getWorldModel()
	{
		return model;
	}


}
