package psl.memento.pervasive.ontology;

import java.sql.*;
import java.io.*;
import java.util.*;

public class OntologyConnectedNodeIterator implements Iterator {
	private Enumeration connectedEnum;
	private OntologyNode node;

	public OntologyConnectedNodeIterator (OntologyNode node, Hashtable connectedNodes) {
		this.node = node;
		this.connectedEnum = connectedNodes.keys();
	}

	public boolean hasNext() {
		return (connectedEnum.hasMoreElements());
	}

	public Object next() throws NoSuchElementException {
		if (!hasNext()) throw new NoSuchElementException();

		long nodeID = ((Long) connectedEnum.nextElement()).longValue();
		OntologyNode connectedNode = node.getOntology().getNode(nodeID);

		return connectedNode;
	}

	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}
}