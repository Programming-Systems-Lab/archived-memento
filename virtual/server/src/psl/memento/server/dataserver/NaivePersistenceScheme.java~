/*
 * NaivePersistenceScheme.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

/**
 * A naive persistence scheme performs no decision-making when it comes to
 * passing data to the persistence grunt; it merely persists every object
 * handed to it.
 *
 * @author Mark Ayzenshtat
 */
class NaivePersistenceScheme implements PersistenceScheme {
	private DataServer mDataServer;
	
	NaivePersistenceScheme(DataServer iDataServer) {
		mDataServer = iDataServer;
	}
	
	/**
	 * Potentially stores the supplied <code>Persistent</code> object according
	 * to some predetermined persistence strategy.  Calling this method does
	 * not guarantee that the supplied object will be stored.
	 *
	 * @param iP the object to potentially store
	 */
	public void smartStore(Persistent iP) throws DataAccessException {
		DAOFactory factory = mDataServer.getDAOFactory();
		DataAccessObject dao = factory.getDAO(iP.getClass());
		dao.store(iP);
	}

	/**
	 * Potentially stores all of the supplied <code>Persistent</code> objects
	 * (which must all be of the same class) according to some predetermined
	 * persistence strategy.  Calling this method does not guarantee that the
	 * supplied objects will be stored.
	 *
	 * @param iP the objects to potentially store
	 */
	public void smartStore(Persistent[] iPs) throws DataAccessException {
		if (iPs.length == 0) {
			return;
		}
		
		DAOFactory factory = mDataServer.getDAOFactory();
		DataAccessObject dao = factory.getDAO(iPs[0].getClass());
		
		for (int i = 0; i < iPs.length; i++) {
			dao.store(iPs[i]);
		}
	}	
}