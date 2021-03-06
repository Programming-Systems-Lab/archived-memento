/*
 * ConnectionSource.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.memento.server.dataserver.sql;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

/** 
 * An abstraction that represents some DB connection source.  Clients of 
 * <code>ConnectionSource</code> don't care how connections are obtained; 
 * the details are handled by concrete implementations.  For instance, we
 * could create connection source implementations that use JDBC 1.0 
 * (<code>DriverManager.getConnection()</code>), JDBC 2.0 
 * (<code>DataSource.getConnection()</code>), some connection pool, etc.
 * Classes that implement this interface must provide a zero-argument,
 * package-scope constructor to be properly instantiated.<br>
 * <br>
 * Proper, safe use of <code>ConnectionSource</code> objects will look 
 * something like this:<br>
 * <br>
 * <code>
 * ConnectionSource connSource = ConnectionSourceFactory.getInstance().getConnectionSource();<br>
 * Connection conn = connSource.obtainConnection();<br>
 * try {<br>
 * &nbsp;&nbsp;<em>do stuff with connection</em><br>
 * } finally {<br>
 * &nbsp;&nbsp;connSource.releaseConnection(conn);<br>
 * }<br>
 * </code>
 *
 * @author Mark Ayzenshtat
 */
public interface ConnectionSource {
	/**
	 * Obtains a DB connection.
	 *
	 * @return the connection
	 * @exception SQLException if a database error occurs
	 */
	public Connection obtainConnection(String iJdbcURL) throws SQLException;
  
  public Connection obtainConnection(URI iDbURI) throws SQLException;
	
	/**
	 * Instructs this connection source to perform clean-up on the supplied
	 * connection.
	 *
	 * @param iConn the connection to release
	 */
	public void releaseConnection(Connection iConn) throws SQLException;
}