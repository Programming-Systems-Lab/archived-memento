package psl.memento.server.container.event;

/**
 * Represents an event server within the network.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class EventHub
{
	private String host;
	private int port;
	
	/**
	 * Construct a new EventHub.
	 * 
	 * @param host host or IP address of the event hub
	 * @param port port which the hub is listening on
	 **/
	public EventHub(String host, int port)
	{
		setHost(host);
		setPort(port);
	}
	
	/**
	 * Get the host or IP address of the event hub.
	 * 
	 * @return host or IP address of the event hub
	 **/
	public String getHost()
	{
		return host;
	}
	
	/**
	 * Set the host or IP address of the event hub.
	 * 
	 * @param host host or IP address of the event hub
	 **/
	public void setHost(String host)
	{
		if (host == null)
		{
			String msg = "host can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.host = host;
	}
	
	/**
	 * Get the port of the event hub.
	 * 
	 * @return port of the event hub
	 **/
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Set the port on which the event hub recieves subscription requests.
	 * 
	 * @param port port the event hub is recieving subscription requests
	 **/
	public void setPort(int port)
	{
		if (port < 0)
		{
			String msg = "port can't be negative";
			throw new IllegalArgumentException(msg);
		}
		
		this.port = port;
	}
}
