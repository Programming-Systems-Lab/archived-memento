/*
 * ResourceDescriptorDAO.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import psl.chime4.server.librarian.LibrarianRequest;
import psl.chime4.server.librarian.LibrarianResult;

/**
 * Manages the persistence of <code>ResourceDescriptor</code> objects.
 * Concrete implementations of this interface will communicate with some kind
 * of underlying data store.  However, if client code is written to the
 * interface and not to the implementation, the user shouldn't care what the
 * backing data store is.  In this manner, we can decouple data access logic 
 * from client code that depends on it.  Use the 
 * <code>ResourceDescriptorDAOFactory</code> API to acquire functional
 * <code>ResourceDescriptorDAO</code> objects.<br>
 * <br>
 * This object conforms to the J2EE Data Access Object
 * <a href="http://java.sun.com/blueprints/patterns/j2ee_patterns/data_access_object/">
 * design pattern</a>.
 *
 * @author Mark Ayzenshtat
 * @see ResourceDescriptorDAOFactory
 */
public interface ResourceDescriptorDAO extends DataAccessObject {
	/**
	 * Carries out a librarian search on the local data store.
	 *
	 * @param iRequest the object encapsulating the search parameters
	 * @return the object encapsulating the search results
	 */
	public LibrarianResult doLibrarianSearch(LibrarianRequest iRequest);
}
