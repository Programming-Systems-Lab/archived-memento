/*
 * PersistenceScheme.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

/**
 * A persistence scheme dictates what strategy the persistence broker is to
 * employ in storing various data to the local store.
 *
 * @author Mark Ayzenshtat
 */
public interface PersistenceScheme {
	/**
	 * Potentially stores the supplied <code>Persistent</code> object according
	 * to some predetermined persistence strategy.  Calling this method does
	 * not guarantee that the supplied object will be stored.
	 *
	 * @param iP the object to potentially store
	 */
	public void smartStore(Persistent iP) throws DataAccessException;

	/**
	 * Potentially stores all of the supplied <code>Persistent</code> objects
	 * (which must all be of the same class) according to some predetermined
	 * persistence strategy.  Calling this method does not guarantee that the
	 * supplied objects will be stored.
	 *
	 * @param iP the objects to potentially store
	 */
	public void smartStore(Persistent[] iPs) throws DataAccessException;
}