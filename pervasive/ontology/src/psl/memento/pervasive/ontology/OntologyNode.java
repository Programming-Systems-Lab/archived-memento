package psl.memento.pervasive.ontology;

import java.sql.*;
import java.io.*;
import java.util.*;

public class OntologyNode {
	//Instance variables
	private String ontologyName;
	private long id;
	private Ontology ontology;
	private Properties props;
	private long lastAccessed;
	private String label;
	private boolean dirty;

	//Connected Nodes
	private Hashtable connectedNodes;

	/**
	 * Initializes an Ontology Node for easier traversal
	 * of the ontology graph.
	 **/
	public OntologyNode () {
		props = new Properties();
		connectedNodes = new Hashtable();
		dirty = false;
	}

///////////////// Sets up the node ///////////////////
// These methods should only be called by the Ontology
// object when creating a wrapper around pre-existing
// node data
//////////////////////////////////////////////////////

	public void setID(long id) {
		this.id = id;
	}

	public void setOntologyName(String name) {
		ontologyName = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setProperties(Properties props) {
		this.props = props;
	}

	public void setConnectedNode(long id, long weight) {
		connectedNodes.put(new Long(id), new Long(weight));
	}

	public void setLastAccessedTime(long millis) {
		lastAccessed = millis;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

///////////////// To enhance node later ////////////

	public void addConnection(OntologyNode node) {
		addConnection(node.getID());
	}

	public void addConnection(String label) {
		OntologyNode node = ontology.getNode(label);
		if (node != null) addConnection(node.getID());
	}

	public void addConnection(long id) {
		ontology.connectNodes(this.id, id);

		//Set the dirty bit of the current node and the
		//one that was connected to
		OntologyNode node = ontology.getNode(id);
		node.setDirty();
		dirty = true;
	}

	public void addProperty(String name, String value) {
		ontology.addProperty(id, name, value);
		dirty = true;
	}

	public String removeProperty(String name) {
		String value = props.getProperty(name);
		ontology.removeProperty(id, name);
		dirty = true;
		return value;
	}

////////////////////////////////////////////////////

	public long getID() {
			return id;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public String getLabel() {
		return label;
	}

	public Properties getProperties() {
		checkDirty();
		return props;
	}

	public long getLastAccessedTime() {
		return lastAccessed;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public Iterator getConnectedNodes() {
		checkDirty();
		return new OntologyConnectedNodeIterator(this, connectedNodes);
	}

	public long getWeight(OntologyNode connectedNode) {
		checkDirty();
		Long id = new Long(connectedNode.getID());
		Object obj = connectedNodes.get(id);

		if (obj == null) return 0;
		else return ((Long) obj).longValue();
	}

	public void setDirty() {
		dirty = true;
	}

	public void checkDirty() {
		if (dirty) {
			ontology.updateNode(this);
			dirty = false;
		}
	}

	public boolean equals(Object obj) {
		OntologyNode node = (OntologyNode) obj;

		if (node.getID() == id) return true;
		else return false;
	}
}