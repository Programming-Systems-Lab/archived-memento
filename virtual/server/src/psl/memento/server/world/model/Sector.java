package psl.memento.server.world.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import psl.memento.server.util.concurrent.ReadLockEnumeration;

/**
 * Represents a sector in the chime world.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Sector extends WorldObject
{
	private Set portals = new HashSet();
	private Set contents = new HashSet();
	
	/**
	 * Add an object to the contents of this sector.
	 * 
	 * @param wo object to add to the contents of this sector
	 **/
	public void add(LocatableWorldObject wo)
	{
		if (wo == null)
		{
			String msg = "wo cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		
		try
		{
			this.getWriteLock();
			contents.add(wo);
			
			// update the world object
			wo.setCurrentLocation(this);
		}
		finally
		{
			this.releaseWriteLock();
		}
	
	}

	/**
	 * Remove an object from the contents of this sector
	 * 
	 * @param wo WorldObject to remove from the contents of this sector
	 **/
	public void remove(LocatableWorldObject wo)
	{
		// check for null
		if (wo == null)
		{
			return;
		}
		
		// make sure this world object is in the sector
		try
		{
			this.getReadLock();
			if (!contents.contains(wo))
			{
				return;
			}
		}
		finally
		{
			this.releaseReadLock();
		}
		
		// if so remove it from this sector
		try
		{
			this.getWriteLock();
			contents.remove(wo);
			
			// update the world obkect
			wo.setCurrentLocation(null);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}
	
	/**
	 * Retrieve an enumeration over the contents of this sector.
	 * 
	 * @return enumeration over the contents of the sector
	 **/
	public ReadLockEnumeration contents()
	{
		return new ReadLockEnumeration(this, contents.iterator());
	}
	
	/**
	 * Add a portal to this Sector linking it to another sector.
	 * 
	 * @param p Portal which leads from this sector to another sector
	 **/
	public void add(Portal p)
	{
		if (p == null)
		{
			String msg = "p cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		try
		{	
			this.getWriteLock();		
			portals.add(p);
			
			// change the portal so that it points from this sector
			p.setSourceSector(this);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}		
	
	/**
	 * Remove a portal from this sector. The removed portal will no longer link
	 * this sector with another sector.
	 * 
	 * @param p Portal to remove from this sector
	 **/
	public void remove(Portal p)
	{
		// check for null
		if (p == null)
		{
			return;
		}
		
		// make sure this portal is inside this sector
		try
		{
			this.getReadLock();
			if (!portals.contains(p))
			{
				return;
			}
		}
		finally
		{
			this.releaseReadLock();
		}
		
		// remove this portal from the sector
		try
		{
			this.getWriteLock();
			portals.remove(p);
			
			// nullify the source of the portal
			p.setSourceSector(null);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}
	
	/**
	 * Return an enumeration over the portals leading from this sector.
	 * 
	 * @return enumeration over the portals leading from this room
	 **/
	public ReadLockEnumeration portals()
	{
		return new ReadLockEnumeration(this, portals.iterator());
	}
	
}
