/*
 * Persistent.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

/**
 * This interface should be used to mark any class of objects that can be
 * loaded from or stored to the CHIME 4 data server.  Since data access objects
 * (DAOs) map integral IDs to object references, <code>Persistent</code>
 * objects must implement the <code>getPersistenceID()</code> and <code>
 * setPersistenceID(id)</code> methods.
 *
 * @author Mark Ayzenshtat
 */
public interface Persistent {
	/**
	 * Retrieves the unique ID that serves as a key to this object.
	 *
	 * @return this object's ID
	 */
	public int getPersistenceID();
	
	/**
	 * Assigns the unique ID that serves as a key to this object.
	 *
	 * @param iID this object's ID
	 */
	public void setPersistenceID(int iID);
}