package aether.event;

/**
 * Defines a queue of events that must be processed in some logical manner.
 *
 * // TODO: add a close() method to enforce idea of closing queue
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface EventQueue
{
	/**
	 * Enqueue a Event onto the queue.
	 *
	 * @param msg Event to queue
	 */
    public void enqueue(Event msg);

	/**
	 * Dequeue a event from the queue.
	 *
	 * @return Event dequeued from the queue
	 */
	public Event dequeue();

    /**
     * Close the event queue to halt all further processing.
     */
    public void close();
}
