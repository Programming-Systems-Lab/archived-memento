package psl.memento.pervasive.recommendation.util;

import psl.memento.pervasive.recommendation.Search;
import psl.memento.pervasive.recommendation.SuggestionContainer;

/**
 * An Abstract simple implementation of a Search class. This should be extended and the following
 * methods refined:
 * 
 * doWork() is supposed to consist of the "searching" part of the algorithm. The method needs to end every once in a while so that
 * data can be returned.
 * returnData() is invoked whenever the Manager wants a Suggestion
 * _returnData contains the SuggestionContainer that should be used to contact the Manager
 * stop() can be used to permanently stop the search (aka it will not be possible to restart it with this Object)
 * 
 * Note that after constructing an object extending this class, it is imperative to start a Thread object. This should be done
 * before any methods are invoked.
 * 
 * @author jg253
 */
public abstract class SearchSimpleImpl implements Search {

	// used to return data to the component managing the Search
	protected SuggestionContainer _returnData = null;

	// whether or not this search is running. If it is not running then by definition, the stop() method has been invoked.
	// the SSI is considered to be running after construction even with it has not been given its own thread of execution
	protected boolean _running = true;

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (_running) {
			doWork();

			if (_returnData != null) {

				returnData();

				_returnData.close();
				_returnData = null;
			}
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Search#signal(psl.memento.pervasive.recommendation.SuggestionContainer)
	 */
	public void signal(SuggestionContainer sc) {
		_returnData = sc;
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Search#stop()
	 */
	public void stop() {
		_running = false;
	}

	/**
	 * Method returnData. @see run() for more details
	 * This is called when data should be returned to the managing component. Data to be returned should be put in the Container available
	 * via the _returnData variable. That is all.
	 */
	protected abstract void returnData();

	/**
	 * Method doWork. @see run() for more details
	 * This is called repeatedly and is where most of the Search's work should be done. Make sure to return from this method often.
	 */
	protected abstract void doWork();

}
