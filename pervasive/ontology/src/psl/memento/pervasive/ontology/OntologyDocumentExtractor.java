package psl.memento.pervasive.ontology;

import java.util.*;

public interface OntologyDocumentExtractor {
	/**
	 * Initialize the OntologyDocumentExtractor
	 * @param ontology the Ontology to add the extractions to.
         * @param settings the settings for the extractor
	 **/
	public void init(Ontology ontology, Properties settings);

	/**
	 * Add the specified document to be extracted. Must support
	 * multiple documents.
	 * @param path the path of the document to be added
	 **/
	public void addDocument(String path);

	/**
	 * Extract the supplied documents and add them to the ontology
	 **/
	public void extract();
}
