package psl.memento.pervasive.ontology.extractors;

import java.util.*;
import java.io.*;

import de.fzi.wim.kaoncorpus.api.*;
import de.fzi.wim.texttoonto.dictionary.*;

import psl.memento.pervasive.ontology.*;
import psl.memento.pervasive.ontology.relationsextraction.*;
import psl.memento.pervasive.ontology.relationsextraction.algorithm.*;

public class KAONRelationsExtractor {
	//instance variables
	private Properties settings;
	private Ontology ontology;
	private Corpus corpus;
	private AssociationRules associationRelations;
	private DictionaryEntry[] entries;
	private boolean existEntries;

	//Settings variables
	private boolean applyTextPatterns;
	private boolean applyAssociationRules;
	private double minimumSupport;
	private double minimumConfidence;
	private String language;

	public KAONRelationsExtractor(Properties settings, Ontology ontology) {
		this.settings = settings;
		this.ontology = ontology;
		this.existEntries = false;
	}

	public void setupRelationsExtraction() {
		applyTextPatterns = ((settings.getProperty("relations_extraction_apply_text_patterns").equals("true")?true: false));
		applyAssociationRules = ((settings.getProperty("relations_extraction_apply_association_rules").equals("true")?true: false));
		minimumSupport = Double.parseDouble(settings.getProperty("relations_extraction_minimum_support"));
		minimumConfidence = Double.parseDouble(settings.getProperty("relations_extraction_minimum_confidence"));
		language = settings.getProperty("language");

		corpus = new Corpus();
	}

	public void setDictionaryEntries(DictionaryEntry[] entries) {
		this.entries = entries;
		existEntries = true;
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

	public void performExtraction() {
		System.out.println("Started Relations Extraction...");
		System.out.println("\tApply Text Patterns: " + applyTextPatterns);
		System.out.println("\tApply Association Rules: " + applyAssociationRules);
		System.out.println("\tMinimum Support: " + minimumSupport);
		System.out.println("\tMinimum Confidence: " + minimumConfidence);

		associationRelations=new AssociationRules();
		final PatternBasedRulesExtractor patternBasedRelationsExtractor;
		final AssociationRulesExtractor associationRulesExtractor;
		int relationsExtractionPhases=0;
		int hierarchyReusePhases=0;

		if (applyTextPatterns)
			patternBasedRelationsExtractor=new PatternBasedRulesExtractor(language,associationRelations);
		else
			patternBasedRelationsExtractor=null;

		if (applyAssociationRules) {
			relationsExtractionPhases=AssociationRulesExtractor.PHASE_NUMBER;
			associationRulesExtractor=new AssociationRulesExtractor(associationRelations);
			associationRulesExtractor.setMinimumSupport(minimumSupport);
			associationRulesExtractor.setMinimumConfidence(minimumConfidence);
		}
		else
			associationRulesExtractor=null;

		final RelationsExtractionPreprocessor preprocessor=new RelationsExtractionPreprocessor(language);

		try {
			System.out.print("\tFilling Concept Map...");
			preprocessor.fillConceptMap(ontology);
			System.out.println("done.");

			if (associationRulesExtractor!=null || patternBasedRelationsExtractor!=null) {
				System.out.print("\tAdding Corpus Texts...");
				preprocessor.addCorpusTexts(patternBasedRelationsExtractor,associationRulesExtractor,corpus);
				System.out.println("done.");
			}

			if (associationRulesExtractor!=null) {
				System.out.print("\tComputing Association Rules...");
				associationRulesExtractor.computeAssociationRules();
				System.out.println("done.");
			}
		}
		catch (InterruptedException error) {
			error.printStackTrace();
		}
		catch (final Exception error) {
			error.printStackTrace();
		}

		System.out.println("Done.");
	}

	public void addToOntology() {
		int countSucceeded = 0;
		int countFailed = 0;

		System.out.println("Adding to Ontology...");

		AssociationRule[] rules = associationRelations.getAssociationRules();
		System.out.println("\tNumber of Association Rules: " + rules.length);

		List failedRelations=new ArrayList();
		try {
			System.out.println("\tApplying Changes to Ontology...");
			for (int i=0;i<rules.length;i++) {
				try {
					OntologyNode conclusion = (OntologyNode)rules[i].getConclusion();
					OntologyNode premise = (OntologyNode)rules[i].getPremise();
					conclusion.addConnection(premise);
					countSucceeded++;
				}
				catch(Exception e) {
					countFailed++;
				}
			}
		}
		catch (Exception error) {
			error.printStackTrace();
		}

		System.out.println("\tRelations successfully added: " + countSucceeded);
		System.out.println("\tRelations additions failed: " + countFailed);
		System.out.println("Done.");
	}
}
