package psl.memento.pervasive.kaon;

/**************************************************************
 * KAONManager.java
 *
 * This class completely manages client usage of the
 * KAONInterfacer and KAONInquirer. There is only one instance
 * of the KAONInterfacer since it modifies the ontology. Thus,
 * a queue is set up of incoming documents to be added, which
 * is constantly being worked on.
 *
 * This class sets up a single connection to the SQL server
 * and distributes various instances of the KAONInquirer for
 * clients to use.
 *
 * This class constantly checks for incoming client connections
 * which it then spawns individual threads to manage them.
 *************************************************************/

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

import java.util.*;

public class KAONManager {
	//instance variables
	Vector queue;
	Threads connections;

	//Settings variables
	private int documentPort;
	private int inquirerPort;

	//final variables
	private final String PROPERTIES_FILE = "kaon_manager_settings.txt";

	public KAONManager() {
		setupSettings();
	}//KAONManager

	/**
	 * Sets up the settings from the properties file
	 */
	private void setupSettings() {
		//Load Properties File
		Properties = settings = new Properties();
		try {
			settings.load(new FileInputStream(PROPERTIES_FILE));
		} catch (Exception e) {
			System.out.println("Error loading properties file: " + PROPERTIES_FILE + ".");
			System.exit(1);
		}

		//Setup properties file
		String temp = "";
		temp = settings.getProperty("MANAGER_DOCUMENT_PORT");
		if (temp == null) {
			System.out.println("Settings: MANAGER_DOCUMENT_PORT field empty.");
			System.exit(1);
		}
		else {
			try {
				documentPort = Integer.parseInt(temp);
			} catch (Exception e) {
				System.out.println("MANAGER_DOCUMENT_PORT must be an integer");
				System.exit(1);
			}
		}

		temp = settings.getProperty("MANAGER_INQUIRER_PORT");
		if (temp == null) {
			System.out.println("Settings: MANAGER_INQUIRER_PORT field empty.");
			System.exit(1);
		}
		else {
			try {
				inquirerPort = Integer.parseInt(temp);
			} catch (Exception e) {
				System.out.println("MANAGER_INQUIRER_PORT must be an integer");
				System.exit(1);
			}
		}
	}

	/**
	 * Creates two threads, one to listen on the documentPort and the
	 * other to listen on the inquirerPort.
	 */
	private void createListenerThreads() {
		Thread inquirerListener = new KAONPortListener(inquirerPort, KAONManagerThread.ONTOLOGY_QUERY);
		Thread documentListener = new KAONPortListener(documentPort, KAONManagerThread.DOCUMENT_ADDER);
	}

	/**
	 * Add a document to the queue. When the document is the first
	 * document to add to the queue, this method triggers the
	 * KAONDocumentAdder.
	 *
	 * @param doc the document to add
	 */
	public synchronized void enqueueDocument(File doc) {

	}

	/**
	 * Remove document from queue
	 *
	 * @return the removed document
	 */
	public synchronized File dequeueDocument() {

	}

	/**
	 * Returns the size of the queue
	 *
	 * @return the size of the queue
	 */
	public synchronized int getQueueSize() {

	}

	/**
	 * This inner class listens on the given port and
	 * spawns a new instance of the KAONManagerThread for the client
	 */
	public class KAONPortListener extends Thread {
		int port;
		int type;

		/**
		 * KAONPortListener constructor
		 * @param port the port to listen on
		 * @param type the type of KAONListener this is
		 */
		public KAONPortListener(int port, int type) {
			this.port = port;
			this.type = type;
		}

		/**
		 * The run method
		 */
		 public void run() {
			listenOnPort();
		 }

		 /**
		  * Listen on the document port and spawn a new instance
		  * of the KAONManagerThread for the client
		  */
		 public listenOnPort() {

		 }
	}
}