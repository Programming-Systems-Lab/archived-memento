/*
 * JdbcUserDAO.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import java.sql.*;
import psl.chime4.server.auth.User;
import psl.chime4.server.data.sql.*;

/**
 * Manages the persistence of <code>User</code> objects to a relational
 * database via JDBC.
 *
 * @author Mark Ayzenshtat
 * @see psl.chime4.server.auth.User
 */
public class JdbcUserDAO extends AbstractJdbcDAO
		implements UserDAO {
	// table names
	private static final String kTableUsers = "Users";
	
	// column names
	private static final String kColUsersID = "ID";
	private static final String kColUsersUserName = "UserName";
	private static final String kColUsersPassword = "Password";
	private static final String kColUsersPublicKey = "PublicKey";
	private static final String kColUsersAccessModes = "AccessModes";
		
	/*
	 * Package-scope constructor to ensure instantiation through factory.
	 */
	JdbcUserDAO() {
		super();
	}

	/**
	 * Ensures that data tables exist.  If they do not exist, it is expected
	 * that they will after this method completes.
	 */
	protected void ensureTablesExist() throws DataAccessException {
		Connection conn = null;
		Statement stmt = null;
		
		// get a connection
		conn = getConnection();
				
		try {
			stmt = conn.createStatement();			
			
			// attempt to create users table
			try {
				stmt.execute(buildCreateUsersTableStatement());
			} catch (SQLException ex2) {
				// if an exception is thrown, it means the table already exists				
			}
		} catch (SQLException ex) {			
			throw new
				DataAccessException("Error while attempting to create tables.", ex);
		} finally {
			cleanUp(conn, stmt, null);
		}		
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
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		// milk the connection source
		conn = getConnection();
				
		// load the User
		try {
			stmt = conn.createStatement();			
			rs = stmt.executeQuery(buildLoadUserQuery(iID));			
			rs.next();
			
			// instantiate an empty User object
			User u = new User();
			
			// copy values from the result set into the User object
			u.setPersistenceID(iID);
			u.setUserID(rs.getString(kColUsersUserName));
			u.setPassword(rs.getString(kColUsersPassword));
			u.setPublicKey(rs.getBytes(kColUsersPublicKey));
			u.setValidServices(rs.getInt(kColUsersAccessModes));
			
			return u;
		} catch (SQLException ex) {			
			throw new DataAccessException("Error loading User.", ex);
		} finally {
			cleanUp(conn, stmt, rs);
		}
	}
	
	/**
	 * Saves the given persistent object to the backing data store.
	 *
	 * @param iP the persistent object to save
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void store(Persistent iP) throws DataAccessException {
		// ClassCastException will be thrown here if cast fails
		User u = (User) iP;		
		
		Connection conn = null;
		Statement stmt = null;
		
		// get a connection
		conn = getConnection();
				
		try {
			stmt = conn.createStatement();
			
			// store user
			stmt.executeUpdate(buildStoreUserStatement(u));
		} catch (SQLException ex) {			
			throw new DataAccessException("Error storing user.", ex);
		} finally {
			cleanUp(conn, stmt, null);
		}
	}
	
	/**
	 * Creates a persistent object in the backing data store.
	 *
	 * @return the ID that uniquely identifies the created persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public int create() throws DataAccessException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		// get a connection
		conn = getConnection();
		
		try {
			stmt = conn.createStatement();
			
			// create the row
			stmt.executeUpdate(buildCreateUserStatement());
			
			// get the ID of the row we just created
			rs = stmt.executeQuery(buildGetMaxIDQuery());
			rs.next();
			
			return rs.getInt(1);
		} catch (SQLException ex) {
			throw new DataAccessException("Error creating user.", ex);
		} finally {
			cleanUp(conn, stmt, rs);
		}
	}

	/**
	 * Deletes a persistent object from the backing data store.
	 *
	 * @param iID the ID that uniquely identifies the persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void delete(int iID) throws DataAccessException {
		Connection conn = null;
		Statement stmt = null;
		
		// get a connection
		conn = getConnection();
		
		try {
			stmt = conn.createStatement();
			
			// delete user
			stmt.executeUpdate(buildDeleteUserStatement(iID));
		} catch (SQLException ex) {
			throw new DataAccessException("Error deleting user.", ex);
		} finally {
			cleanUp(conn, stmt, null);
		}
	}

	// query/statement builders -- methods that neatly assemble the SQL
	// statements that we're sending to the DB
	private String buildCreateUsersTableStatement() {
		// table columns...
		String[] columns = {
			kColUsersID, kColUsersUserName, kColUsersPassword,
			kColUsersPublicKey, kColUsersAccessModes
		};
		
		// ...and their corresponding types
		String[] types = {
			"integer PRIMARY KEY AUTO_INCREMENT", "varchar(200)", "varchar(200)",
			"varbinary(32)", "integer"
		};
		
		return SqlHelper.create(kTableUsers, columns, types);
	}	

	private String buildLoadUserQuery(int iID) {
		String[] columns = {
			kColUsersUserName, kColUsersPassword,
			kColUsersPublicKey, kColUsersAccessModes
		};

		String whereClause = (new StringBuffer(kColUsersID)).append(" = ")
			.append(iID).toString();
		
		return SqlHelper.select(columns, kTableUsers, whereClause);
	}

	private String buildStoreUserStatement(User iU) {
		String[] columns = {
			kColUsersUserName, kColUsersPassword,
			kColUsersPublicKey, kColUsersAccessModes
		};
		
		String[] values = {
			SqlHelper.prepareString(iU.getUserID()),
			SqlHelper.prepareString(iU.getPassword()),
			SqlHelper.wrapInQuotes(new String(iU.getPublicKey())),
			String.valueOf(iU.getValidServices())
		};
		
		String whereClause = (new StringBuffer(kColUsersID)).append(" = ")
			.append(iU.getPersistenceID()).toString();
		
		return SqlHelper.update(kTableUsers, columns, values, whereClause);
	}
	
	private String buildCreateUserStatement() {
		String[] columns = {
			kColUsersAccessModes
		};
		
		String[] values = {
			String.valueOf(0)		// dummy value, so we can complete the INSERT
		};
		
		return SqlHelper.insert(kTableUsers, columns, values);
	}
	
	private String buildGetMaxIDQuery() {
		return new StringBuffer(50).append("SELECT MAX(").append(kColUsersID)
			.append(") FROM ").append(kTableUsers).toString();
	}
	
	private String buildDeleteUserStatement(int iID) {
		String whereClause = new StringBuffer(50).append(kColUsersID)
			.append(" = ").append(iID).toString();
		
		return SqlHelper.delete(kTableUsers, whereClause);
	}
}