/*
 * CachedDAO.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import java.util.Map;
import java.util.HashMap;

/**
 * <code>CachedDAO</code> caches a mapping of IDs to <code>Persistent</code>
 * objects in local memory, so that successive <code>load(id)</code> calls
 * do not need to be routed to the underlying data store in order to be
 * fulfilled.  This will almost always result in at least a marginal increase
 * in speed.<br>
 * <br>
 * To use <code>CachedDAO</code>, simply pass <code>true</code> as the <code>
 * iCached</code> parameter to DAOFactory.getDAO(...):<br>
 * <br>
 * <code>
 * DAOFactory factory = <em>acquire DAOFactory instance</em><br>
 * DataAccessObject myDAO = factory.getDAO(<em>somePersistentClass</em>,
 * true);
 * </code>
 *
 * @author Mark Ayzenshtat 
 */
public class CachedDAO implements DataAccessObject {
	private DataAccessObject mSourceDAO;
	private Map mCache;
	
	CachedDAO(DataAccessObject iSourceDAO) {
		if (iSourceDAO == null) {
			throw new NullPointerException("Supplied DAO must be non-null.");
		}
		
		mSourceDAO = iSourceDAO;
		mCache = new HashMap();		
	}
	
	/**
	 * Loads the persistent object with the given ID from the
	 * backing data store.
	 *
	 * @param iID the ID
	 * @return the loaded persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public Persistent load(int iID) throws DataAccessException {
		Integer integerID = new Integer(iID);
		
		Persistent p = (Persistent) mCache.get(integerID);
		if (p == null) {
			// the object we're trying to load is not locally cached,
			// so get it from the source DAO and place it in the cache
			p = mSourceDAO.load(iID);
			mCache.put(integerID, p);
		}
		
		return p;
	}
	
	/**
	 * Saves the given persistent object to the backing data store.
	 *
	 * @param iR the persistent object to save
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void store(Persistent iR) throws DataAccessException {
		// store the persistent object in the source DAO
		mSourceDAO.store(iR);
		
		// store the persistent object in the cache
		mCache.put(new Integer(iR.getPersistenceID()), iR);
	}
	
	/**
	 * Creates a persistent object in the backing data store.
	 *
	 * @return the ID that uniquely identifies the created persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public int create() throws DataAccessException {
		return mSourceDAO.create();
	}

	/**
	 * Deletes a persistent object from the backing data store.
	 *
	 * @param iID the ID that uniquely identifies the persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void delete(int iID) throws DataAccessException {
		// delete the persistent object from the source DAO
		mSourceDAO.delete(iID);
		
		// delete the persistent object from the cache
		mCache.remove(new Integer(iID));
	}
}