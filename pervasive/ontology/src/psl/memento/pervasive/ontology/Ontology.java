package psl.memento.pervasive.ontology;

import java.util.*;

public interface Ontology {
	/**
	 * Loads the ontology of the specified name
	 * @param name the name of the ontology
	 **/
	public void loadOntology(String name); 

	/**
	 * Connect the ontology to the database
	 **/
	public void connect();	

	/**
	 * Disconnect the ontology from the database
	 **/
	public void disconnect();

	/**
	 * Returns the name of the ontology
	 * @returns the name of the ontology
	 **/
	public String getOntologyName();

	/**
	 * Gets the size of the ontology
	 * @return the size
	 **/
	public long getSize();

	/**
	 * Returns an iterator that contains all
	 * the nodes within the ontology. This Iterator
	 * does not actually load all the nodes, but rather
	 * provides Iterator methods to retreive them all.
	 * @returns an iterator of all the OntologyNodes
	 **/
	public Iterator getNodes();

	/**
	 * Deletes a specific node
	 * @param label the label of the node
	 **/
	public void removeNode(String label);
	
	/**
	 * Deletes a specific node
	 * @param node the node to delete
	 **/
	public void removeNode(OntologyNode node);

	/**
	 * Delete a specific node
	 * @param id the id of the node to delete
	 **/
	public void removeNode(long id); 

	/**
	 * Create an OntologyNode to be elaborated by the
	 * user.
	 * @param label the label for the node
	 * @return the new OntologyNode to start manipulating or null if the node already exists.
	 **/
	public OntologyNode createNode(String label); 

	/**
	 * Get node by label
	 * @param label the label for the node
	 **/
	public OntologyNode getNode(String label) ;

	/**
	 * Returns the node with the specified id
	 * @param id the id of the node
	 **/
	public OntologyNode getNode(long id); 

	/**
	 * Implemented the equals method from the Object class
	 * @param obj the Ontology to test if its equal to this one
	 * @return true if the ontologies are equal, false if they aren't.
	 **/
	public boolean equals(Object obj); 

}//Ontology.java
