/*
 * ConnectionSourceFactory.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data.sql;

import java.util.prefs.Preferences;

/**
 * A factory for creating <code>ConnectionSource</code> objects.
 *
 * @author Mark Ayzenshtat
 * @see ConnectionSource
 */
public class ConnectionSourceFactory {
	private static final Preferences kPrefs =
		Preferences.userNodeForPackage(ConnectionSourceFactory.class);
	
	private static final ConnectionSourceFactory kSingleton =
		new ConnectionSourceFactory();
	
	private static final String kKeyConnectionSourceClassName =
		"ConnectionSourceClassName";
	
	private static final String kDefaultConnectionSourceClassName =
		"psl.chime4.server.data.sql.PooledJdbcOneConnectionSource";
	
	private ConnectionSource mCS;
	
	// prevent external instantiation
	private ConnectionSourceFactory() {		
		String className = 
			kPrefs.get(kKeyConnectionSourceClassName, 
			kDefaultConnectionSourceClassName);
		
		Class csClass;		
		try {
			csClass = Class.forName(className);
		} catch (ClassNotFoundException ex) {
			try {
				csClass = Class.forName(kDefaultConnectionSourceClassName);
			} catch (ClassNotFoundException ex2) {
				throw new RuntimeException(
					"Cannot resolve a ConnectionSource subclass.", ex2
				);
			}
		}
		
		ConnectionSource cs;
		try {
			cs = (ConnectionSource) csClass.newInstance();
		} catch(InstantiationException ex) {
			throw new RuntimeException(
				"Cannot instantiate a ConnectionSource subclass.", ex
			);
		} catch(IllegalAccessException ex) {
			throw new RuntimeException(
				"Cannot instantiate a ConnectionSource subclass.", ex
			);
		}
		
		mCS = cs;
	}
	
	/**
	 * Retrieves the singleton instance of this factory.
	 *
	 * @return the singleton instance of this factory
	 */
	public static ConnectionSourceFactory getInstance() {
		return kSingleton;
	}
	
	/**
	 * Retrieves a valid <code>ConnectionSource</code> object.
	 *
	 * @return a valid <code>ConnectionSource</code> object
	 */
	public ConnectionSource getConnectionSource() {
		return mCS;
	}
}