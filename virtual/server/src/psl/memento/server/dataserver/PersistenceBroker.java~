/*
 * PersistenceBroker.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

/**
 * The broker is the brain of the persistence layer.  It enables the public API
 * to transparently access multiple data sources, including the local data
 * store, the data stores driving other CHIME worlds, and FRAX.  It also
 * enables client code to cache the data obtained from these sources in the
 * local data store according to some intelligent persistence scheme.
 *
 * @author Mark Ayzenshtat
 */
public class PersistenceBroker {
	private DataServer mDataServer;
	private PersistenceScheme mScheme;
	
	/**
	 * Constructs a persistence broker that uses the 
	 * <code>NaivePersistenceScheme</code>.
	 *
	 * @param iDataServer the data server on which to construct a broker
	 * @see NaivePersistenceScheme
	 */
	public PersistenceBroker(DataServer iDataServer) {
		this(iDataServer, new NaivePersistenceScheme(iDataServer));
	}
	
	/**
	 * Constructs a persistence broker that uses the supplied persistence scheme.
	 *
	 * @param iDataServer the data server on which to construct a broker
	 */
	public PersistenceBroker(DataServer iDataServer, PersistenceScheme iScheme) {
		mDataServer = iDataServer;
		setPersistenceScheme(iScheme);
	}
	
	/**
	 * Stores an object using this broker's persistence scheme.  Note that
	 * calling this method does not ensure that the supplied object will be
	 * stored.
	 *
	 * @param iP the object to store
	 * @exception DataAccessException if a backing data store error occurs
	 */
	public void smartPersistObject(Persistent iP) throws DataAccessException {
		mScheme.smartStore(iP);
	}
	
	/**
	 * Stores objects using this broker's persistence scheme.  Note that
	 * calling this method does not ensure that the supplied objects will be
	 * stored.
	 *
	 * @param iPs an array containing the objects to store
	 * @exception DataAccessException if a backing data store error occurs
	 */
	public void smartPersistObjects(Persistent[] iPs)
			throws DataAccessException {
		mScheme.smartStore(iPs);
	}
	
	/**
	 * Returns the scheme this broker will use in making objects persistent.
	 *
	 * @return the scheme this broker will use
	 */
	public PersistenceScheme getPersistenceScheme() {
		return mScheme;
	}

	/**
	 * Assigns the scheme this broker will use in making objects persistent.
	 *
	 * @param iScheme the scheme this broker will use
	 */
	public void setPersistenceScheme(PersistenceScheme iScheme) {
		mScheme = iScheme;
	}
}