package psl.memento.pervasive.ontology;

import java.util.*;
import de.fzi.wim.texttoonto.common.*;

public class OntologyInquirer {
    private Ontology ontology;
    private ArrayList allPaths;

    public OntologyInquirer (Ontology ontology) {
	this.ontology = ontology;
    }
    
    /**
     * Returns every path leading from a premise word to a conclusion word
     * @param premise the first word
     * @param conclusion the conclusion word
     * @param depth the depth that the algorithm will search the ontology at.
     * @return A list that contains multiple Lists. Each List within the returned
     * list contains the path of OntologyNode's needed to reach the conclusion
     * from the premise (includes the premise and conclusion in the path). Returns
     * null if there was no path.
     **/
    public List getAllPaths(String premise, String conclusion, int depth) {
	//Set up allPaths
	allPaths = new ArrayList();

	//Get the stems of the conclusion and the premise
	StemmerEN stemmer = new StemmerEN();
	String premiseStem = stemmer.getWordStem(premise);
	String conclusionStem = stemmer.getWordStem(conclusion);
	
	OntologyNode node = ontology.getNode(premiseStem);
	if (node == null) return null;
	else {
	    ArrayList list = new ArrayList();
	    list.add(node);

	    //Keep a hashtable of the used nodes indexed by their label
	    Hashtable usedNodes = new Hashtable();
	    usedNodes.put(node.getLabel(), "");

	    getAllPaths(node, conclusionStem, depth, list, usedNodes);
	}

	//Check to see if allPaths is empty
	if (allPaths.isEmpty())
	    return null;
	else
	    return allPaths;
    }
    
    /**
     * Returns the first path leading from a premise word to a conclusion word
     * @param premise the first word
     * @param conclusion the conclusion word
     * @param depth the maximum depth that the algorithm will search the ontology at.
     * @return a List that contains the path of OntologyNode's needed to reach the conclusion
     * from the premise (includes the premise and conclusion in the path). Returns null if
     * there was no path.
     **/
    public List getFirstPath(String premise, String conclusion, int depth) {
	//Get the stems of the conclusion and the premise
	StemmerEN stemmer = new StemmerEN();
	String premiseStem = stemmer.getWordStem(premise);
	String conclusionStem = stemmer.getWordStem(conclusion);
	
	OntologyNode node = ontology.getNode(premiseStem);
	if (node == null) return null;
	else {
	    ArrayList list = new ArrayList();
	    list.add(node);
	    return getFirstPath(node, conclusionStem, depth, list);
	}
    }

    private void getAllPaths(OntologyNode premise, String conclusion, int depth, ArrayList path, Hashtable usedNodes) {
	//Always Check immediate connections
	OntologyNode node = ontology.getNode(conclusion);
	if (node == null) return;
	
	long weight = premise.getWeight(node);
	
	if (weight > 0) {
	    ArrayList list = (ArrayList) path.clone();
	    list.add(node);
	    allPaths.add(list);
	}
	
	if (depth > 1) {
	    //Recursively call further depths
	    Iterator itr = premise.getConnectedNodes();
	    while (itr.hasNext()) {
		node = (OntologyNode) itr.next();
		
		//First check if the node was already used or if
		//the node is the conclusion node. If so, skip it
		if (usedNodes.get(node.getLabel()) != null || node.getLabel().equals(conclusion)) continue;

		//Get weight to current node
		long tempWeight = premise.getWeight(node);
		
		ArrayList list = (ArrayList) path.clone();
		Hashtable usedNodesCopy = (Hashtable) usedNodes.clone();
		list.add(node);
		usedNodesCopy.put(node.getLabel(), "");
		
		getAllPaths(node, conclusion, (depth-1), list, usedNodesCopy);
	    }
	}
    }
    
    private List getFirstPath(OntologyNode premise, String conclusion, int depth, ArrayList path) {
	
	//Always Check immediate connections
	OntologyNode node = ontology.getNode(conclusion);
	if (node == null) return null;
	
	long weight = premise.getWeight(node);
	
	if (weight > 0) {
	    path.add(node);
	    return path;
	}
	else if (weight == 0 && depth > 1) {
	    //Recursively call further depths
	    Iterator itr = premise.getConnectedNodes();
	    while (itr.hasNext()) {
		node = (OntologyNode) itr.next();
		
		//Get weight to current node
		long tempWeight = premise.getWeight(node);
		
		ArrayList list = (ArrayList) path.clone();
		list.add(node);
		List result = getFirstPath(node, conclusion, (depth-1), list);
		
		if (result != null) {
		    return result;
		}
	    }
	}
	
	return null;
    }
}
