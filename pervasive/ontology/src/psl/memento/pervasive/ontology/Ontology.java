package psl.memento.pervasive.ontology;

import java.sql.*;
import java.io.*;
import java.util.*;

public class Ontology {
	//Instance variables
	private Properties settings;
	private Connection conn;
	private String ontologyName;

	//Cache
	private Hashtable nodeCache;
	private int cacheSize;
	private TreeMap cacheInsertionRecord;

	//Cache statistics
	private long cacheHits;
	private long cacheMisses;

	//Timing statistics for each method
	private long timeCreateNode;
	private long accessesCreateNode;
	private long timeGetNode;
	private long accessesGetNode;
	private long timeUpdateNode;
	private long accessesUpdateNode;
	private long timeAddProperty;
	private long accessesAddProperty;
	private long timeGetSize;
	private long accessesGetSize;

	//Default variables
	private final int DEFAULT_CACHE_SIZE = 1000;

	//SQL Prepared Statements
	private final String GET_NODE = "SELECT label FROM onto_nodes WHERE id=?";
	private final String GET_NODE_BY_LABEL = "SELECT id FROM onto_nodes WHERE label=? AND ontology_name=?";
	private final String GET_NODE_PROPS = "SELECT name, value FROM onto_nodeProps WHERE id=?";
	private final String GET_EDGES = "SELECT id, start_id, end_id FROM onto_edges WHERE start_id=? OR end_id=?";
	private final String GET_UNIQUE_EDGES = "SELECT id, start_id, end_id FROM onto_edges WHERE start_id=? AND end_id=?";
	private final String GET_EDGES_PROPS = "SELECT value FROM onto_edgeProps WHERE id=? AND name='weight'";
	private final String GET_NUMBER_NODES = "SELECT COUNT(id) FROM onto_nodes WHERE ontology_name = ?";
	private final String INSERT_NODE = "INSERT INTO onto_nodes VALUES (?,?)\nSELECT @@identity";
	private final String INSERT_EDGE = "INSERT INTO onto_edges VALUES (?,?)\nSELECT @@identity";
	private final String INSERT_NODE_PROP = "INSERT INTO onto_nodeProps VALUES (?,?,?)";
	private final String INSERT_EDGE_PROP = "INSERT INTO onto_edgeProps VALUES (?,?,?)";
	private final String UPDATE_EDGE_PROP = "UPDATE onto_edgeProps SET value=? WHERE id=? AND name='weight'";
	private final String DELETE_NODE = "DELETE FROM onto_nodes WHERE id=?";
	private final String DELETE_NODE_PROP = "DELETE FROM onto_nodeProps WHERE id=? AND name=?";
	private final String DELETE_NODE_PROPS = "DELETE FROM onto_nodeProps WHERE id=?";
	private final String DELETE_EDGES = "DELETE FROM onto_edges WHERE start_id=? OR end_id=?";
	private final String DELETE_EDGE_PROPS = "DELETE FROM onto_edgeProps WHERE id IN (SELECT id FROM onto_edges WHERE start_id=? OR end_id=?)";

	public Ontology (Properties settings) {
		this.settings = settings;

		//Set up cache
		cacheSize = Integer.parseInt(settings.getProperty("ontology_node_cache", String.valueOf(DEFAULT_CACHE_SIZE)));
		nodeCache = new Hashtable(cacheSize);
		cacheInsertionRecord = new TreeMap();

		//Connect to to ontology
		connect();
	}

	public void connect() {
		//get driver name
		String driverName = settings.getProperty("JDBC_driver");
		String username = settings.getProperty("database_username");
		String password = settings.getProperty("database_password");
		String uri = settings.getProperty("database_uri");

		//connect
		try {
			Class.forName(driverName).newInstance();
			conn = DriverManager.getConnection(uri,username,password);
		} catch (Exception e) {
			System.err.println("Unable to load driver.");
		   	e.printStackTrace();
		   	System.exit(1);
		}
	}

	public void disconnect() {
		try {
			//Close connection;
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Loads the ontology of the specified name
	 * @param name the name of the ontology
	 **/
	public void loadOntology(String name) {
		ontologyName = name;
	}

	/**
	 * Returns the name of the ontology
	 * @returns the name of the ontology
	 **/
	public String getOntologyName() {
		return ontologyName;
	}

	/**
	 * Gets the size of the ontology
	 * @return the size
	 **/
	public long getSize() {
		long start = System.currentTimeMillis();
		accessesGetSize++;

		//Get size
		try {
			PreparedStatement stmt = conn.prepareStatement(GET_NUMBER_NODES);
			stmt.setString(1, ontologyName);
			ResultSet rs = stmt.executeQuery();

			rs.next();

			timeGetSize += System.currentTimeMillis() - start;
			return rs.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public Iterator getNodes() {
		return new OntologyNodeIterator(conn, ontologyName, this);
	}

	/**
	 * Deletes a specific node
	 * @param label the label of the node
	 **/
	public void removeNode(String label) {
		OntologyNode node = getNode(label);
		if (node != null) removeNode(node.getID());
	}

	/**
	 * Deletes a specific node
	 * @param node the node to delete
	 **/
	public void removeNode(OntologyNode node) {
		removeNode(node.getID());
	}

	/**
	 * Delete a specific node
	 * @param id the id of the node to delete
	 **/
	public void removeNode(long id) {
		try {
			conn.setAutoCommit(false);
			PreparedStatement removeNodeProps = conn.prepareStatement(DELETE_NODE_PROPS);
			removeNodeProps.setLong(1, id);
			removeNodeProps.executeUpdate();

			PreparedStatement removeEdgeProps = conn.prepareStatement(DELETE_EDGE_PROPS);
			removeEdgeProps.setLong(1, id);
			removeEdgeProps.setLong(2, id);
			removeEdgeProps.executeUpdate();

			PreparedStatement removeEdges = conn.prepareStatement(DELETE_EDGES);
			removeEdges.setLong(1, id);
			removeEdges.setLong(2, id);
			removeEdges.executeUpdate();

			PreparedStatement removeNode = conn.prepareStatement(DELETE_NODE);
			removeNode.setLong(1, id);
			removeNode.executeUpdate();

			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create an OntologyNode to be elaborated by the
	 * user.
	 * @param label the label for the node
	 * @throws NodeAlreadyExistsException if the node already exists
	 **/
	public OntologyNode createNode(String label) throws NodeAlreadyExistsException {
		long start = System.currentTimeMillis();
		accessesCreateNode++;

		OntologyNode node = null;

		//Check if node already exists
		if (getNode(label) != null) {
			timeCreateNode += System.currentTimeMillis() - start;
			throw new NodeAlreadyExistsException();
		}

		//Create new node
		try {
			PreparedStatement stmt = conn.prepareStatement(INSERT_NODE);
			stmt.setString(1, ontologyName);
			stmt.setString(2, label);
			stmt.executeQuery();

			//Get the new node from the database so that it is inserted in the cache
			node = getNode(label);
		} catch (Exception e) {
			e.printStackTrace();
		}

		timeCreateNode += System.currentTimeMillis() - start;
		return node;
	}

	/**
	 * Removes a property from a given node
	 * @param id the id of the node
	 * @param name the name of the property
	 */
	public void removeProperty(long id, String name) {
		try {
			PreparedStatement stmt = conn.prepareStatement(DELETE_NODE_PROP);
			stmt.setLong(1, id);
			stmt.setString(2, name);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a property to a given node
	 * @param id the id of the node
	 * @param name the name of the property
	 * @param value the value of the propery
	 **/
	public void addProperty(long id, String name, String value) {
		long start = System.currentTimeMillis();
		accessesAddProperty++;

		try {
			PreparedStatement stmt = conn.prepareStatement(INSERT_NODE_PROP);
			stmt.setLong(1, id);
			stmt.setString(2, name);
			stmt.setString(3, value);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		timeAddProperty += System.currentTimeMillis() - start;
	}

	/**
	 * Connect node to another node. If the connection already exists,
	 * increment the weight
	 * @param startNode the first node's id
	 * @param endNode the node id to connect to
	 **/
	public void connectNodes(long startNode, long endNode) {
		try {
			//Check to see if the connection already exists
			//Store the proper end and start nodes
			boolean exists = false;
			int count = 0;
			long actualStart = startNode;
			long actualEnd = endNode;
			long edgeID = 0;

			do {
				PreparedStatement stmt = conn.prepareStatement(GET_UNIQUE_EDGES);
				stmt.setLong(1, actualStart);
				stmt.setLong(2, actualEnd);
				ResultSet rs = stmt.executeQuery();

				//Doesn't exist or wrong order
				if (!rs.next()) {
					//swap start and end
					actualStart = endNode;
					actualEnd = startNode;
					//repeat
				}
				else {
					//exists so set exists to true and get the id
					exists = true;
					edgeID = rs.getLong(1);
				}

				count++;
			} while (!exists && count < 2);

			//If the connection exists, increment the weight
			if (exists) {
				PreparedStatement stmt = conn.prepareStatement(GET_EDGES_PROPS);
				stmt.setLong(1, edgeID);
				ResultSet rs = stmt.executeQuery();

				//Get old weight and increment
				rs.next();
				long weight = rs.getLong(1);
				weight++;

				//update
				stmt = conn.prepareStatement(UPDATE_EDGE_PROP);
				stmt.setLong(1, weight);
				stmt.setLong(2, edgeID);
				stmt.executeUpdate();
			}
			else {
				//Create a new connection
				PreparedStatement stmt = conn.prepareStatement(INSERT_EDGE);
				stmt.setLong(1, startNode);
				stmt.setLong(2, endNode);
				ResultSet rs = stmt.executeQuery();

				//Get the new edge id
				rs.next();
				long id = rs.getLong(1);

				//Setup weight
				stmt = conn.prepareStatement(INSERT_EDGE_PROP);
				stmt.setLong(1, id);
				stmt.setString(2, "weight");
				stmt.setString(3, "1");
				stmt.executeUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates a node with the new node information
	 * @param node the node to update
	 **/
	public void updateNode(OntologyNode node) {
		long start = System.currentTimeMillis();
		accessesUpdateNode++;

		try {
			long id = node.getID();

			//Get Properties for node
			Properties props = new Properties();
			PreparedStatement stmt = conn.prepareStatement(GET_NODE_PROPS);
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString(1);
				String value = rs.getString(2);
				props.setProperty(name, value);
			}

			node.setProperties(props);

			//Get connected nodes
			stmt = conn.prepareStatement(GET_EDGES);
			stmt.setLong(1, id);
			stmt.setLong(2, id);
			rs = stmt.executeQuery();

			PreparedStatement stmt2 = conn.prepareStatement(GET_EDGES_PROPS);

			while (rs.next()) {
				long edgeID = rs.getLong(1);
				long connectedID = rs.getLong(2);

				//Get connected node ID
				if (connectedID == id) connectedID = rs.getLong(3);

				//Get weight
				stmt2.setLong(1, edgeID);
				ResultSet rs2 = stmt2.executeQuery();

				//Currently just supports weight
				rs2.next();
				long weight = rs2.getLong(1);

				node.setConnectedNode(connectedID, weight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		timeUpdateNode += System.currentTimeMillis() - start;
	}

	/**
	 * Get node by label
	 * @param label the label for the node
	 **/
	public OntologyNode getNode(String label) {
		//Get the id from the database of the node
		OntologyNode node = null;

		try {
			PreparedStatement stmt = conn.prepareStatement(GET_NODE_BY_LABEL);
			stmt.setString(1, label);
			stmt.setString(2, ontologyName);
			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) return null;
			long id = rs.getLong(1);

			node = getNode(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return node;
	}

	/**
	 * Returns the node with the specified id
	 * @param id the id of the node
	 **/
	public OntologyNode getNode(long id) {
		long start = System.currentTimeMillis();
		accessesGetNode++;

		//First try and get the node from the cache
		OntologyNode node = loadFromCache(id);

		//Create node if it wasn't found
		if (node == null) {
			try {
				PreparedStatement stmt = conn.prepareStatement(GET_NODE);
				stmt.setLong(1, id);
				ResultSet rs = stmt.executeQuery();

				//if the node doesn't exist
				if (!rs.next()) return null;

				//Create the node
				node = new OntologyNode();
				node.setID(id);
				node.setOntology(this);
				node.setOntologyName(ontologyName);
				node.setLabel(rs.getString(1));

				//Get Properties for node
				Properties props = new Properties();
				stmt = conn.prepareStatement(GET_NODE_PROPS);
				stmt.setLong(1, id);
				rs = stmt.executeQuery();

				while (rs.next()) {
					String name = rs.getString(1);
					String value = rs.getString(2);
					props.setProperty(name, value);
				}

				node.setProperties(props);

				//Get connected nodes
				stmt = conn.prepareStatement(GET_EDGES);
				stmt.setLong(1, id);
				stmt.setLong(2, id);
				rs = stmt.executeQuery();

				PreparedStatement stmt2 = conn.prepareStatement(GET_EDGES_PROPS);

				while (rs.next()) {
					long edgeID = rs.getLong(1);
					long connectedID = rs.getLong(2);

					//Get connected node ID
					if (connectedID == id) connectedID = rs.getLong(3);

					//Get weight
					stmt2.setLong(1, edgeID);
					ResultSet rs2 = stmt2.executeQuery();

					//Currently just supports weight
					rs2.next();
					long weight = rs2.getLong(1);

					node.setConnectedNode(connectedID, weight);
				}

				//Insert new node into cache
				insertIntoCache(node);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		timeGetNode += System.currentTimeMillis() - start;
		return node;
	}

	/**
	 * Check if the node is in the cache. If it is, return it.
	 * Otherwise return null
	 * @param id the id of the node
	 * @return the node or null if it isn't loaded yet
	 **/
	private OntologyNode loadFromCache(long id) {
		Long key = new Long(id);
		Object nodeObj = nodeCache.get(key);

		//Check if it exists
		if (nodeObj == null) {
			cacheMisses++;
			return null;
		}
		else {
			cacheHits++;

			//Before returning it, update cache access time
			OntologyNode node = (OntologyNode)nodeObj;
			long lastAccess = node.getLastAccessedTime();
			long newAccess = System.currentTimeMillis();

			Long lastAccessObj = new Long(lastAccess);
			Long newAccessObj = new Long(newAccess);

			cacheInsertionRecord.remove(lastAccessObj);
			cacheInsertionRecord.put(newAccessObj, key);

			node.setLastAccessedTime(newAccess);
			return node;
		}
	}

	/**
	 * Put node in cache
	 * @param node the ontology node
	 **/
	private void insertIntoCache(OntologyNode node) {
		//Get new access time
		long newAccess = System.currentTimeMillis();
		Long newAccessObj = new Long(newAccess);

		//Set access time in node
		node.setLastAccessedTime(newAccess);

		//Check to see if cache is full
		if (nodeCache.size() == cacheSize) {
			//Remove oldest access
			Long oldId = (Long) cacheInsertionRecord.remove(cacheInsertionRecord.firstKey());
			nodeCache.remove(oldId);
		}

		//Insert new node
		Long newId = new Long(node.getID());
		nodeCache.put(newId, node);
		cacheInsertionRecord.put(newAccessObj, newId);
	}

	/**
	 * Return cache hits so far
	 * @return number of cache hits
	 **/
	public long getCacheHits() {
		return cacheHits;
	}

	/**
	 * Return cache misses so far
	 * @return number of cache misses
	 **/
	public long getCacheMisses() {
		return cacheMisses;
	}

	/**
	 * Prints timing statistics to standard output
	 **/
	public void printStatistics() {
		System.out.println("Method\t\tAccesses\t\tTotal Time\t\tAverage Time");
		System.out.println("======\t\t========\t\t==========\t\t============");
		if (accessesGetSize != 0)
			System.out.println("getSize\t\t"+accessesGetSize+"\t\t"+timeGetSize+"\t\t"+(timeGetSize/accessesGetSize));
		if (accessesAddProperty != 0)
			System.out.println("addProperty\t\t"+accessesAddProperty+"\t\t"+timeAddProperty+"\t\t"+(timeAddProperty/accessesAddProperty));
		if (accessesCreateNode != 0)
			System.out.println("createNode\t\t"+accessesCreateNode+"\t\t"+timeCreateNode+"\t\t"+(timeCreateNode/accessesCreateNode));
		if (accessesGetNode != 0)
			System.out.println("getNode\t\t"+accessesGetNode+"\t\t"+timeGetNode+"\t\t"+(timeGetNode/accessesGetNode));
		if (accessesUpdateNode != 0)
			System.out.println("updateNode\t\t"+accessesUpdateNode+"\t\t"+timeUpdateNode+"\t\t"+(timeUpdateNode/accessesUpdateNode));
	}

	/**
	 * Implemented the equals method from the Object class
	 * @param obj the Ontology to test if its equal to this one
	 * @return true if the ontologies are equal, false if they aren't.
	 **/
	public boolean equals(Object obj) {
		Ontology onto = (Ontology) obj;
		if (onto.getOntologyName().equals(ontologyName)) return true;
		else return false;
	}

//////////////////// Methods used by OntologyNode ///////////////////

//These methods provide a simple centralized way for the nodes to
//interact with the database

/////////////////////////////////////////////////////////////////////

	/**
	 * Returns the connection, but throws an exception if
	 * the connection was close
	 * @return the connection to the database
	 * @throws SQLException if the connection was closed
	 **/
	public Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed()) throw new SQLException();
		return conn;
	}

}//Ontology.java
