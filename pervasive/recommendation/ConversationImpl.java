package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.NoRemoveIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Basic implementation of the Conversation Interface. 
 * @see psl.memento.pervasive.recommendation.Conversation
 */
public class ConversationImpl implements Conversation {

	// general conversation engine for this conversation. Has access to all messages in the conversation that the Conversation's creator 
	// would have access to
	private ConversationSuggestionEngine _generalCSE;

	// state of this conversation (started, ended, etc.)
	private ConversationState _state;

	// log of all messages in the conversation
	private ConversationLog _log;

	// users in conversation
	private ArrayList _users;

	// logs registered with conversation (and the corresponding User)
	private HashMap _logs;

	// user who created the conversation
	private User _creator;

	/**
	 * Create a conversation. Note that creator is not automatically added to the conversation as a participant.
	 */
	public ConversationImpl(User creator) throws GenericException {
		_state = new ConversationState(ConversationState.constructed);
		_users = new ArrayList();
		_log = new ConversationLog();
		_logs = new HashMap();
		_creator = creator;

		// ask to create a suggestionEngine that has the same access as the conversation's creator.
		_generalCSE =
			SuggestionEngineFactory.loadConversationSEFromProfile(
				_creator.getProfile());
		try {
			_generalCSE.registerLog(_log);
		} catch (GenericException e) {
			e.printStackTrace();
			throw new GenericException("Creation Unsuccessful");
		}
	}

	public void init() {
		_state.changeTo(ConversationState.init);
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#start()
	 */
	public void start() {
		_state.changeTo(ConversationState.started);
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#end()
	 */
	public void end() {
		_state.changeTo(ConversationState.ended);
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#addUser(psl.memento.pervasive.recommendation.User)
	 * @throws GenericException if the conversation already has User u
	 */
	public void addUser(User u) throws GenericException {
		synchronized (_users) {
			if (_users.contains(u)) {
				throw new GenericException(
					"Conversation already contains User " + u);
			}
			_users.add(u);
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#removeUser(psl.memento.pervasive.recommendation.User)
	 * @throws GenericException if the conversation does not have User u
	 */
	public void removeUser(User u) throws GenericException {
		synchronized (_users) {
			if (!_users.contains(u)) {
				throw new GenericException(
					"Conversation does not contain User " + u);
			}
			_users.remove(u);
			if (_logs.get(u) != null) {
				_logs.remove(u);
			}
		}
	}

	/**
	 * Method getUsers.
	 * Note that the returned Iterator does not support remove()
	 * @return Iterator
	 */
	public Iterator getUsers() {
		return new NoRemoveIterator(_users.iterator());
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#addConversationMessage(psl.memento.pervasive.recommendation.ConversationMessage)
	 * @throws GenericException if the conversation is not started, or if the cm's User is not in conversation
	 */
	public void addConversationMessage(ConversationMessage cm)
		throws GenericException {

		// make sure the conversation is started
		if (!_state.equals(ConversationState.started)) {
			throw new GenericException("Cannot add ConversationMessage as Conversation is not started");
		}

		User u = cm.getUser();
		synchronized (_users) {
			if (!_users.contains(u)) {
				throw new GenericException(
					"Cannot add ConversationMessage as User "
						+ u
						+ " not in Conversation");
			}

			// all messagse go to main log, only messages that the Conversation's creator has access to should be added
			if (hasSecurityAccess(_creator, cm)) {
				_log.add(cm);
			}

			// send message to all logs with the correct access 
			for (int i = 0; i < _users.size(); i++) {
				User u2 = (User) _users.get(i);
				if (hasSecurityAccess(u2, cm)) {
					ConversationLog cl = (ConversationLog) _logs.get(u2);
					cl.add(cm);
				}
			}
		}
	}
	/**
	 * Method hasSecurityAccess.
	 * In the simple implementation, this always returns true.
	 */
	// TODO support security via User's Profiles for Conversation Messages
	private boolean hasSecurityAccess(User u, ConversationMessage cm) {
		return true;
	}
	
	/**
	 * Method hasSecurityAccess.
	 * In the simple implementation, this always returns true.
	 */
	//	TODO support security via User's Profiles for Conversation Messages
	private boolean hasSecurityAccess(
		User u,
		ConversationSuggestionEngine cse) {
		return true;
	}

	/**
	 * Returns the Conversation's state.
	 * @return ConversationState
	 */
	public ConversationState getState() {
		return _state;
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#hasUser(psl.memento.pervasive.recommendation.User)
	 */
	public boolean hasUser(User u) {
		return _users.contains(u);
	}

	/**
	 * In this implementation, all User's have access to the GeneralConversationSuggestionEngine, no Profile checking is done
	 * @see psl.memento.pervasive.recommendation.Conversation#getGeneralSuggestionEngine(psl.memento.pervasive.recommendation.SecurityProfile)
	 */
	public SuggestionEngine getGeneralConversationSuggestionEngine(User u)
		throws GenericException {
		if (hasSecurityAccess(u, _generalCSE)) {
			return _generalCSE;
		}
		return null;
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Conversation#registerCallback(psl.memento.pervasive.recommendation.ConversationLog)
	 * @throws GenericException if the user already has a conversationlog associated with him/her
	 **/
	public void registerLog(User u, ConversationLog cl)
		throws GenericException {
		if (_logs.get(u) != null) {
			throw new GenericException(
				"Unable to register Log for User "
					+ u
					+ " as User already has ConversationLog associated with this Conversation");
		}
		synchronized (_logs) {
			_logs.put(u, cl);
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.EventSupport#registerCallback(psl.memento.pervasive.recommendation.EventCallback)
	 */
	// TODO add support for Events 
	public void registerCallback(EventCallback ec) {
	}

	/**
	 * @see psl.memento.pervasive.recommendation.EventSupport#deregisterCallback(psl.memento.pervasive.recommendation.EventCallback)
	 */
	// TODO add support for Events
	public void unregisterCallback(EventCallback ec) throws GenericException {
	}
}
