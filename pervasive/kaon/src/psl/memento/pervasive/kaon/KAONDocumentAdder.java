package psl.memento.pervasive.kaon;

/******************************************************************
 * KAONDocumentAdder.java
 *
 * This clsss constantly adds documents to the KAONInterfacer from
 * the queue until the queue is empty. When the queue is empty, it
 * must be triggered by the KAONManager when the queue is refilled.
 ******************************************************************/

public class KAONDocumentAdder extends Thread {
	//instance variables
	KAONInterfacer interfacer;

	/**
	 * Initializes the DocumentAdder. It creates a single
	 * instance of the KAONInterfacer
	 */
	public KAONDocumentAdder {

	}

	/**
	 * This method triggers the processing of the
	 * queue in KAONManager
	 */
	public void trigger() {

	}
}