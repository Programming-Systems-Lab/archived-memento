package psl.memento.pervasive.ontology;

import java.util.*;
import de.fzi.wim.texttoonto.common.*;

public class OntologyInquirer {
	private Ontology ontology;

	public OntologyInquirer (Ontology ontology) {
		this.ontology = ontology;
	}

	public List areRelated(String premise, String conclusion, int depth) {
		//Get the stems of the conclusion and the premise
		StemmerEN stemmer = new StemmerEN();
		String premiseStem = stemmer.getWordStem(premise);
		String conclusionStem = stemmer.getWordStem(conclusion);

		OntologyNode node = ontology.getNode(premiseStem);
		if (node == null) return null;
		else {
			ArrayList list = new ArrayList();
			list.add(node);
			return areRelated(node, conclusionStem, depth, list);
		}
	}

	private List areRelated(OntologyNode premise, String conclusion, int depth, ArrayList path) {

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
				List result = areRelated(node, conclusion, (depth-1), list);

				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}
}