package psl.memento.pervasive.kaon;

/**************************************************************
 * KAONManagerThread.java
 *
 * This class manages a single client connection to the
 * KAONManager class. It deals with client document additions.
 *************************************************************/

import java.net.*;

import edu.unika.aifb.kaon.api.oimodel.*;
import edu.unika.aifb.kaon.api.change.*;
import edu.unika.aifb.kaon.api.util.*;
import edu.unika.aifb.kaon.api.vocabulary.*;
import edu.unika.aifb.kaon.api.*;

public class KAONManagerThread extends Thread {
	//instance variables
	private Socket soc;
	private int type;
	private KAONConnection connection;

	//final variables
	public static final int DOCUMENT_ADDER = 0;
	public static final int ONTOLOGY_QUERY = 1;

	/**
	 * This method constructs the KAONManagerThread. It is
	 * given a client socket and performs one of two functions.
	 * If type is set to DOCUMENT_ADDER, it listens on the
	 * socket for files to be sent and then adds them to the
	 * queue in the Manager class.
	 *
	 * If the type is set to ONTOLOGY_QUERY, it creates an
	 * instance of the KAONInquirer and sends it over the socket
	 *
	 * @param soc the socket of the client
	 * @param type the type of connection
	 * @param connection the KAONConnection
	 */
	public KAONManagerThread(Socket soc, int type, KAONConnection connection) {
		this.soc = soc;
		this.type = type;
		this.connection = connection;
	}

	/**
	 * This method listens for documents to be sent over in the
	 * case that type is DOCUMENT_ADDER
	 */
	public void listenForDocuments() {

	}

	/**
	 * This method immediately sends an instance of the KAONInquirer
	 * over the socket to the client.
	 */
	public void sendInquirer() {

	}
}