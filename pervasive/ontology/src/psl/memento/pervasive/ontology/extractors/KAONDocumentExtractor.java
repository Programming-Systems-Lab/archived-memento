package psl.memento.pervasive.ontology.extractors;

import java.util.*;
import psl.memento.pervasive.ontology.*;

public class KAONDocumentExtractor implements OntologyDocumentExtractor {
	private Ontology ontology;
	private Properties settings;
	private KAONTermExtractor termExtractor;
	private KAONRelationsExtractor relationsExtractor;
	private ArrayList filePaths;

	/**
         * Initialize the OntologyDocumentExtractor
         **/
	public void init (Ontology ontology, Properties settings) {
		this.ontology = ontology;
		this.settings = settings;
		filePaths = new ArrayList();
		termExtractor = new KAONTermExtractor(settings, ontology);
		relationsExtractor = new KAONRelationsExtractor(settings, ontology);
		termExtractor.setupTermExtraction();
                relationsExtractor.setupRelationsExtraction();
	}

	/**
	 * Add the specified document to be extracted. Must support
	 * multiple documents.
	 * @param path the path of the document to be added
	 **/
	public void addDocument(String path) {
		filePaths.add(path);
	}

	/**
	 * Extract the supplied documents and add them to the ontology
	 **/
	public void extract() {
		//Add documents to term extractor
		for (int i=0; i<filePaths.size(); i++) {
			termExtractor.addDocument((String)filePaths.get(i));
		}
		try {
			termExtractor.performExtraction();
			termExtractor.addToOntology();
			termExtractor.clearCorpus();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//Add documents and perform extraction for relations extractor
		for (int i=0; i<filePaths.size(); i++) {
			relationsExtractor.addDocument((String)filePaths.get(i));
			relationsExtractor.performExtraction();
			relationsExtractor.addToOntology();
			relationsExtractor.clearCorpus();
		}

	}
}
