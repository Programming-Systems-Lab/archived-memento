package psl.memento.pervasive.ontology.relationsextraction;

import java.util.*;
import psl.memento.pervasive.ontology.*;

public class OntologyNodeMap implements Map {
	private Ontology ontology;

	public OntologyNodeMap(Ontology ontology) {
		this.ontology = ontology;
	}

	public Ontology getOntology() {
		return ontology;
	}

///////////// Implemented Methods /////////////

//These methods act like a map but really query
//the ontology for every call.

	public void clear() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(Object key) throws ClassCastException, NullPointerException {
		String label = (String)key;
		OntologyNode node = ontology.getNode(label);

		if (node == null) return false;
		else return true;
	}

	public boolean containsValue(Object value) throws ClassCastException, NullPointerException {
		OntologyNode node = (OntologyNode) value;
		OntologyNode target = ontology.getNode(node.getLabel());

		if (target.equals(node)) return true;
		else return false;
	}

	public Set entrySet() {
		//Not implemented
		return null;
	}

	public boolean equals(Object o) {
		OntologyNodeMap map = (OntologyNodeMap) o;
		if (map.getOntology().equals(ontology)) return true;
		else return false;
	}

	public Object get(Object key) throws ClassCastException, NullPointerException {
		String label = (String)key;
		return ontology.getNode(label);
	}

	public int hashCode() {
		//Not properly implemented but not needed for the current purposes
		return this.hashCode();
	}

	public boolean isEmpty() {
		if (ontology.getSize() == 0) return true;
		else return false;
	}

	public Set keySet() {
		//Not implemented
		return null;
	}

	public Object put(Object key, Object value) throws ClassCastException, NullPointerException, IllegalArgumentException, UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

	public void putAll(Map t) throws ClassCastException, NullPointerException, IllegalArgumentException, UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

	public Object remove(Object key)  throws ClassCastException, NullPointerException, UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

	public int size() {
		return (int) ontology.getSize();
	}

	public Collection values() {
		//Not implemented
		return null;
	}
}