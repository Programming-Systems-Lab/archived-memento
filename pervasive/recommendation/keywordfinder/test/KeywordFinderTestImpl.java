package psl.memento.pervasive.recommendation.keywordfinder.test;

import java.util.ArrayList;

import psl.memento.pervasive.recommendation.Context;
import psl.memento.pervasive.recommendation.ConversationLogMessageStream;
import psl.memento.pervasive.recommendation.ConversationMessage;
import psl.memento.pervasive.recommendation.Keyword;
import psl.memento.pervasive.recommendation.KeywordFinderConfiguration;
import psl.memento.pervasive.recommendation.Relevance;
import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.KeywordFinderSimpleImpl;

/**
 * Test implementation of a KeywordFinder, it just spits out the ConversationMessages that it gets.
 */
public class KeywordFinderTestImpl extends KeywordFinderSimpleImpl {

	// How long to wait after one call of the DoWork() method.
	private final int _DOWORK_WAIT = 500;

	// counter of how many messages have been processed (makes debugging easier)
	private int _counter = 0;

	// the Log from which the KeywordFinder gets messages. This implementation of KeywordFinder only supports one log
	private ConversationLogMessageStream _cli = null;

	// messages to process
	private ArrayList _messages = new ArrayList();

	/**
	 * go through the ConversationLog and add messages to queue
	 */
	protected void doWork() {
		try {
			while ((_cli != null) && _cli.hasNext()) {
				ConversationMessage cm = _cli.next2();
				_messages.add(cm);
			}
			Thread.sleep(_DOWORK_WAIT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * turn ConversationMessages into Keywords 
	 */
	protected void returnData() {
		// go through queued messages
		while (_messages.size() > 0) {
			Keyword kw =
				new Keyword(
					"mirrorKFkeyword: " + _counter++ +" " + _messages.get(0),
					new Context(),
					Keyword.NO_DELAY,
					new Relevance());
			// add the keyword to the return container
			try {
				_returnData.add(kw);
			} catch (GenericException e) {
				e.printStackTrace();
			}
			_messages.remove(0);
		}
	}

	/**
	 * register a Log from which we get ConversationMessages
	 * We only support registering one log in this implementation, don't use this call more than once
	 */
	public void registerLogIterator(ConversationLogMessageStream cli)
		throws GenericException {
		if (_cli != null)
			throw new GenericException("KeywordFinder (KeywordFinderTestImpl) can only handle one ConversationLogMessageStream");
		_cli = cli;
	}

	/**
	 * initialize the KeywordFinder from a configuration
	 * Note that this does nothing in the test implementation as there is nothing to configure
	 */
	public void init(KeywordFinderConfiguration kfc) {
	}

	/**
	 * reset the keyword finding algorithm, not supported in this implementation
	 */
	public void reset() {
	}

	/**
	 * reset the keyword finding algorith using a different configuration.
	 * no support (aka has no effect) in this implementation
	 */
	public void reset(KeywordFinderConfiguration kfc) throws Exception {
	}
}
