/*
 * JdbcResourceDescriptorDAO.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import java.sql.*;
import java.util.*;
import psl.chime4.server.librarian.*;
import psl.chime4.server.data.metadata.*;
import psl.chime4.server.data.metadata.ResourceDescriptor;
import psl.chime4.server.data.metadata.ResourceDescriptorFactory;
import psl.chime4.server.data.sql.*;

/** 
 * Manages the persistence of <code>ResourceDescriptor</code> objects to a
 * relational database through JDBC.
 *
 * @author Mark Ayzenshtat 
 */
public class JdbcResourceDescriptorDAO extends AbstractJdbcDAO
		implements ResourceDescriptorDAO {	
	// table names
	private static final String kTableRDs = "ResourceDescriptors";
	private static final String kTableMoreData = "RDAdditionalData";
	
	// column names
	private static final String kColRDsID = "ID";
  private static final String kColRDsName = "Name";
  private static final String kColRDsProtocol = "Protocol";
  private static final String kColRDsSize = "Size";
  private static final String kColRDsType = "Type";
  private static final String kColRDsWhenLastModified = "WhenLastModified";
  private static final String kColRDsHasMoreData = "HasMoreData";
  
	private static final String kColMoreDataID = "ID";
	private static final String kColMoreDataValue = "Data";
	private static final String kColMoreDataOrder = "DataOrder";
	
	private static final String[] kZeroStringArray = new String[0];
	
	/*
	 * Package-scope constructor to ensure instantiation through factory.
	 */
	JdbcResourceDescriptorDAO() {
		super();
	}
	
	/**
	 * Loads the resource descriptor with the given ID from the 
	 * backing data store.
	 *
	 * @param iID the ID
	 * @return the loaded resource descriptor
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 */
	public Persistent load(int iID) throws DataAccessException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		// milk the connection source
		conn = getConnection();
				
		// load the ResourceDescriptor
		try {
			stmt = conn.createStatement();			
      rs = stmt.executeQuery(buildLoadRDQuery(iID));
			rs.next();
			
			// instantiate a type-specific ResourceDescriptor subclass
			String type = rs.getString(kColRDsType);
			ResourceDescriptor rd = ResourceDescriptorFactory
				.getInstance().newRD(type);
			
			// copy values from the result set into the ResourceDescriptor object      
			rd.setPersistenceID(iID);
      rd.setName(rs.getString(kColRDsName));
      rd.setProtocol(rs.getString(kColRDsProtocol));
      rd.setSize(rs.getInt(kColRDsSize));
      rd.setType(rs.getString(kColRDsType));
      rd.setWhenLastModified(rs.getTimestamp(kColRDsWhenLastModified));
			
			// copy additional data, if it exists
			boolean hasMoreData = rs.getBoolean(kColRDsHasMoreData);
			if (hasMoreData) {
				List additionalDataList = new ArrayList(5);
				
				rs = stmt.executeQuery(buildLoadMoreDataQuery(iID));
				while (rs.next()) {
					additionalDataList.add(rs.getString(kColMoreDataValue));
				}
				
				String[] additionalData = (String[]) 
					additionalDataList.toArray(kZeroStringArray);
				rd.setAdditionalData(additionalData);
			}
			
			return rd;
		} catch (SQLException ex) {			
			throw new
				DataAccessException("Error loading ResourceDescriptor.", ex);
		} finally {
			cleanUp(conn, stmt, rs);
		}
	}
	
	/**
	 * Saves the given resource descriptor to the backing data store.
	 *
	 * @param iP the resource descriptor to save
	 * @exception DataAccessException if this operation cannot complete
	 * due to a failure in the backing store
	 * @exception ClassCastException if the supplied <code>Persistent</code>
	 * object is not a <code>ResourceDescriptor</code>
	 */
	public void store(Persistent iP) throws DataAccessException {
		// ClassCastException will be thrown here if cast fails
		ResourceDescriptor r = (ResourceDescriptor) iP;
		
		Connection conn = null;
		Statement stmt = null;
		
		// get a connection
		conn = getConnection();
				
		try {
			stmt = conn.createStatement();
			
			String[] moreData = r.getAdditionalData();
			boolean hasMoreData = moreData.length > 0;
			
			// store resource descriptor
			stmt.executeUpdate(buildStoreRDStatement(r, hasMoreData));
			
			// store additional data if necessary			
			if (hasMoreData) {
				stmt.executeUpdate(buildStoreMoreDataStatement(moreData));
			}      
		} catch (SQLException ex) {			
			throw new
				DataAccessException("Error storing resource descriptor.", ex);
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
			stmt.executeUpdate(buildCreateRDStatement());
			
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
			stmt.executeUpdate(buildDeleteRDStatement(iID));
		} catch (SQLException ex) {
			throw new DataAccessException("Error deleting user.", ex);
		} finally {
			cleanUp(conn, stmt, null);
		}
  }

	/**
	 * Carries out a librarian search on the local data store.
	 *
	 * @param iRequest the object encapsulating the search parameters
	 * @return the object encapsulating the search results
	 */
	public LibrarianResult doLibrarianSearch(LibrarianRequest iRequest) {
		String query = iRequest.getQuery();
		
		LibrarianResult result = new LibrarianResult();
		result.setSourceRequest(iRequest);
		
		//TODO: Implement me
		
		return result;
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
			
			// attempt to create resource descriptors table
			try {
				stmt.execute(buildCreateRDsTableStatement());
			} catch (SQLException ex2) {
				// if an exception is thrown, it means the table already exists				
			}
			
			// attempt to create additional data table
			try {
				stmt.execute(buildCreateMoreDataTableStatement());
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

	// query/statement builders -- methods that neatly assemble the SQL
	// statements that we're sending to the DB
	private String buildLoadRDQuery(int iID) {
		String[] columns = {
      kColRDsName, kColRDsProtocol, kColRDsSize,
      kColRDsType, kColRDsWhenLastModified, kColRDsHasMoreData
		};

		String whereClause = (new StringBuffer(kColRDsID)).append(" = ")
			.append(iID).toString();
		
		return SqlHelper.select(columns, kTableRDs, whereClause);
	}
	
	private String buildStoreRDStatement(ResourceDescriptor iRD, 
			boolean iHasMoreData) {
		String[] moreData = iRD.getAdditionalData();
		
		String[] columns = {
      kColRDsName, kColRDsProtocol, kColRDsSize,
      kColRDsType, kColRDsWhenLastModified, kColRDsHasMoreData
		};
		
		String[] values = {
      SqlHelper.prepareString(iRD.getName()),
      SqlHelper.prepareString(iRD.getProtocol()),
      String.valueOf(iRD.getSize()),
      SqlHelper.prepareString(iRD.getType()),
      SqlHelper.wrapInQuotes(
        new Timestamp(iRD.getWhenLastModified().getTime()).toString()),
      String.valueOf((iHasMoreData) ? 1 : 0)
		};
		
		String whereClause = (new StringBuffer(kColRDsID)).append(" = ")
			.append(iRD.getPersistenceID()).toString();
		
		return SqlHelper.update(kTableRDs, columns, values, whereClause);
	}
	
	private String buildStoreMoreDataStatement(String[] iAdditionalData) {
		// TODO: Implement me
		return null;
	}
	
	private String buildLoadMoreDataQuery(int iID) {
		String[] columns = { kColMoreDataValue };
		String whereClause = (new StringBuffer(kColMoreDataID)).append(" = ")
			.append(iID).toString();
		
		return SqlHelper.select(columns, kTableMoreData, 
			whereClause, kColMoreDataOrder);
	}
	
	private String buildCreateRDsTableStatement() {
		// table columns...
		String[] columns = {
      kColRDsID, kColRDsName, kColRDsProtocol,
      kColRDsSize, kColRDsType, kColRDsWhenLastModified,
      kColRDsHasMoreData
    };

    // ...and their corresponding types
		String[] types = {
			"integer PRIMARY KEY AUTO_INCREMENT", "varchar(100)", "varchar(20)",
      "integer", "varchar(50)", "timestamp", "bit"
		};
		
		return SqlHelper.create(kTableRDs, columns, types);
	}
	
	private String buildCreateMoreDataTableStatement() {
		// table columns...
		String[] columns = {
			kColMoreDataID, kColMoreDataValue, kColMoreDataOrder
		};
		
		// ...and their corresponding types
		String[] types = {
			"integer PRIMARY KEY AUTO_INCREMENT", "varchar(255)", "integer"
		};
		
		return SqlHelper.create(kTableMoreData, columns, types);
	}

	private String buildCreateRDStatement() {
		String[] columns = {
      kColRDsSize			
		};
		
		String[] values = {
			String.valueOf(0)		// dummy value, so we can complete the INSERT
		};
		
		return SqlHelper.insert(kTableRDs, columns, values);
	}
	
	private String buildGetMaxIDQuery() {
		return new StringBuffer(50).append("SELECT MAX(").append(kColRDsID)
			.append(") FROM ").append(kTableRDs).toString();
	}
	
	private String buildDeleteRDStatement(int iID) {
		String whereClause = new StringBuffer(50).append(kColRDsID)
			.append(" = ").append(iID).toString();
		
		return SqlHelper.delete(kTableRDs, whereClause);
	}
  
  public static void main(String[] args) {
    ResourceDescriptorDAO dao = new JdbcResourceDescriptorDAO();    
    
    try {      
      int id = dao.create();
      System.out.println("ID = " + id);

/*      
      ResourceDescriptor rd = (ResourceDescriptor) dao.load(0);
      rd.setName("Name of Resource Descriptor");
      rd.setSize(1234);
      rd.setProtocol("HTTP");
      rd.setType("text/html");
      rd.setWhenLastModified(new java.util.Date());
      dao.store(rd);
*/
    } catch (DataAccessException ex) {
      ex.printStackTrace();
    }
  }
}