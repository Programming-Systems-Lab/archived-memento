package psl.memento.pervasive.recommendation.util;

import psl.memento.pervasive.recommendation.KeywordContainer;
import psl.memento.pervasive.recommendation.KeywordFinder;

/**
 * This is a skeleton abstract class for a KeywordFinder.
 * 
 * doWork() should contain the main part of the KeywordFinder algorithm (including a sleep() if it does nothing).
 * returnData() gets called whenever data is requested by the SuggestionManager. the KeywordContainer in which data will be 
 * returned is available via the protected variable _returnData;
 * stop() can be used to permanently stop the KeywordFinder
 * 
 * The above makes sense if the source code of this file is looked at in detail
 * 
 * This should be used for easing implementation of straight-forward KeywordFinder
 * 
 * Note that after constructing and object extending KeywordFinderSimpleImpl, it is imperative to start a Thread object wraping
 * the KFSI. This needs to be done before any other methods are called.
 */
public abstract class KeywordFinderSimpleImpl implements KeywordFinder {

	// used to return data to the component managing this KeywordFinder
	protected KeywordContainer _returnData = null;

	// whether or not this compoentn is running. If it is not running then by definition, the stop() method has been called.
	// a KFSI is considered to be running if the constructor has been invoked but the thread has not been started.
	protected boolean _running = true;

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		// until stop is called
		while (_running) {

			doWork(); // do work

			if (_returnData != null) {
				// if the managing component wants Keywords, then send them out

				returnData();

				_returnData.close();
				_returnData = null;
			}
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#signal(psl.memento.pervasive.recommendation.KeywordContainer)
	 */
	public void signal(KeywordContainer kc) {
		_returnData = kc;
	}

	/**
	 * @see psl.memento.pervasive.recommendation.KeywordFinder@stop()
	 */
	public void stop() {
		_running = false;
	}

	/**
	 * Method doWork. @see run() for more details
	 * This is called repeatedly, and is where most of the KeywordFinder's work should be put, make sure to return from this
	 * method often so that data can be returned.
	 */
	protected abstract void doWork();

	/**
	 * Method returnData. @see run() for more details
	 * This is called when data should be returned to the managing component. The KeywordFinderSimpleImpl should put
	 * data that can be returned inside of the _returnData KeywordContainer, that is all.
	 */
	protected abstract void returnData();
}
