package memento.world.model;

import javax.swing.event.EventListenerList;
import java.util.Enumeration;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

/**
 * Basic implementation of the Sector interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class Sector extends WorldObject
{
	/**
	 * Set of contents in the sector.
	 */
	protected Set contents = Collections.synchronizedSet(new HashSet());

	/**
	 * Set of portals in the sector.
	 */
	protected Set portals = Collections.synchronizedSet(new HashSet());

	/**
	 * ListenerList used to manage listeners.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	public void add(LocatableWorldObject lwo)
	{
        if (lwo == null)
		{
			String msg = "lwo can't be null";
			throw new IllegalArgumentException(msg);
		}

        if (!contents.contains(lwo))
		{
			contents.add(lwo);
			lwo.setLocation(this);
		}
	}


	public void add(Portal p)
	{
		if (p == null)
		{
			String msg = "p can't be null";
			throw new IllegalArgumentException(msg);
		}

        if (!portals.contains(p))
		{
			portals.add(p);
			p.setLocation(this);
		}
	}


	public void addSectorListener(SectorListener sl)
	{
		listenerList.add(SectorListener.class, sl);
	}

	public Enumeration contents()
	{
		return Collections.enumeration(contents);
	}

	public Enumeration portals()
	{
		return Collections.enumeration(portals);
	}

	public void remove(LocatableWorldObject lwo)
	{
		if (lwo == null)
		{
			String msg = "lwo can't be null";
			throw new IllegalArgumentException(msg);
		}

        if (contents.contains(lwo))
		{
			contents.remove(lwo);
			lwo.setLocation(null);
		}
	}

	public void remove(Portal p)
	{
		if (p == null)
		{
			String msg = "p can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (portals.contains(p))
		{
			portals.remove(p);
			p.setLocation(null);
		}
	}
}
