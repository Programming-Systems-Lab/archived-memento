package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * This tags a class as supporting Events. It provides a mechanism for callbacks to be registered and deregistered.
 * Note that this is a rudimentary implementation in that a Callback gets all events coming from teh class implementing
 * EventSupport, there is (as of yet) no support for subscribing to only certain types of events. Additionaly, 
 * there is no security mechanism in the EventSupport layer.
 * There is also no multi-level event propagation as of now.
 */
// TODO add security + specific subscriptions to Events
public interface EventSupport {

	/**
	 * Register a callback that will get all events from the class implementing EventSupport
	 */
	public void registerCallback(EventCallback ec);

	/**
	 * Unregister a callback that was receiving events from the class implementing EventSupport
	 */
	public void unregisterCallback(EventCallback ec) throws GenericException;

}
