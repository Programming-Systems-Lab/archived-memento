package aether.server;

/**
 * Defines a threadpool that units of work can be queued upon.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface ThreadPool
{
	/**
	 * Enqueue a unit of work onto the threadpool.
	 *
	 * @param work unit of work to enqueue on the threadpool
	 */
	public void execute(Runnable work);
}
