/*
 * JdbcOneConnectionSource.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.memento.server.dataserver.sql;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * A <code>ConnectionSource</code> implementation that uses <code>
 * DriverManager.getConnection()</code> from the JDBC 1.0 API.
 *
 * @author Mark Ayzenshtat
 */
public class JdbcOneConnectionSource extends ConnectionSourceBase
    implements ConnectionSource {
 	public JdbcOneConnectionSource(VendorCoupler iVC) {
    super(iVC);    
    loadDriver(iVC.getDriverClassName());
	}
  
/*  
  public JdbcOneConnectionSource(String iDriverClassName) {
    super(null);
    loadDriver(iDriverClassName);
  }
*/
  
  private void loadDriver(String iDriverClassName) {
		try {
			Class.forName(iDriverClassName);      
		} catch (ClassNotFoundException ex) {
			throw new java.lang.RuntimeException("Cannot load the DB driver.", ex);
		}
  }
	
	/**
	 * Obtains a DB connection.
	 *
	 * @return the connection
	 * @exception SQLException if a database error occurs
	 */
	public Connection obtainConnection(String iJdbcURL) throws SQLException {
		return DriverManager.getConnection(iJdbcURL);
	}
	
	/**
	 * Instructs this connection source to perform clean-up on the supplied
	 * connection.
	 *
	 * @param iConn the connection to release
	 */
	public void releaseConnection(Connection iConn) throws SQLException {
		// since this is just a simple JDBC 1.0 implementation, there's no
		// pool clean-up involved -- simply close the connection if it isn't
		// already closed
		if (!iConn.isClosed()) {
			iConn.close();
		}
	}
}