package psl.memento.pervasive.recommendation.test;

import java.util.Iterator;

import psl.memento.pervasive.recommendation.Conversation;
import psl.memento.pervasive.recommendation.ConversationSuggestionEngine;
import psl.memento.pervasive.recommendation.SuggestionCallback;
import psl.memento.pervasive.recommendation.SuggestionContainer;
import psl.memento.pervasive.recommendation.User;
import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.ConversationUtil;

/**
 * This class provides a testing environment for psl.memento.pervasive.recommendation. 
 * It simmulates three users conversing and outputs the psl.memento.pervasive.recommendation engine's recommendations.
 * 
 * Input format from std.in
 * (a|b|c) (message)
 * e.g.
 * a foo
 * b bar
 * c baz
 * 
 * user a says foo
 * user b says bar
 * user c says baz
 * 
 * The config files for three users are conversationconfiga.xml, conversationconfigb.xml, 
 * conversationconfigc.xml.
 * User a's config file is used for the generalSuggestionEngine. 
 */
public class ConversationTestMain implements SuggestionCallback {

	/**
	 * This main simulates a conversation between three users and may be used to test
	 * keywordfinder and search algorithms.
	 */
	public static void main(String[] args) {

		// we now load everything from the user config files
		/*
				User a = new User("usera");
				User b = new User("userb");
				User c = new User("userc");
		
				Profile p = a.getProfile();
				p.setConversationConfiguration(
					"psl/memento/pervasive/recommendation/test/conversationconfiga.xml");
				p = b.getProfile();
				p.setConversationConfiguration(
					"psl/memento/pervasive/recommendation/test/conversationconfigb.xml");
				p = c.getProfile();
				p.setConversationConfiguration(
					"psl/memento/pervasive/recommendation/test/conversationconfigc.xml"); 
		*/

		User a = null;
		User b = null;
		User c = null;

		try {
			a =
				User.loadFromFile(
					"psl/memento/pervasive/recommendation/test/usera.xml");
			b =
				User.loadFromFile(
					"psl/memento/pervasive/recommendation/test/userb.xml");
			c =
				User.loadFromFile(
					"psl/memento/pervasive/recommendation/test/userc.xml");
		} catch (GenericException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// add users to conversation and conversation to users
		Conversation conversation = null;
		try {
			// use ConversationUtil helper class
			conversation = ConversationUtil.createConversation(a);
			ConversationUtil.addUser(conversation, a);
			ConversationUtil.addUser(conversation, b);
			ConversationUtil.addUser(conversation, c);
		} catch (GenericException e) {
			e.printStackTrace();
		}
		conversation.start();

		// get a general suggestion engine for the conversation
		ConversationSuggestionEngine cse = null;
		try {
			cse =
				(
					ConversationSuggestionEngine) conversation
						.getGeneralConversationSuggestionEngine(
					a);
		} catch (GenericException e) {
			e.printStackTrace();
		}

		// test suggestioncallback
		ConversationTestMain ctm = new ConversationTestMain();
		cse.subscribeToSuggestions(ctm);

		// start thread that executes conversation
		ConversationTestConsole ctc =
			new ConversationTestConsole(a, b, c, conversation);
		Thread t = new Thread(ctc);
		t.start();

		// let's look at suggestions
		while (true) {
			SuggestionContainer sc = cse.getSuggestionsSinceLast();
			Iterator it = sc.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * To test the callback
	 */
	public void signal(SuggestionContainer sc) {
		Iterator it = sc.iterator();
		while (it.hasNext()) {
			System.out.println("CALLBACK: " + it.next());
		}
	}
}
