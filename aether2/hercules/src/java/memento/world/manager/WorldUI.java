package memento.world.manager;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;
import memento.world.model.WorldModel;
import aether.server.domain.Advertisement;

/**
 * A WorldUI is not a graphical component, instead it is a logical concept:
 * it manages the complete interface to some world model. This includes the
 * controller that manages incoming requests and the view that broadcasts
 * changes to the model.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldUI extends DefaultComponent  implements Initializable,
		Disposable
{
    private WorldController controller;
	private WorldView view;
    private WorldModel worldModel;

	public void initialize() throws ComponentException
	{
        controller = new WorldController();
		controller.setWorldModel(worldModel);

        if (! getContainer().add(controller))
		{
			String msg = "couldn't add WorldController to container";
			throw new ComponentException(msg);
		}

		view = new WorldView();
		view.setWorldModel(worldModel);

		if (! getContainer().add(view))
		{
			String msg = "couldn't add WorldView to the container";
			throw new ComponentException(msg);
		}
	}

	public void dispose() throws ComponentException
	{
        getContainer().remove(controller);
		getContainer().remove(view);
	}

	public WorldModel getWorldModel()
	{
		return worldModel;
	}

	public void setWorldModel(WorldModel worldModel)
	{
		this.worldModel = worldModel;
	}
}
