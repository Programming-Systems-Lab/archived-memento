/*
 * DAOFactory.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

/**
 * A factory for creating <code>DataAccessObject</code>s.
 *
 * @author Mark Ayzenshtat 
 */
public interface DAOFactory {
	/**
	 * Retrieves a valid <code>DataAccessObject</code> instance that manages
	 * the supplied class of <code>Persistent</code> objects.
	 *
	 * @param iPersistentClass the class of <code>Persistent</code> objects
	 * that the returned <code>DataAccessObject</code> manages
	 * @return a valid <code>DataAccessObject</code> instance	 
	 * @exception IllegalArgumentException if the supplied class does not
	 * inherit from <code>Persistent</code>
	 * @exception IllegalArgumentException if this <code>DAOFactory</code>
	 * does not have any account of the supplied class of <code>Persistent
	 * </code>objects
	 * @see CachedDAO
	 */
	public DataAccessObject getDAO(Class iPersistentClass);
	
	/**
	 * Binds a class of <code>Persistent</code> objects to a class of
	 * <code>DataAccessObject</code> instances.
	 *
	 * @param iPersistentClass the class of Persistent objects
	 * @param iDAOClass the class of DataAccessObject instances
	 */
	public void bindPersistentToDAO(Class iPersistentClass, Class iDAOClass);

	/**
	 * Returns whether this DAO factory should use cached DAOs.
	 * @return whether this DAO factory should use cached DAOs
	 */
	public boolean getShouldCacheDAOs();
	
	/**
	 * Assigns whether this DAO factory should use cached DAOs.
	 * @param iShouldCache whether this DAO factory should use cached DAOs
	 */
	public void setShouldCacheDAOs(boolean iShouldCache);
}