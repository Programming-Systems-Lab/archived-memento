/*
 * JdbcVemMapDAO.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import java.sql.*;
import psl.chime4.server.auth.User;
import psl.chime4.server.vem.*;
import psl.chime4.server.data.sql.SqlHelper;

/**
 * Implements VemMapDAO using a JDBC database connection
 *
 * @author Vladislav Shchogolev
 * @version 1.0
 */

public class JdbcVemMapDAO extends AbstractJdbcDAO
		implements VemMapDAO {
  // table names
  private static final String kTableVMs = "VemMappings";

  // column names
  private static final String kColVMsMapID = "MapID";
  private static final String kColVMsUID = "UserID";
  private static final String kColVMsPattern = "Pattern";
  private static final String kColVMsPriority = "Priority";
  private static final String kColVMsType = "VemType";
  private static final String kColVMsSubType = "SubType";
  private static final String kColVMsShape = "ModelID";
  private static final String kColVMsShape2D = "ImageID";
    
	// prepared statement for searching
	private PreparedStatement searchStmt;
	
	/*
	 * Package-scope constructor to ensure instantiation through factory.
	 */
	JdbcVemMapDAO() {
		super();
	}

	/**
	 * Creates a persistent object in the backing data store.
	 *
	 * @return the ID that uniquely identifies the created persistent object
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public int create() throws DataAccessException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		// get a connection
		conn = getConnection();
		
		try {
			stmt = conn.createStatement();
			
			// create the row
			stmt.executeUpdate(buildCreateMapStatement());
			
			// get the ID of the row we just created
			rs = stmt.executeQuery(buildGetMaxIDQuery());
			rs.next();
			
			return rs.getInt(1);
		} catch (SQLException ex) {
			throw new DataAccessException("Error creating mapping.", ex);
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
	public void delete(int iID) throws DataAccessException
	{
		//FIXME: Implement
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
	public Persistent load(int iID) throws DataAccessException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		// milk the connection source
		conn = getConnection();
				
		// load the mapping
		try {
			stmt = conn.createStatement();			
			rs = stmt.executeQuery(buildLoadMapQuery(iID));			
			rs.next();
			
			VemData data = new VemData();
			data.setType(VemType.getTypeForCode(rs.getInt(kColVMsType)));
			data.setSubType(rs.getString(kColVMsSubType).charAt(0));
			data.setModelID(rs.getInt(kColVMsShape));
			data.setImageID(rs.getInt(kColVMsShape2D));
			
			// instantiate an new VemMap object
			VemMap m = new VemMap(
				rs.getInt(kColVMsUID), rs.getString(kColVMsPattern), 
				rs.getInt(kColVMsPriority),	data);			
			
			// copy values from the result set into the User object
			m.setPersistenceID(iID);
			
			return m;
		} catch (SQLException ex) {			
			throw new DataAccessException("Error loading mapping.", ex);
		} finally {
			cleanUp(conn, stmt, rs);
		}
	}
	
	/**
	 * Saves the given persistent object to the backing data store.
	 *
	 * @param iR the persistent object to save
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public void store(Persistent iP) throws DataAccessException
	{
		// ClassCastException will be thrown here if cast fails
		VemMap m = (VemMap) iP;		
		
		Connection conn = null;
		Statement stmt = null;
		
		conn = getConnection();
				
		try {
			stmt = conn.createStatement();
			
			// store user
			stmt.executeUpdate(buildStoreMapStatement(m));
		} catch (SQLException ex) {			
			throw new DataAccessException("Error storing mapping.", ex);
		} finally {
			cleanUp(conn, stmt, null);
		}
	}
	
	/**
	 * Ensures that data tables exist.  If they do not exist, it is expected
	 * that they will after this method completes.
	 */
	protected void ensureTablesExist() throws DataAccessException
	{
		Connection conn = null;
		Statement stmt = null;
		
		// get a connection
		conn = getConnection();
				
		try {
			stmt = conn.createStatement();			
			
			// attempt to create users table
			try {
				stmt.execute(buildCreateMapTableStatement());
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
	

    public int search(int iUID, String iType, String iURI, VemType iVT) 
	throws DataAccessException {
        Connection conn = getConnection();
		ResultSet rs = null;
		
        try {
            prepareSearch(conn, iUID, iType, iURI, iVT);
            rs = searchStmt.executeQuery();
            if (rs.next()) {	// grab first row of the table
                return rs.getInt(kColVMsMapID);
            } else
                return -1;
        } catch (SQLException sqle) {
            throw new DataAccessException("Error finding mapping", sqle);
        } finally {
            cleanUp(conn, searchStmt, rs);
        }
    }
	
    private void prepareSearch(Connection conn, int iU, String iT, String iURI, VemType iVT) 
    throws SQLException {
        if (searchStmt == null) {	// if first time use, create new statement
            String cols[] = {
                kColVMsMapID, kColVMsPriority
            };
            
            String whereClause = 
				"("+kColVMsUID+" = "+User.GLOBAL_ID+" OR "+kColVMsUID+" = '?') AND " +
				"("+kColVMsPattern+" = '?' OR "+kColVMsPattern+" = '?' OR "+kColVMsPattern+" = NULL) AND " +
				"("+kColVMsType+" = '"+iVT.toInt()+"')";
            
            // order by priority (2nd column)
            searchStmt = conn.prepareStatement(
				SqlHelper.select(cols, kTableVMs, whereClause, "2"));
        }
        
        // parameter indices refer to the ? in the whereClause string
        searchStmt.setInt(1, iU);
        searchStmt.setString(2, iT);
		searchStmt.setString(3, iURI);
    }	
	
	// query/statement builders -- methods that neatly assemble the SQL
	// statements that we're sending to the DB
	private String buildCreateMapTableStatement() {
		// table columns...
		String columns[] = {
			kColVMsMapID, kColVMsUID, kColVMsPriority, kColVMsPattern, 
			kColVMsType, kColVMsSubType, kColVMsShape, kColVMsShape2D
		};
		
		// ...and their corresponding types
		String[] types = {
			"integer PRIMARY KEY AUTO_INCREMENT", "integer", "integer", "varchar(250)",
			"integer", "varchar(10)", "integer", "integer"
		};
		
		return SqlHelper.create(kTableVMs, columns, types);
	}	

	private String buildLoadMapQuery(int iID) {
		String columns[] = {
			kColVMsUID, kColVMsPriority, kColVMsPattern, 
			kColVMsType, kColVMsSubType, kColVMsShape, kColVMsShape2D
		};

		String whereClause = (new StringBuffer(kColVMsMapID)).append(" = ")
			.append(iID).toString();
		
		return SqlHelper.select(columns, kTableVMs, whereClause);
	}

	private String buildStoreMapStatement(VemMap iU) {
		String columns[] = {
			kColVMsUID, kColVMsPriority, kColVMsPattern, 
			kColVMsType, kColVMsSubType, kColVMsShape, kColVMsShape2D
		};
		
		String[] values = {
			String.valueOf(iU.getUserID()),
			String.valueOf(iU.getPriority()),
			SqlHelper.prepareString(iU.getPattern()),	//FIXME: find out if mark made change i wanted in SQLHelper
			String.valueOf(iU.getVemData().getType().toInt()),
			SqlHelper.prepareString(iU.getVemData().getSubType() + ""),
			String.valueOf(iU.getVemData().getModelID()),
			String.valueOf(iU.getVemData().getImageID())
		};
		
		String whereClause = (new StringBuffer(kColVMsMapID)).append(" = ")
			.append(iU.getPersistenceID()).toString();
		
		return SqlHelper.update(kTableVMs, columns, values, whereClause);
	}
	
	private String buildCreateMapStatement() {
		String[] columns = {
			kColVMsPriority
		};
		
		String[] values = {
			String.valueOf(999)		// dummy value, so we can complete the INSERT
		};
		
		return SqlHelper.insert(kTableVMs, columns, values);
	}
	
	private String buildGetMaxIDQuery() {
		return new StringBuffer(50).append("SELECT MAX(").append(kColVMsMapID)
			.append(") FROM ").append(kTableVMs).toString();
	}
	
	private String buildDeleteMapStatement(int iID) {
		String whereClause = new StringBuffer(50).append(kColVMsMapID)
			.append(" = ").append(iID).toString();
		
		return SqlHelper.delete(kTableVMs, whereClause);
	}	
}