/*
 * SqlHelper.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data.sql;

import java.util.Iterator;
import java.util.Map;

/**
 * Provides methods for building SQL statements and various other helpful
 * utility methods.
 *
 * @author Mark Ayzenshtat
 */
public class SqlHelper {
	/* The initial size of the StringBuffer used to build a statement. */
	private static final int kInitialBufferSize = 200;

	/**
	 * Ensures that quote characters in statements are properly escaped.
	 *
	 * @param iStr the input string
	 * @return the original string with properly escaped quote characters
	 */
	public static String escapeString(String iStr) {
		StringBuffer sb = new StringBuffer(iStr);
		for (int i = 0; i < sb.length(); ) {
			if (sb.charAt(i) == '\'') {
				sb.insert(i, '\'');
				i += 2;
				continue;
			}

			i++;
		}

		return sb.toString();
	}
	
	/**
	 * If the input is <em>some_string</em>, 
	 * the output is <em>'some_string'</em>.
	 *
	 * @param iStr the input string
	 * @return the original string wrapped in single quotes.
	 */
	public static String wrapInQuotes(String iStr) {
		// length of original string + 2 (for the 2 single-quotes)
		StringBuffer sb = new StringBuffer(iStr.length() + 2);
		sb.append('\'').append(iStr).append('\'');
		
		return sb.toString();
	}
	
	/**
	 * Ensures that quote characters in the supplied string are properly
	 * escaped and then wraps the string in single quotes.  Calling this method
	 * on a string is exactly the same as calling <code>escapeString(str)</code>
	 * followed by <code>wrapInQuotes(str)</code>.
	 *
	 * @param iStr the string to prepare
	 * @return the prepared string
	 */
	public static String prepareString(String iStr) {
	    if (iStr == null) return "NULL";
	    return wrapInQuotes(escapeString(iStr));
	}

	/**
	 * Builds a <code>SELECT</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>SELECT iCols[0], iCols[1], ... , iCols[iCols.length - 1]
	 * FROM iTable</code>
	 *
	 * @param iCols an array of column names
	 * @param iTable the table name
	 * @return the constructed SELECT statement
	 */
	public static String select(String[] iCols, String iTable) {
		return baseSelect(iCols, iTable).toString();
	}

	/**
	 * Builds a <code>SELECT</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>SELECT iCols[0], iCols[1], ... , iCols[iCols.length - 1]
	 * FROM iTable WHERE iWhere</code>
	 *
	 * @param iCols an array of column names
	 * @param iTable the table name
	 * @param iWhere a condition string
	 * @return the constructed SELECT statement
	 */
	public static String select(String[] iCols, String iTable, String iWhere) {
		return baseSelect(iCols, iTable).append(" WHERE ")
			.append(iWhere).toString();
	}

	/**
	 * Builds a <code>SELECT</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>SELECT iCols[0], iCols[1], ... , iCols[iCols.length - 1]
	 * FROM iTable WHERE iWhere ORDER BY iOrderBy (!iAscendingOrder : DESC)
	 * </code>
	 *
	 * @param iCols an array of column names
	 * @param iTable the table name
	 * @param iWhere a condition string
	 * @param iOrderBy the name of the column to order by
	 * @param iAscendingOrder sort in ascending or descending order?
	 * @return the constructed SELECT statement
	 */
	public static String select(String[] iCols, String iTable, String iWhere,
			String iOrderBy, boolean iAscendingOrder) {
		StringBuffer sb = baseSelect(iCols, iTable).append(" WHERE ")
			.append(iWhere).append(" ORDER BY ").append(iOrderBy);

		if (!iAscendingOrder) {
			sb.append(" DESC");
		}

		return sb.toString();
	}

	/**
	 * Builds a <code>SELECT</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>SELECT iCols[0], iCols[1], ... , iCols[iCols.length - 1]
	 * FROM iTable WHERE iWhere ORDER BY iOrderBy</code>
	 *
	 * @param iCols an array of column names
	 * @param iTable the table name
	 * @param iWhere a condition string
	 * @param iOrderBy the name of the column to order by in ascending order
	 * @return the constructed SELECT statement
	 */
	public static String select(String[] iCols, String iTable, String iWhere,
			String iOrderBy) {
		return select(iCols, iTable, iWhere, iOrderBy, true);
	}

	/*
	 * A base generation method on top of which more complicated SELECT
	 * statements are built.
	 */
	private static StringBuffer baseSelect(String[] iCols, String iTable) {
		StringBuffer query = new StringBuffer(kInitialBufferSize);

		query.append("SELECT ");

    int n = iCols.length - 1;
		for (int i = 0; i < n; i++) {
      query.append(iCols[i]).append(", ");
		}

		query.append(iCols[n]).append(" FROM ").append(iTable);

		return query;
	}

	/**
	 * Builds an <code>UPDATE</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>UPDATE iTable SET iSet WHERE iWhere</code>
	 *
	 * @param iTable the table name
	 * @param iSet a set of column=value assignments
	 * @param iWhere a condition string
	 * @return the constructed UPDATE statement
	 */
	public static String update(String iTable, String iSet, String iWhere) {
		StringBuffer statement = new StringBuffer(kInitialBufferSize);

		statement.append("UPDATE ").append(iTable).append(" SET ").append(iSet)
			.append(" WHERE ").append(iWhere);

		return statement.toString();
	}

	/**
	 * Builds an <code>UPDATE</code> statement, drawing from the supplied
	 * <code>Map</code> for the mapping of columns to values.
	 *
	 * @param iTable the table name
	 * @param iSet the Map that represents the SET clause
	 * @param iWhere a condition string
	 * @return the constructed UPDATE statement
	 */
	public static String update(String iTable, Map iSet, String iWhere) {
    StringBuffer setClause = new StringBuffer(iSet.size() * 25);

		String key;
		Iterator iterator = iSet.keySet().iterator();
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			setClause.append(key).append(" = ").append(iSet.get(key));
      if (iterator.hasNext()) {
				setClause.append(", ");
			}
		}

		return update(iTable, setClause.toString(), iWhere);
	}
	
	/**
	 * Builds an <code>UPDATE</code> statement, drawing from the supplied
	 * string arrays for columns and values.
	 *
	 * @param iTable the table name
	 * @param iCols an array of column names
	 * @param iVals an array of values
	 * @param iWhere a condition string
	 * @return the constructed UPDATE statement
	 * @exception IllegalArgumentException if <code>iCols.length !=
	 * iVals.length</code>
	 */
	public static String update(String iTable, String[] iCols,
			String[] iVals, String iWhere) {		
		if (iCols.length != iVals.length) {
			throw new IllegalArgumentException(
				"Column and value arrays must have the same length."
			);
		}
		
		StringBuffer setClause = new StringBuffer(iCols.length * 25);
		
		for (int i = 0; i < iCols.length; i++) {
			setClause.append(iCols[i]).append(" = ").append(iVals[i]);
			if ((i + 1) < iCols.length) {
				setClause.append(", ");
			}
		}
		
		return update(iTable, setClause.toString(), iWhere);
	}

	/**
	 * Builds a <code>DELETE</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>DELETE FROM iTable WHERE iWhere</code>
	 *
	 * @param iTable the table name
	 * @param iWhere a condition string
	 * @return the constructed DELETE statement
	 */
	public static String delete(String iTable, String iWhere) {
		StringBuffer statement = new StringBuffer(kInitialBufferSize);

		statement.append("DELETE FROM ").append(iTable).append(" WHERE ")
			.append(iWhere);

		return statement.toString();
	}

	/**
	 * Builds an <code>INSERT</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>INSERT INTO iTable (iCols[0], ... ,
	 * iCols[iCols.length - 1]) VALUES (iVals[0], ... ,
	 * iVals[iVals.length - 1])</code>
	 *
	 * @param iTable the table name
	 * @param iCols an array of column names
	 * @param iVals an array of values
	 * @return the constructed INSERT statement
	 * @exception IllegalArgumentException if <code>iCols.length !=
	 * iVals.length</code>
	 */
	public static String insert(String iTable, String[] iCols, String[] iVals) {
		if (iCols.length != iVals.length) {
			throw new IllegalArgumentException(
				"Column and value arrays must have the same length."
			);
		}
		
		StringBuffer statement = new StringBuffer(kInitialBufferSize);
		int n;

		statement.append("INSERT INTO ").append(iTable).append(" (");

		n = iCols.length - 1;
		for (int i = 0; i < n; i++) {
			statement.append(iCols[i]).append(", ");
		}

		statement.append(iCols[n]).append(") VALUES (");

		n = iCols.length - 1;
		for (int i = 0; i < n; i++) {
			statement.append(iVals[i]).append(", ");
		}

		statement.append(iVals[n]).append(")");

		return statement.toString();
	}
	
	/**
	 * Builds a <code>CREATE TABLE</code> statement of the form:<br>
	 * &nbsp;&nbsp;<code>CREATE TABLE iTable (iCols[0] iTypes[0],
	 * iCols[1] iTypes[1], ... , iCols[iCols.length - 1]
	 * iTypes[iTypes.length - 1])</code>
	 *
	 * @param iTable the table name
	 * @param iCols an array of column names
	 * @param iTypes a corresponding array of column types
	 * @return the constructed CREATE TABLE statement
	 * @exception IllegalArgumentException if <code>iCols.length !=
	 * iTypes.length</code>
	 */
	public static String create(String iTable, String[] iCols, String[] iTypes) {
		if (iCols.length != iTypes.length) {
			throw new IllegalArgumentException(
				"Column and type arrays must have the same length."
			);
		}
		
		StringBuffer statement = new StringBuffer(kInitialBufferSize);
		
		statement.append("CREATE TABLE ").append(iTable).append(" (");
		
		int n = iCols.length - 1;
		for (int i = 0; i < n; i++) {
			statement.append(iCols[i]).append(" ").append(iTypes[i]).append(", ");
		}
		
		statement.append(iCols[n]).append(" ").append(iTypes[n]).append(")");
		
		return statement.toString();
	}
}