package psl.memento.pervasive.kaon;

/*******************************************************
 * KAONInquirer.java
 *
 * This class makes inquiries of the KAON Database such
 * as asking for word relationships-this class performs
 * only read-only actions on the database
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

import de.fzi.wim.lexiconindex.*;

import java.util.*;
import java.io.*;

public class KAONInquirer {
	KAONConnection connection;
	OIModel oimodel;
	Properties settings;

	//Settings variables
	private String serverUri;
	private String password;
	private String language;
	private String languageURI;
	private String ontologyName;

	//final variables
	private final String PROPERTIES_FILE = "kaon_manager_settings.txt";

	public KAONInquirer() {
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

		languageURI = KAONVocabularyAdaptor.INSTANCE.getLanguageURI(language);
	}

	public KAONInquirer(KAONConnection cnt) {
		this();
		connection = cnt;
	}

	public void connect() {
		System.out.print("Connecting to SQL database...");
		Map parameters = new HashMap();
		parameters.put(DirectKAONConnection.SERVER_URI, serverUri);
		parameters.put(DirectKAONConnection.PASSWORD, password);

		try {
			DirectKAONConnection directconnect = new DirectKAONConnection(parameters);
			connection = directconnect.getConnection();
			oimodel = connection.openOIModelLogical("ontology");
		} catch(Exception e) {
			System.out.println(e);
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

	public void refresh() {
		try {
			connection.openOIModelLogical(ontologyName).refresh();
		} catch (Exception e) {

		}
	}

	/*************************************************************
	 * Methods for traversing Ontology
	 *************************************************************/

	public List getConcepts(String value) throws KAONException{
		LexiconIndex lexiconIndex = LexiconIndexFactory.createLexiconIndex(oimodel);

		List elements=new ArrayList();
		synchronized (lexiconIndex) {
			Collection answer = lexiconIndex.searchLexicon(languageURI,value);
			oimodel.loadObjects(answer,OIModel.LOAD_CONCEPT_BASICS | OIModel.LOAD_PROPERTY_BASICS | OIModel.LOAD_INSTANCE_BASICS | OIModel.LOAD_LEXICON);
			Iterator iterator=answer.iterator();
			while (iterator.hasNext()) {
				Instance instance=(Instance)iterator.next();
				Concept concept=instance.getSpanningConcept();
				if (concept.isInOIModel())
					elements.add(concept);
			}
		}
		return elements;
	}

	/**
	 * This method takes two lists of concepts, determines the relationships between
	 * them, and then returns those concepts in the second list that link to concepts
	 * in the first list.
	 *
	 * Currently, this algorithm only works for a depth of 0 - must be a direct
	 * connection between the nodes.
	 *
	 * @param firstConcepts the first list of concepts
	 * @param secondConcepts the second list of concepts
	 * @return a Hashtable in which the keys are terms in the list of secondConcepts
	 * and the value of those keys is the number of links to that concept, usable
	 * for ranking the best connection.
	 */
	public Hashtable determineRelations(List firstConcepts, List secondConcepts) throws KAONException {
		Hashtable links = new Hashtable();

		for (int i=0; i < firstConcepts.size(); i++) {
			Concept first = (Concept)firstConcepts.get(i);

			for (int j=0; j < secondConcepts.size(); j++) {
				Concept second = (Concept)secondConcepts.get(j);
				boolean sub = first.isDirectSubConceptOf(second);
				boolean sub2 = second.isDirectSubConceptOf(first);

				if (sub || sub2) {
					//Add to the hashtable, or increment value if it is already there
					boolean present = links.contains(second.getLabel(languageURI));

					if (present) {
						int value = ((Integer)links.get(second)).intValue();
						links.remove(second);

						value++;

						Integer newValue = new Integer(value);
						links.put(second, newValue);
					}
					else {
						int value = 1;
						Integer newValue = new Integer(value);
						links.put(second, newValue);
					}
				}
			}
		}

		return links;
	}

	/*************************************************************
	 * Main method
	 *************************************************************/

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Not enough arguments.\nUsage: java KAONInquirer [first term to compare] [second term to compare]");
			return;
		}

		KAONInquirer inq = new KAONInquirer();
		inq.connect();

		try {
			List firstConcepts = inq.getConcepts(args[0]);
			System.out.println("Concepts found for argument 1:");
			System.out.println("\tSize of list: " + firstConcepts.size());
			for (int i=0; i < firstConcepts.size(); i++) {
				System.out.println("\t" + ((Concept)firstConcepts.get(i)).getLabel(KAONVocabularyAdaptor.INSTANCE.getLanguageURI("en")));
			}

			List secondConcepts = inq.getConcepts(args[1]);
			System.out.println("Concepts found for argument 2:");
			System.out.println("\tSize of list: " + secondConcepts.size());
			for (int i=0; i < secondConcepts.size(); i++) {
				System.out.println("\t" + ((Concept)secondConcepts.get(i)).getLabel(KAONVocabularyAdaptor.INSTANCE.getLanguageURI("en")));
			}

			Hashtable links = inq.determineRelations(firstConcepts, secondConcepts);
			System.out.println("Concepts and values found in relationships between argument 1 and argument 2:");
			System.out.println("\tSize of list: " + links.size());

			Enumeration keys = links.keys();
			Enumeration values = links.elements();

			while(keys.hasMoreElements()) {
				System.out.println("\t" + ((Concept)keys.nextElement()).getLabel(KAONVocabularyAdaptor.INSTANCE.getLanguageURI("en")) + " : " + values.nextElement());
			}

		} catch (Exception e) {
			System.out.println(e);
		}


		inq.close();
	}
}