/*
 * AbstractDAO.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import java.sql.*;
import psl.chime4.server.data.sql.*;

/**
 * Provides a skeletal JDBC implementation of the <code>DataAccessObject</code>
 * interface.  For convenience, JDBC-based DAOs can extend this class.
 *
 * @author Mark Ayzenshtat 
 */
public abstract class AbstractJdbcDAO implements DataAccessObject {	
	protected ConnectionSource mConnectionSource;
		
	/**
	 * Invoked by subclass constructors.  This constructor sets the value of
	 * <code>mConnectionSource</code> and calls <code>ensureTablesExist()</code>.
	 */
	protected AbstractJdbcDAO() {
		// get a connection source from the factory
		mConnectionSource = ConnectionSourceFactory.getInstance()
			.getConnectionSource();
		
		// make sure DB tables exist
		try {
			ensureTablesExist();
		} catch (DataAccessException ex) {
			throw new RuntimeException("Could not acquire tables.", ex);
		}
	}

	/**
	 * Ensures that data tables exist.  If they do not exist, it is expected
	 * that they will after this method completes.
	 */
	protected abstract void ensureTablesExist() throws DataAccessException;
	
	/**
	 * Retrieves a connection from the connection source.
	 */
	protected Connection getConnection() throws DataAccessException {		
		try {
			return mConnectionSource.obtainConnection();
		} catch (SQLException ex) {
			throw new DataAccessException("Could not obtain connection.", ex);
		}
	}
	
	/**
	 * Safely disposes of a <code>Connection</code>, <code>Statement</code>,
	 * and <code>ResultSet</code>.
	 */
	protected void cleanUp(Connection iConn, Statement iStmt, ResultSet iRS) 
			throws DataAccessException {		
		try {
			if (iRS != null) {
				iRS.close();
			}
			
			if (iStmt != null) {
				iStmt.close();
			}
			
			if (iConn != null) {
				mConnectionSource.releaseConnection(iConn);
			}
		} catch (SQLException ex) {
			throw new 
				DataAccessException("Could not dispose of connection.", ex);
		}		
	}
}