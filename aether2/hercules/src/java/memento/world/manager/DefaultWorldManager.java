package memento.world.manager;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.Beans;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.beans.beancontext.BeanContextMembershipListener;
import java.beans.beancontext.BeanContextMembershipEvent;

import memento.world.model.WorldModel;

import javax.swing.event.EventListenerList;

/**
 * Default implementation of the WorldManager interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultWorldManager extends DefaultComponent
		implements Initializable, Disposable, WorldManager
{
	private Map worldUIMap = Collections.synchronizedMap(new HashMap());
	private BeanContextMembershipListener bcml;

	/**
	 * EventListenerList used to manage listeners.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	public void initialize() throws ComponentException
	{
		bcml = new ContextMembershipListener();
		getContainer().addBeanContextMembershipListener(bcml);
	}

	public void dispose() throws ComponentException
	{
		getContainer().removeBeanContextMembershipListener(bcml);
		bcml = null;

		// remove any left over worlds and uis in this container
		for (Iterator i = worldUIMap.keySet().iterator(); i.hasNext(); )
		{
			WorldModel model = (WorldModel) i.next();
			WorldUI ui = (WorldUI) worldUIMap.get(model);
			getContainer().remove(ui);
			getContainer().remove(model);
		}
		worldUIMap.clear();
		worldUIMap = null;
	}

	public boolean manage(WorldModel model)
	{
		if (model == null)
		{
			String msg = "model can't be null";
			throw new IllegalArgumentException(msg);
		}

		WorldUI ui = new WorldUI();
		ui.setWorldModel(model);
		worldUIMap.put(model, ui);

		if (getContainer().add(ui))
		{
			fireManaged(model);
			return true;
		}
		else
		{
			worldUIMap.remove(model);
			return false;
		}
	}

	public boolean unmanage(WorldModel model)
	{
		if (model == null)
		{
			String msg = "model can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (worldUIMap.containsKey(model))
		{
			WorldUI ui = (WorldUI) worldUIMap.remove(model);
			getContainer().remove(ui);
			fireUnmanaged(model);
			return true;
		}
		return false;
	}

	public void addWorldManagerListener(WorldManagerListener wml)
	{
		listenerList.add(WorldManagerListener.class, wml);
	}

	public void removeWorldManagerListener(WorldManagerListener wml)
	{
		listenerList.add(WorldManagerListener.class, wml);
	}

    protected void fireManaged(WorldModel model)
	{
        if (model == null)
		{
			String msg = "model can't be null";
			throw new IllegalArgumentException(msg);
		}

        WorldManagerEvent wme = new WorldManagerEvent(this, model);
        Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
            ((WorldManagerListener) listeners[i + 2]).managed(wme);
		}
	}

	protected void fireUnmanaged(WorldModel model)
	{
		if (model == null)
		{
			String msg = "model can't be null";
			throw new IllegalArgumentException(msg);
		}

        WorldManagerEvent wme = new WorldManagerEvent(this, model);
        Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
        	((WorldManagerListener) listeners[i + 2]).unmanaged(wme);
		}
	}


	/**
	 * Special class for managing incoming and departing world models. Each
	 * time a World Model joins a container, it must be managed by some UI
	 * and each time it leaves the container the UI must be destroyed.
	 */
	private class ContextMembershipListener
			implements BeanContextMembershipListener
	{
		public void childrenAdded(BeanContextMembershipEvent bcme)
		{
			Beans beans = getContainer().getBeans();
			for (int i = 0; i < bcme.toArray().length; ++i)
			{
				Object child = bcme.toArray()[i];
				if (beans.isInstanceOf(child, WorldModel.class))
				{
					WorldModel model = (WorldModel)
							beans.getInstanceOf(child, WorldModel.class);
					manage(model);
				}
			}
		}

		public void childrenRemoved(BeanContextMembershipEvent bcme)
		{
			Beans beans = getContainer().getBeans();
			for (int i = 0; i < bcme.toArray().length; ++i)
			{
				Object child = bcme.toArray()[i];
				if (beans.isInstanceOf(child, WorldModel.class))
				{
					WorldModel model = (WorldModel)
							beans.getInstanceOf(child, WorldModel.class);
					unmanage(model);
				}
			}
		}
	}


}
