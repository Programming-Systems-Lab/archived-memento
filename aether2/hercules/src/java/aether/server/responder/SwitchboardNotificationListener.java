package aether.server.responder;

import org.elvin.je4.NotificationListener;
import org.elvin.je4.Notification;
import org.apache.log4j.Logger;

import aether.net.Connection;

import EDU.oswego.cs.dl.util.concurrent.Executor;

/**
 * A special NotifcationListener that dispatches incoming Requests to a given
 * Responder.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class SwitchboardNotificationListener implements NotificationListener
{
    private Responder responder;
	private Connection connection;
	private Executor executor;

	private static final Logger log =
			Logger.getLogger(SwitchboardNotificationListener.class);

	/**
	 * Construct a new listener to dispatched received requests to the
	 * responder.
	 *
	 * @param resp Responder to receive notification/requests
	 * @param conn Connection used to send responses
	 * @param exec Executor to place request executors on
	 */
	public SwitchboardNotificationListener(Responder resp, Connection conn,
										   Executor exec)
	{
		if ((resp == null) || (conn == null) || (exec == null))
		{
			String msg = "no parameter be null";
			throw new IllegalArgumentException(msg);
		}

		this.responder = resp;
		this.connection = conn;
		this.executor = exec;
	}

	/**
	 * Called when an Elvin Notification arrives which is probably a Request
	 * that needs to be responded to.
	 *
	 * @param notif Notification that may be a request
	 */
	public void notificationAction(Notification notif)
	{
     	// queue up a new RequestExecutor
		try
		{
			executor.execute(new RequestExecutor(responder, notif, connection));
		}
		catch (InterruptedException e)
		{
			log.warn("got unexpected interrupted exception " + e);
			e.printStackTrace();
		}
	}

}
