package psl.memento.pervasive.kaon;

/*******************************************************
 * KAONInterfacer.java
 *
 * This class interfaces with the KAON database.
 *******************************************************/

import edu.unika.aifb.kaon.api.oimodel.*;
import edu.unika.aifb.kaon.api.change.*;
import edu.unika.aifb.kaon.api.util.*;
import edu.unika.aifb.kaon.api.vocabulary.*;
import edu.unika.aifb.kaon.api.*;
import edu.unika.aifb.kaon.engineeringserver.change.*;
import edu.unika.aifb.kaon.engineeringserver.client.*;
import edu.unika.aifb.kaon.engineeringserver.dao.*;
import edu.unika.aifb.kaon.engineeringserver.loader.*;
import edu.unika.aifb.kaon.engineeringserver.query.*;
import edu.unika.aifb.kaon.defaultevolution.*;

import de.fzi.wim.kaoncorpus.api.*;

import de.fzi.wim.texttoonto.dictionary.*;
import de.fzi.wim.texttoonto.termextraction.algorithm.*;
import de.fzi.wim.texttoonto.common.*;
import de.fzi.wim.texttoonto.relationsextraction.*;
import de.fzi.wim.texttoonto.relationsextraction.actions.*;
import de.fzi.wim.texttoonto.relationsextraction.algorithm.*;

import java.util.*;
import java.io.*;

public class KAONInterfacer {
	KAONConnection connection;
	Corpus corpus;
	TermExtractor termExtractor;
	MultiDictionary dictionary;
	OIModel oimodel;
	AssociationRules associationRelations;
	Properties settings;

	//For optimization purposes, these will be set up in the beginning
	Map mapOfStems;
	Map mapOfSynonyms;

	//Settings variables
	private String serverUri;
	private String password;
	private String language;
	private int pruneThreshold;
	private int maxNumWords;
	private boolean applyTextPatterns;
	private boolean applyAssociationRules;
	private double minimumSupport;
	private double minimumConfidence;
	private boolean applyHierarchyReuse;
	private String ontologyName;

	//final variables
	private final String PROPERTIES_FILE = "kaon_manager_settings.txt";
	private final boolean PERFORMANCE_ON = false;

	public KAONInterfacer() {
		//Load Properties
		settings = new Properties();
		try {
			settings.load(new FileInputStream(PROPERTIES_FILE));
		} catch (Exception e) {
			System.out.println("Error loading properties file: " + PROPERTIES_FILE + ".");
			System.exit(1);
		}

		String temp = "";
		temp = settings.getProperty("LANGUAGE");
		if (temp == null) {
			System.out.println("Settings: LANGUAGE field empty.");
			System.exit(1);
		}
		else {
			language = temp;
		}

		temp = settings.getProperty("PASSWORD");
		if (temp == null) {
			System.out.println("Settings: PASSWORD field empty.");
			System.exit(1);
		}
		else {
			password = temp;
		}

		temp = settings.getProperty("SERVER_URI");
		if (temp == null) {
			System.out.println("Settings: SERVER_URI field empty.");
			System.exit(1);
		}
		else {
			serverUri = temp;
		}

		temp = settings.getProperty("ONTOLOGY_NAME");
		if (temp == null) {
			System.out.println("Settings: ONTOLOGY_NAME field empty.");
			System.exit(1);
		}
		else {
			ontologyName = temp;
		}

		temp = settings.getProperty("PRUNE_THRESHOLD");
		if (temp == null) {
			System.out.println("Settings: PRUNE_THRESHOLD field empty.");
			System.exit(1);
		}
		else {
			try {
				pruneThreshold = Integer.parseInt(temp);
			} catch (Exception e) {
				System.out.println("PRUNE_THESHOLD must be an integer");
				System.exit(1);
			}
		}

		temp = settings.getProperty("MAX_NUM_WORDS");
		if (temp == null) {
			System.out.println("Settings: MAX_NUM_WORDS field empty.");
			System.exit(1);
		}
		else {
			try {
				maxNumWords = Integer.parseInt(temp);
			} catch (Exception e) {
				System.out.println("MAX_NUM_WORDS must be an integer");
				System.exit(1);
			}
		}

		temp = settings.getProperty("APPLY_TEXT_PATTERNS");
		if (temp == null) {
			System.out.println("Settings: APPLY_TEXT_PATTERNS field empty.");
			System.exit(1);
		}
		else {
			if (temp.equals("true")) applyTextPatterns = true;
			else applyTextPatterns = false;
		}

		temp = settings.getProperty("APPLY_ASSOCIATION_RULES");
		if (temp == null) {
			System.out.println("Settings: APPLY_ASSOCIATION_RULES field empty.");
			System.exit(1);
		}
		else {
			if (temp.equals("true")) applyAssociationRules = true;
			else applyAssociationRules = false;
		}

		temp = settings.getProperty("MINIMUM_SUPPORT");
		if (temp == null) {
			System.out.println("Settings: MINIMUM_SUPPORT field empty.");
			System.exit(1);
		}
		else {
			try {
				minimumSupport = Double.parseDouble(temp);
			} catch (Exception e) {
				System.out.println("MINUMUM_SUPPORT must be a double");
				System.exit(1);
			}
		}

		temp = settings.getProperty("MINIMUM_CONFIDENCE");
		if (temp == null) {
			System.out.println("Settings: MINIMUM_CONFIDENCE field empty.");
			System.exit(1);
		}
		else {
			try {
				minimumConfidence = Double.parseDouble(temp);
			} catch (Exception e) {
				System.out.println("MINIMUM_CONFIDENCE must be a double");
				System.exit(1);
			}
		}

		temp = settings.getProperty("APPLY_HIERARCHY_REUSE");
		if (temp == null) {
			System.out.println("Settings: APPLY_HIERARCHY_REUSE field empty.");
			System.exit(1);
		}
		else {
			if (temp.equals("true")) applyHierarchyReuse = true;
			else applyHierarchyReuse = false;
		}

		//Initialize Instance Variables

		connection = null;
		corpus = new Corpus();
		termExtractor = new TermExtractor(language);
		dictionary = new MultiDictionary();
		oimodel = null;
		associationRelations = null;
		mapOfStems = new HashMap();
		mapOfSynonyms = new HashMap();
	}

	public KAONInterfacer(KAONConnection cnt) {
		connection = cnt;

		try {
			oimodel = connection.openOIModelLogical(ontologyName);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public KAONConnection getConnection() {
		return connection;
	}

	public void connect() {
		System.out.println("Connecting to SQL database...");
		System.out.println("\tServer URI: " + serverUri);
		Map parameters = new HashMap();
		parameters.put(DirectKAONConnection.SERVER_URI, serverUri);
		parameters.put(DirectKAONConnection.PASSWORD, password);

		try {
			DirectKAONConnection directconnect = new DirectKAONConnection(parameters);
			connection = directconnect.getConnection();
			oimodel = connection.openOIModelLogical(ontologyName);
		} catch(Exception e) {
			System.out.println("\t" + e.getMessage());
			System.exit(1);
		}

		//For optimization purposes, the mapOfStems and mapOfSynonyms will
		//be preloaded
		String languageURI=KAONVocabularyAdaptor.INSTANCE.getLanguageURI(language);

		try {
			System.out.print("\tLoading Map of Stems...");
			loadStems(mapOfStems,languageURI,oimodel);
			System.out.println("done.");

			System.out.print("\tLoading Map of Synonyms...");
			loadSynonyms(mapOfSynonyms,languageURI,oimodel);
			System.out.println("done.");
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		System.out.println("Done.");
	}

	public void close() {
		System.out.print("Closing SQL database...");
		try {
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("Done.");
	}

	/************************************************************************
	 * Term Extraction code
	 ************************************************************************/

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

	public void setupTermExtraction() {
		System.out.print("Setting up term extractor...");
		termExtractor.setPruneThreshold(pruneThreshold);
		termExtractor.setMaxNumberOfWords(maxNumWords);
		System.out.println("Done.");
	}

	public void performExtraction() throws IOException, InterruptedException {
		System.out.println("Starting Term Extraction...");
		System.out.println("\tPrune Threshold: " + termExtractor.getPruneThreshold());
		System.out.println("\tMaximum Number of Words: " + termExtractor.getMaxNumberOfWords());
		System.out.println("\tLinguistic Filter: " + termExtractor.getLinguisticFilter());

		//Add Algorithm Listener
		AlgorithmListener listener = new AlgorithmListener();
		termExtractor.addAlgorithmProgressListener(listener);
		termExtractor.doTermExtraction(corpus,dictionary);

		boolean continueAlg = false;

		//Wait until algorithm is done
		System.out.print("\tWaiting for alogorithm to be done...");
		while (!continueAlg) {
			synchronized(listener.lock) {
				if (listener.working) continueAlg = false;
				else continueAlg = true;
			}

			System.out.print(".");
		}
		System.out.println("Done.");

		//Statistical Code
		System.out.println("\tNumber of entries: " + termExtractor.getDictionaryEntries(dictionary).length);
	}

	public void addToOIModel() {
		//Timers to measure performance:
		long time_createStem = 0;
		long time_getConcept = 0;
		long time_addLexicalReference = 0;
		long time_getLexicalEntry = 0;
		long time_createLexicalEntry = 0;
		long time_getUnstemmedTerms = 0;
		long time_applyChanges = 0;
		long time_temp = 0;

		List list=new LinkedList();
		try {
			System.out.println("Adding to OIModel...");

			try {
				String languageURI=KAONVocabularyAdaptor.INSTANCE.getLanguageURI(language);
				DictionaryEntry[] entries = termExtractor.getDictionaryEntries(dictionary);
				System.out.println("\tNumber of entries to add: " + entries.length);

				for (int i=0;i<entries.length;i++) {
					DictionaryEntry entry = entries[i];
					String stemValue=entry.getTermStem();

					LexicalEntry stem=getStem(mapOfStems,stemValue);
					if (stem==null) {
						time_temp = System.currentTimeMillis();
						stem=createStem(mapOfStems,oimodel,languageURI,stemValue,list);
						time_createStem += System.currentTimeMillis() - time_temp;

						time_temp = System.currentTimeMillis();
						Concept concept=oimodel.getConcept(oimodel.createNewURI());
						time_getConcept += System.currentTimeMillis() - time_temp;

						list.add(new AddEntity(oimodel,null,concept));
						list.add(new AddSubConcept(oimodel,null,oimodel.getRootConcept(),concept));

						time_temp = System.currentTimeMillis();
						LexiconUtil.addLexicalReference(stem,concept,list);
						time_addLexicalReference += System.currentTimeMillis() - time_temp;

						time_temp = System.currentTimeMillis();
						LexicalEntry label=oimodel.getLexicalEntry(oimodel.createNewURI());
						time_getLexicalEntry += System.currentTimeMillis() - time_temp;

						time_temp = System.currentTimeMillis();
						LexiconUtil.createLexicalEntry(label,KAONVocabularyAdaptor.INSTANCE.getKAONLabel(),stemValue,languageURI,concept,list);
						time_createLexicalEntry += System.currentTimeMillis() - time_temp;

						time_temp = System.currentTimeMillis();
						Iterator iterator=entry.getUnstemmedTerms().iterator();
						while (iterator.hasNext()) {
							String synonymValue=(String)iterator.next();
							LexicalEntry synonym=createSynonym(mapOfSynonyms,oimodel,languageURI,synonymValue,list);
							LexiconUtil.addLexicalReference(synonym,concept,list);
						}
						time_getUnstemmedTerms += System.currentTimeMillis() - time_temp;
					}
				}
				//mapOfStems.clear();
				//mapOfSynonyms.clear();

				//Perform changes to OIModel
				time_temp = System.currentTimeMillis();
				oimodel.applyChanges(list);
				time_applyChanges += System.currentTimeMillis() - time_temp;
			}
			finally {
				System.out.println("Done.");
			}
		}
		catch (KAONException error) {
			error.printStackTrace();
        }

        //If performance measurement is on
       	if (PERFORMANCE_ON) {
			System.out.println("PERFORMANCE MEASUREMENT ON");
			System.out.println("\ttime_createStem = " + time_createStem);
			System.out.println("\ttime_addLexicalReference = " + time_addLexicalReference);
			System.out.println("\ttime_getLexicalEntry = " + time_getLexicalEntry);
			System.out.println("\ttime_createLexicalEntry = " + time_createLexicalEntry);
			System.out.println("\ttime_getUnstemmedTerms = " + time_getUnstemmedTerms);
			System.out.println("\ttime_applyChanges = " + time_applyChanges);
		}
	}

	protected void loadStems(Map mapOfStems,String languageURI,OIModel oimodel) throws KAONException {
		Concept stemConcept=oimodel.getConcept(KAONVocabularyAdaptor.INSTANCE.getStem());
		Set stems=stemConcept.getInstances();
		oimodel.loadObjects(stems,OIModel.LOAD_INSTANCE_FROM_PROPERTY_VALUES);
		Iterator iterator=stems.iterator();
		while (iterator.hasNext()) {
			LexicalEntry stem=(LexicalEntry)iterator.next();
			if (languageURI.equals(stem.getInLanguage())) {
				String stemValue=stem.getValue();
				mapOfStems.put(stemValue,stem);
			}
		}
	}
	protected void loadSynonyms(Map mapOfSynonyms,String languageURI,OIModel oimodel) throws KAONException {
		Concept synonymConcept=oimodel.getConcept(KAONVocabularyAdaptor.INSTANCE.getSynonym());
		Set syonyms=synonymConcept.getInstances();
		oimodel.loadObjects(syonyms,OIModel.LOAD_INSTANCE_FROM_PROPERTY_VALUES);
		Iterator iterator=syonyms.iterator();
		while (iterator.hasNext()) {
			LexicalEntry synonym=(LexicalEntry)iterator.next();
			if (languageURI.equals(synonym.getInLanguage())) {
				String synonymValue=synonym.getValue();
				mapOfSynonyms.put(synonymValue,synonym);
			}
		}
	}

	protected void checkForStems(String languageURI,OIModel oimodel) throws KAONException {

	}

	protected void checkForSynonyms(String languageURI,OIModel oimodel) throws KAONException {

	}

	protected LexicalEntry getStem(Map mapOfStems,String stemValue) throws KAONException {
		return (LexicalEntry)mapOfStems.get(stemValue);
	}
	protected LexicalEntry createStem(Map mapOfStems,OIModel oimodel,String languageURI,String stemValue,List list) throws KAONException {
		LexicalEntry stem=(LexicalEntry)mapOfStems.get(stemValue);
		if (stem==null) {
			stem=oimodel.getLexicalEntry(oimodel.createNewURI());
			LexiconUtil.addLexicalEntry(stem,KAONVocabularyAdaptor.INSTANCE.getStem(),list);
			Property value=oimodel.getProperty(KAONVocabularyAdaptor.INSTANCE.getValue());
			list.add(new AddPropertyInstance(oimodel,null,value,stem,stemValue));
			Property inLanguage=oimodel.getProperty(KAONVocabularyAdaptor.INSTANCE.getInLanguage());
			list.add(new AddPropertyInstance(oimodel,null,inLanguage,stem,oimodel.getInstance(languageURI)));
			mapOfStems.put(stemValue,stem);
		}
		return stem;
	}
	protected LexicalEntry createSynonym(Map mapOfSynonyms,OIModel oimodel,String languageURI,String synonymValue,List list) throws KAONException {
		LexicalEntry synonym=(LexicalEntry)mapOfSynonyms.get(synonymValue);
		if (synonym==null) {
			synonym=oimodel.getLexicalEntry(oimodel.createNewURI());
			LexiconUtil.addLexicalEntry(synonym,KAONVocabularyAdaptor.INSTANCE.getSynonym(),list);
			Property value=oimodel.getProperty(KAONVocabularyAdaptor.INSTANCE.getValue());
			list.add(new AddPropertyInstance(oimodel,null,value,synonym,synonymValue));
			Property inLanguage=oimodel.getProperty(KAONVocabularyAdaptor.INSTANCE.getInLanguage());
			list.add(new AddPropertyInstance(oimodel,null,inLanguage,synonym,oimodel.getInstance(languageURI)));
			mapOfSynonyms.put(synonymValue,synonym);
		}
		return synonym;
    }

	/***********************************************************************
	 * Relations Extractor Code
	 ***********************************************************************/

	public void startRelationsExtraction() {
		System.out.println("Started Relations Extraction...");
		System.out.println("\tApply Text Patterns: " + applyTextPatterns);
		System.out.println("\tApply Association Rules: " + applyAssociationRules);
		System.out.println("\tApply Hierarchy Reuse: " + applyHierarchyReuse);
		System.out.println("\tMinimum Support: " + minimumSupport);
		System.out.println("\tMinimum Confidence: " + minimumConfidence);

		associationRelations=new AssociationRules();
		final PatternBasedRulesExtractor patternBasedRelationsExtractor;
		final AssociationRulesExtractor associationRulesExtractor;
		final HierarchyReuseExtractor hierarchyReuseExtractor;
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

		if (applyHierarchyReuse) {
			hierarchyReusePhases=HierarchyReuseExtractor.PHASE_NUMBER;
			hierarchyReuseExtractor=new HierarchyReuseExtractor(language,associationRelations);
		}
		else
			hierarchyReuseExtractor=null;

		final RelationsExtractionPreprocessor preprocessor=new RelationsExtractionPreprocessor(language);

		try {
			System.out.print("\tFilling Concept Map...");
			preprocessor.fillConceptMap(oimodel);
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

			if (hierarchyReuseExtractor!=null) {
				System.out.print("\tReusing Hierarchy...");
				hierarchyReuseExtractor.initialize(preprocessor.getConceptMap());
				hierarchyReuseExtractor.reuseHierarchy(oimodel);
				System.out.println("done.");
			}
		}
		catch (InterruptedException error) {
			System.out.println(error);
		}
		catch (final Exception error) {
			System.out.println(error);
		}

		System.out.println("Done.");
    }

    public void addToOIModelAsHierarchy() {
		int countSucceeded = 0;
		int countFailed = 0;

		System.out.println("Adding to OI Model as Hierarchy...");

		AssociationRule[] rules = associationRelations.getAssociationRules();
		System.out.println("\tNumber of Association Rules: " + rules.length);

		List failedRelations=new ArrayList();
		try {
			oimodel.suspendEvents();
			String languageURI=KAONVocabularyAdaptor.INSTANCE.getLanguageURI(language);
			System.out.println("\tApplying Changes to OIModel...");
			for (int i=0;i<rules.length;i++) {
				try {
					Concept conclusion = (Concept)rules[i].getConclusion();
					Concept premise = (Concept)rules[i].getPremise();
					oimodel.applyChanges(Collections.singletonList(new AddSubConcept(oimodel,null,conclusion,premise)));
					countSucceeded++;
				}
				catch(Exception e) {
					countFailed++;
				}
			}
		}
		catch (Exception error) {
			System.out.println(error);
        }
		finally {
			oimodel.resumeEvents();
		}

		System.out.println("\tRelations successfully added: " + countSucceeded);
		System.out.println("\tRelations additions failed: " + countFailed);
        System.out.println("Done.");
	}

    /*********************************************************************
     * Main Method
     *********************************************************************/

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java KAONInterfacer [name of directory or file]");
			return;
		}

		KAONInterfacer kaon = new KAONInterfacer();
		kaon.connect();

		//Check to see if the file is a directory
		File input = new File(args[0]);
		if (input.isDirectory()) {
			String[] files = input.list();
			for (int i=0; i < files.length; i++) {
				kaon.addDocument(args[0] + File.separator + files[i]);
				kaon.setupTermExtraction();

				try {
					kaon.performExtraction();
					kaon.addToOIModel();
					kaon.startRelationsExtraction();
					kaon.addToOIModelAsHierarchy();
				} catch (Exception e) {
					System.out.println(e);
				}
			}//for
		}
		else {
			kaon.addDocument(args[0]);
			kaon.setupTermExtraction();

			try {
				kaon.performExtraction();
				kaon.addToOIModel();
				kaon.startRelationsExtraction();
				kaon.addToOIModelAsHierarchy();
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		kaon.close();
	}

	/***********************************************************************
	 * Listener Classes
	 ***********************************************************************/

	 class AlgorithmListener implements AlgorithmProgressListener {
		 //instance variables
		 public boolean working;
		 public Object lock;

		 public AlgorithmListener() {
			lock = new Object();

			synchronized(lock) {
				working = false;
			}
		 }

		/**
		 * Called when the algorithm is started.
		 *
		 * @param algorithm                         the algorithm
		 * @param numberOfPhases                    the number of phases of the algorithm
		 */
		public void algorithmStarted(AbstractAlgorithm algorithm,int numberOfPhases) {
			synchronized(lock) {
				working = true;
			}
		}

		/**
		 * Called to report the progress of the algorithm.
		 *
		 * @param algorithm                         the algorithm
		 * @param currentPhase                      the index of the current phase
		 * @param numberOfPhases                    the number of phases of the algorithm
		 * @param currentStep                       the index of the current step
		 * @param numberOfSteps                     the number of steps in the current phase
		 */
		public void progressReport(AbstractAlgorithm algorithm,int currentPhase,int numberOfPhases,long currentStep,long numberOfSteps) {

		}

		/**
		 * Called when the algorithm is finished.
		 *
		 * @param algorithm                         the algorithm
		 */
    	public void algorithmFinished(AbstractAlgorithm algorithm) {
			synchronized(lock) {
				working = false;
			}
		}
	 }//AlgorithmListener
}//KAONInterfacer