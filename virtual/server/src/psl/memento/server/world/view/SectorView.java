package psl.memento.server.world.view;

import java.util.HashSet;
import java.util.Set;

import psl.memento.server.util.concurrent.ReadLockEnumeration;

/**
 * Describes a sector within the world.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class SectorView extends View
{	
	private int width;
	private int height;
	private int length;
	private Set portalViews = new HashSet();
	private Set contentsViews = new HashSet();
	
	/**
	 * Get the width of the sector.
	 * 
	 * @return width of the sector
	 **/
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Set the width of the sector.
	 * 
	 * @param width width of the sector
	 **/
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	/**
	 * Get the height of the sector.
	 * 
	 * @return height of the sector
	 **/
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Set the height of the sector.
	 * 
	 * @param height set the height of the sector
	 **/
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 * Get the length of the sector.
	 * 
	 * @return length of the sector
	 **/
	public int getLength()
	{
		return length;
	}
	
	/**
	 * Set the length of the sector.
	 * 
	 * @param length length of the sector
	 **/
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * Add a view of an object to the contents of this sector view.
	 * 
	 * @param view view of an object in this room
	 **/
	public void add(LocatableWorldObjectView view)
	{
		if (view == null)
		{
			String msg = "view cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		try
		{
			this.getWriteLock();
			contentsViews.add(view);
			
			// update the view
			view.setCurrentLocationView(this);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}
	
	/**
	 * Remove a view of an object from the contents of this sector view.
	 * 
	 * @param view view of an object in this room
	 **/
	public void remove(LocatableWorldObjectView view)
	{
		// check for null
		if (view == null)
		{
			return;
		}
		
		// make sure this sector view contains the given view
		try
		{
			this.getReadLock();
			if (!contentsViews.contains(view))
			{
				return;
			}
		}
		finally
		{
			this.releaseReadLock();
		}
		
		// remove the view from this sector view
		try
		{
			this.getWriteLock();
			contentsViews.remove(view);
			
			// update the view
			view.setCurrentLocationView(null);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}
	
	/**
	 * Return a read locking enumeration over the views of contents in this
	 * object.
	 * 
	 * @return read-locking enumeration over the contents of this view
	 **/
	public ReadLockEnumeration contentsViews()
	{
		return new ReadLockEnumeration(this, contentsViews.iterator());
	}
			
	/**
	 * Add a view of a portal that leads from this sector which leads to
	 * another.
	 * 
	 * @param portalView view of a portal for a portal which leads from this
	 *                   room to another
	 **/
	public void add(PortalView portalView)
	{
		if (portalView == null)
		{
			String msg = "portalView cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		try
		{
			this.getWriteLock();
			portalViews.add(portalView);
			
			// update the portal view
			portalView.setCurrentLocationView(this);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}
	
	/**
	 * Remove a view of a portal from this sector.
	 * 
	 * @param pView view of a portal which no longer leads from this sector
	 **/
	public void remove(PortalView pView)
	{
		if (pView == null)
		{
			return;
		}
		
		// make sure this view is in this sector view
		try
		{
			this.getReadLock();
			if (!portalViews.contains(pView))
			{
				return;
			}
		}
		finally
		{
			this.releaseReadLock();
		}
		
		// remove the portal view from this sector view
		try
		{
			this.getWriteLock();
			portalViews.remove(pView);
			
			pView.setCurrentLocationView(null);
		}
		finally
		{
			this.releaseWriteLock();
		}
	}
	
	/**
	 * Return a read-locking enumeration over the views of portals in this 
	 * sector.
	 * 
	 * @return ReadLockEnumeration over the portal views in the room
	 **/
	public ReadLockEnumeration portalViews()
	{
		return new ReadLockEnumeration(this, portalViews.iterator());
	}
	

}
