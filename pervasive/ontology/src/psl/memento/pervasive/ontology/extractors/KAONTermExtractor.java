package psl.memento.pervasive.ontology.extractors;

import java.util.*;
import java.io.*;

import de.fzi.wim.kaoncorpus.api.*;
import de.fzi.wim.texttoonto.dictionary.*;
import de.fzi.wim.texttoonto.termextraction.algorithm.*;
import de.fzi.wim.texttoonto.common.*;

import psl.memento.pervasive.ontology.*;

public class KAONTermExtractor {
	private Properties settings;
	private Ontology ontology;
	private TermExtractor termExtractor;
	private Corpus corpus;
	private MultiDictionary dictionary;

	private final String NUM_SYNONYMS = "num_synonyms";

	/**
	 * Initialize the term extractor
	 * @param settings the settings for the extractor
	 * @param ontology the ontology that the extractor should add to
	 **/
	public KAONTermExtractor (Properties settings, Ontology ontology) {
		this.settings = settings;
		this.ontology = ontology;
	}

	/**
	 * Set up the term extractor
	 **/
	public void setupTermExtraction() {
		termExtractor = new TermExtractor(settings.getProperty("language"));
		termExtractor.setPruneThreshold(Integer.parseInt(settings.getProperty("term_extractor_prune_threshold")));
		termExtractor.setMaxNumberOfWords(Integer.parseInt(settings.getProperty("term_extractor_max_num_words")));

		corpus = new Corpus();
		dictionary = new MultiDictionary();
	}

	/**
	 * Adds a document to the collection to extract terms from
	 * @param doc the document path
	 **/
	public void addDocument(String doc) {
		System.out.print("Adding Document: " + doc + " to corpus...");
		File file = new File(doc);
		String fileName=file.toString();
		for (int type=0;type<Content.s_suffixes.length;type++) {
			String suffix="."+Content.s_suffixes[type];
			if (fileName.endsWith(suffix)) {
				String documentName=fileName.substring(0,fileName.length()-suffix.length());
				Document document=corpus.getDocument(documentName);
				if (document==null)
					document=corpus.createDocument(documentName);
				if (document.getContent(suffix)==null)
					document.createContent(suffix,file);
				break;
			}
		}
		System.out.println("Done.");
	}

	/**
	 * Clears the corpus
	 **/
	public void clearCorpus() {
		corpus.clear();
	}

	/**
	 * Returns the corpus
	 * @returns the corpus
	 **/
	public Corpus getCorpus() {
		return corpus;
	}

	/**
	 * Sets the corpus
	 * @param corpus
	 **/
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	/**
	 * Perform term extraction
	 **/
	public void performExtraction() throws IOException, InterruptedException {
		System.out.println("Starting Term Extraction...");
		System.out.println("\tPrune Threshold: " + termExtractor.getPruneThreshold());
		System.out.println("\tMaximum Number of Words: " + termExtractor.getMaxNumberOfWords());
		System.out.println("\tLinguistic Filter: " + termExtractor.getLinguisticFilter());

		termExtractor.doTermExtraction(corpus,dictionary);

		System.out.println("\tNumber of entries: " + termExtractor.getDictionaryEntries(dictionary).length);
	}

	public DictionaryEntry[] getDictionaryEntries() {
		return termExtractor.getDictionaryEntries(dictionary);
	}

	public void addToOntology() {
		List list=new LinkedList();
		try {
			System.out.println("Adding to Ontology...");

			try {
				DictionaryEntry[] entries = termExtractor.getDictionaryEntries(dictionary);
				System.out.println("\tNumber of entries to add: " + entries.length);

				for (int i=0;i<entries.length;i++) {
					DictionaryEntry entry = entries[i];
					String stemValue=entry.getTermStem();
					Iterator iterator=entry.getUnstemmedTerms().iterator();

					//OntologyNode to add
					OntologyNode node = null;
					int numSynonyms = 0;
					Hashtable synonyms = null;

					try {
						//Use stem as node
						node = ontology.createNode(stemValue);
						numSynonyms = 0;
						synonyms = new Hashtable();
					} catch (NodeAlreadyExistsException e) {
						//Node already exists so update the old node
						node = ontology.getNode(stemValue);
						synonyms = getSynonyms(node);
						numSynonyms = Integer.parseInt(node.removeProperty(NUM_SYNONYMS));
					}

					//Add synonyms unless they already exist
					while (iterator.hasNext()) {
						String synonymValue=(String)iterator.next();

						if (!doesSynonymExist(synonyms, synonymValue)){
							numSynonyms++;
							String newName = "synonym["+numSynonyms+"]";
							node.addProperty(newName, synonymValue);
						}
					}

					//Set synonym number value
					node.addProperty(NUM_SYNONYMS, String.valueOf(numSynonyms));
				}
			}
			finally {
				System.out.println("Done.");
			}
		}
		catch (Exception error) {
			error.printStackTrace();
        }
	}

	private Hashtable getSynonyms(OntologyNode node) {
		Properties props = node.getProperties();
		String value = props.getProperty(NUM_SYNONYMS);
		Hashtable synonyms = new Hashtable();

		int number = 0;
		if (value != null) number = Integer.parseInt(value);

		for (int i=1; i<= number; i++) {
			String syn = props.getProperty("synonym["+i+"]");
			synonyms.put(syn, "");
		}

		return synonyms;
	}

	private boolean doesSynonymExist(Hashtable synonyms, String synonym) {
		Object value = synonyms.get(synonym);
		if (value == null) return false;
		else return true;
	}

}
