package psl.memento.pervasive.ontology;

import java.util.*;

public interface OntologyNode {
	/**
	 * Adds a connection between the current node and the specified node
	 * If the connection already exists, increment the weight.
	 * @param node the node to add the connection to.
	 **/
	public void addConnection(OntologyNode node);

	/**
	 * Adds a connection between the current node and the specified node
         * If the connection already exists, increment the weight.
         * @param label the label of the node to add the connection to.
         **/
	public void addConnection(String label);

	/**
         * Adds a connection between the current node and the specified node
         * If the connection already exists, increment the weight.
         * @param label the id of the node to add the connection to.
         **/
	public void addConnection(long id);

	/**
	 * Adds a property with the given name and value to the node
	 * @param name the name of the property
	 * @param value the value of the property
	 **/
	public void addProperty(String name, String value);

	/**
	 * Remove a property with the given name from the node
	 * @param name the name of the property
	 **/
	public String removeProperty(String name);

	/**
	 * Set the frequency of the node
	 * @param freq the frequency to set the node to
	 **/
	public void setFrequency(long freq);

	/**
	 * Get the ID of the node
	 * @return the ID of the node
	 **/
	public long getID();

	/**
	 * Get the ontology name of the OntologyNode
	 * @return the name of the ontology node's name
	 **/
	public String getOntologyName();

	/**
	 * Get the label of the node
	 * @return the label of the node
	 **/
	public String getLabel();

	/**
	 * Gets the properties of the node
	 * @return the properties of the node as a Properties object
	 **/
	public Properties getProperties();

	/**
	 * Gets the Ontology that the node is part of
	 * @return the ontology
	 **/
	public Ontology getOntology();

	/**
	 * Gets an Iterator of all the connected nodes
	 * to this node
	 * @return an iterator of all the nodes connected to this node
	 **/
	public Iterator getConnectedNodes();

	/**
	 * Gets the weight between this node and the specified connected node
	 * @param connectedNode the node that this node is connected to.
	 * @return the weight between the two nodes
	 **/
	public long getWeight(OntologyNode connectedNode);

	/**
	 * Gets the frequency of the node
	 * @return the frequency of the node
	 **/
	public long getFrequency();

	/**
	 * Returns true if the supplied node is equal to this one
	 * @param obj the OntologyNode to compare this one to.
	 * @return true if they are equal, false if they aren't
	 **/
	public boolean equals(Object obj);
}
