package psl.memento.pervasive.recommendation.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import psl.memento.pervasive.recommendation.Conversation;
import psl.memento.pervasive.recommendation.ConversationMessage;
import psl.memento.pervasive.recommendation.User;

/**
 * Console interface for the ConversationTestMain
 */
public class ConversationTestConsole
	extends ConversationTestMain
	implements Runnable {

	private User _a;
	private User _b;
	private User _c;
	private Conversation _conversation;

	/**
	 * Constructor ConversationTestConsole. The three Users and the Conversation they are in.
	 * @param a 
	 * @param b
	 * @param c
	 */
	public ConversationTestConsole(User a, User b, User c, Conversation co) {
		_a = a;
		_b = b;
		_c = c;
		_conversation = co;
	}

	/**
	 * Run the pseudo-chat client
	 */
	public void run() {
		String input;
		BufferedReader keyboard =
			new BufferedReader(new InputStreamReader(System.in));
		try {
			// as long as we have input
			while (!(input = keyboard.readLine())
				.toLowerCase()
				.equals("quit")) {
				StringTokenizer s = new StringTokenizer(input);
				String command = "", options;

				if (s.hasMoreTokens())
					command = s.nextToken(); // the command is the user's name
				if (input.length() > command.length()) // the rest of the message
					options = input.substring(command.length() + 1);
				else
					options = "";

				if (command == null); // do nothing
				else if (command.equals("a")) { // if user a
					ConversationMessage cm =
						new ConversationMessage(_a, options);
					_conversation.addConversationMessage(cm);
				} else if (command.equals("b")) { // if user b
					ConversationMessage cm =
						new ConversationMessage(_b, options);
					_conversation.addConversationMessage(cm);
				} else if (command.equals("c")) { // if user c
					ConversationMessage cm =
						new ConversationMessage(_c, options);
					_conversation.addConversationMessage(cm);
				}
				Thread.sleep(100); // wait a bit until processing the next message
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
