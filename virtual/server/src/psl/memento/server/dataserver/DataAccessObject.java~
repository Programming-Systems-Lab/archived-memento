/*
 * DataAccessObject.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

/**
 * Manages the persistence of <code>Persistent</code> objects.
 * Concrete implementations of this interface will communicate with some kind
 * of underlying data store.  However, if client code is written to the
 * interface and not to the implementation, the user shouldn't care what the
 * backing data store is.  In this manner, we can decouple data access logic 
 * from client code that depends on it.  Use the <code>DAOFactory</code> API
 * to acquire functional <code>DataAccessObject</code> instances.<br>
 * <br>
 * This object conforms to the J2EE Data Access Object
 * <a href="http://java.sun.com/blueprints/patterns/j2ee_patterns/data_access_object/">
 * design pattern</a>.
 *
 * @author Mark Ayzenshtat 
 */
public interface DataAccessObject {
	/**
	 * Loads the persistent object with the given ID from the
	 * backing data store.
	 *
	 * @param iID the ID
	 * @return the loaded persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public Persistent load(int iID) throws DataAccessException;
	
	/**
	 * Saves the given persistent object to the backing data store.
	 *
	 * @param iR the persistent object to save
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void store(Persistent iR) throws DataAccessException;
	
	/**
	 * Creates a persistent object in the backing data store.
	 *
	 * @return the ID that uniquely identifies the created persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public int create() throws DataAccessException;

	/**
	 * Deletes a persistent object from the backing data store.
	 *
	 * @param iID the ID that uniquely identifies the persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void delete(int iID) throws DataAccessException;
}