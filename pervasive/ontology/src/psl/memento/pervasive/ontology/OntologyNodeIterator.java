package psl.memento.pervasive.ontology;

import java.sql.*;
import java.io.*;
import java.util.*;

public class OntologyNodeIterator implements Iterator {
	private String ontologyName;
	private Connection conn;
	private long lastNode;
	private long size;
	private long itrCount;
	private Ontology ontology;

	//SQL Statements
	private final String GET_NEXT_NODE = "SET ROWCOUNT 1\nSELECT id FROM onto_nodes WHERE id > ? AND ontology_name = ?\nSET ROWCOUNT 0";

	public OntologyNodeIterator (Connection conn, String ontologyName, Ontology ontology) {
		this.conn = conn;
		this.ontologyName = ontologyName;
		this.ontology = ontology;
		this.size = ontology.getSize();
		this.itrCount = 0;
		this.lastNode = 0;
	}

	public boolean hasNext() {
		return (itrCount < size);
	}

	public Object next() throws NoSuchElementException {
		if (!hasNext()) throw new NoSuchElementException();

		OntologyNode node = null;

		try {
			PreparedStatement stmt = conn.prepareStatement(GET_NEXT_NODE);
			stmt.setLong(1, lastNode);
			stmt.setString(2, ontologyName);
			ResultSet rs = stmt.executeQuery();

			rs.next();
			long id = rs.getLong(1);
			node = ontology.getNode(id);
			lastNode = id;
		} catch (Exception e) {
			e.printStackTrace();
		}

		itrCount++;
		return node;
	}

	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}
}