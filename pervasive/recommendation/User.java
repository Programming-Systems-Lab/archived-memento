package psl.memento.pervasive.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.NoRemoveIterator;

/**
 * User (aka person partaking in conversations) of the conversation/recommendation system
 */
public class User implements EventSupport {

	// User's name
	private String _name;

	// Profile information (essentially data on the SuggestionEngine appropriate for this User)
	private Profile _profile;

	// All the conversations this user is a part of
	private ArrayList _conversations;

	// A list of ConversationLogs linked 1-1 with the _conversations Array. We need to keep track of this to unregister Logs
	// when a user leaves a conversation
	private ArrayList _conversationCallbacks;

	// An array of ConversationSuggestionEngines also 1-1 with the _conversations
	private ArrayList _conversationSuggestionEngines;

	/** Static constructor from an xml file describing a user */
	public static User loadFromFile(String xmlpath) throws GenericException {

		String username = "";
		String conversationConfigurationPath = "";

		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(xmlpath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Element e = document.getDocumentElement();
		if (!"UserConfiguration".equals(e.getTagName())) {
			throw new GenericException("inproper xml document in file");
		}
		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element c = (Element) n;
				// 
				if ("Username".equals(c.getTagName())) {
					username = c.getAttribute("value");
				} else if (
					"ConversationConfigurationPath".equals(c.getTagName())) {
					conversationConfigurationPath = c.getAttribute("value");
				}
			}
		}
		User u = new User(username);
		Profile p = u.getProfile();
		p.setConversationConfiguration(conversationConfigurationPath);
		return u;
	}

	/**
	 * Constructor
	 * @param name the User's name
	 */
	public User(String name) {
		_conversations = new ArrayList();
		_conversationCallbacks = new ArrayList();
		_conversationSuggestionEngines = new ArrayList();
		_name = name;
		_profile = new Profile();
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the profile.
	 * @return Profile
	 */
	public Profile getProfile() {
		return _profile;
	}

	/**
	 * Method addConversation.
	 * Note that this must be called 1-1 with Conversation's addUser call. @see psl.memento.pervasive.recommendation.util.ConversationUtil for details
	 */
	public void addConversation(Conversation c) throws GenericException {
		if (c.hasUser(this)) {
			synchronized (_conversations) {
				_conversations.add(c);
				ConversationLog cc = new ConversationLog();
				_conversationCallbacks.add(cc);
				ConversationSuggestionEngine cse =
					SuggestionEngineFactory.loadConversationSEFromProfile(
						_profile);
				if (cse != null) {
					_conversationSuggestionEngines.add(cse);
					cse.registerLog(cc);
					c.registerLog(this, cc);
				} else {
					_conversations.remove(_conversations.size() - 1);
					_conversationCallbacks.remove(
						_conversationCallbacks.size() - 1);
					throw new GenericException(
						"No ConversationSuggestionEngine Available for User: "
							+ getName());
				}
			}
		} else {
			throw new GenericException("Trying to add a Conversation to a user who is not part of that Conversation object");
		}
	}

	/**
	 * Method removeConversation.
	 */
	public void removeConversation(Conversation c) throws GenericException {
		if (!c.hasUser(this)) {
			synchronized (_conversations) {
				int i = _conversations.indexOf(c);
				_conversations.remove(c);
				ConversationLog cc =
					(ConversationLog) _conversationCallbacks.get(i);
				ConversationSuggestionEngine sce =
					(
						ConversationSuggestionEngine) _conversationSuggestionEngines
							.get(
						i);
				sce.stop();
				_conversationCallbacks.remove(i);
				_conversationSuggestionEngines.remove(i);
			}
		} else {
			throw new GenericException("Trying to remove a Conversation for a user who is still part of that Conversation object");
		}
	}

	/**
	 * Method getConversations.
	 * @return Iterator
	 */
	public Iterator getConversations() {
		return new NoRemoveIterator(_conversations.iterator());
	}

	/**
	 * @see psl.memento.pervasive.recommendation.EventSupport#registerCallback(psl.memento.pervasive.recommendation.EventCallback)
	 */
	// TODO add support for the Event mechanism
	public void registerCallback(EventCallback ec) {
	}

	/**
	 * @see psl.memento.pervasive.recommendation.EventSupport#deregisterCallback(psl.memento.pervasive.recommendation.EventCallback)
	 */
	//	TODO add support for the Event mechanism
	public void unregisterCallback(EventCallback ec) throws GenericException {
	}
}
