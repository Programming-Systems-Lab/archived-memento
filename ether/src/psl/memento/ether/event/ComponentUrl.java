package psl.memento.ether.event;

import psl.memento.ether.util.Uid;

import java.net.MalformedURLException;

/**
 * Describes the URL of a component hosted within the ether network. The
 * network is made up of components which are constantly sending and
 * receiving events. Each component is hosted within a single container so
 * components can be uniquely identified by a unique component ID (assigned by
 * the container), a container ID (assigned by the master server) and the IP
 * address or hostname of the computer the container is on.
 * <p>
 * Component URLs are of the form:
 * 'component:{componentID}:{containerID}@{hostname}:{port}'
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentUrl
{
	private Uid componentId;
	private Uid containerID;
   private String hostname;

   /**
	 * Construct a new ComponentUrl to represent the address of some component.
	 *
	 * @param componentUrl URL of the Component
	 */
	public ComponentUrl(String componentUrl) throws MalformedURLException
	{
		if (componentUrl == null)
		{
			String msg = "componentUrl can't be null";
			throw new IllegalArgumentException(msg);
		}

      init(componentUrl);
	}

	/**
	 * Construct a new ComponentUrl out of the given parts.
	 *
	 * @param componentId container-assigned component ID
	 * @param containerId  ID of the container
	 * @param hostname     host of the component
	 */
	public ComponentUrl(Uid componentId, Uid containerId, String hostname)
	{
		if ((componentId == null) || (containerId == null) || (hostname == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.containerID = containerId;
		this.containerID = containerId;
		this.hostname = hostname;
	}

	/**
	 * Parse a component URL of the form
	 * 'component:{componentID}:{containerID}@{hostname}:{port}' and initialize
	 * the memeber variables.
	 *
	 * @param componentUrl a component url
	 * @throws MalformedURLException
	 *         if the componentUrl is invalid
	 */
	private void init(String componentUrl) throws MalformedURLException
	{
      // chop off the first 10 letters, the component:
      componentUrl = componentUrl.substring(10);

		// find the second colon pos
		int colonPos = componentUrl.indexOf(':');
      if (colonPos < 0)
		{
			String msg = "no second colon";
			throw new MalformedURLException(msg);
		}

      // the component ID is everything before the colon
		String componentIdStr = componentUrl.substring(0, colonPos);

		// find the @-sign and get the component id
		int atPos = componentUrl.indexOf('@');
		if (atPos < 0)
		{
			String msg = "no at sign";
			throw new MalformedURLException(msg);
		}

		// the container ID is everything after the colon and before the @
		String containerIdStr = componentUrl.substring(colonPos + 1, atPos);

		// find the second colon, by searching backwards
		int secondColonPos = componentUrl.indexOf(':', colonPos + 1);
      if (secondColonPos < 0)
		{
			String msg = "no second colon pos";
			throw new MalformedURLException(msg);
		}

		// the IP address is everything before the second colon and after the @
		String ipAddress = componentUrl.substring(atPos + 1, secondColonPos);

		setComponentId(new Uid(componentIdStr));
		setContainerId(new Uid(containerIdStr));
		setHostname(ipAddress);
	}

	/**
	 * Get the IP address or hostname of the container hosting the component.
	 *
	 * @return IP address or hostname of the container hosting the component
	 */
   public String getHostname()
	{
		return hostname;
	}

	/**
	 * Set the IP address or hostname of the container hosting the component.
	 *
	 * @param hostname IP address of the container hosting the component
	 */
	public void setHostname(String hostname)
	{
      this.hostname = hostname;
	}

   /**
	 * Get the container ID of the component. This is a unique ID assigned
	 * by the master server to this container.
	 *
	 * @return container ID for this container
	 */
	public Uid getContainerId()
	{
		return containerID;
	}

	/**
	 * Set the container ID of the component.
	 *
	 * @param containerID containerID of the container hosting the component
	 */
	public void setContainerId(Uid uid)
	{
		this.containerID = containerID;
	}

	/**
	 * Get the component id of the component. This is a unique ID assinged
	 * by the container to the component.
	 *
	 * @return component ID for the component
	 */
	public Uid getComponentId()
	{
		return componentId;
	}

	/**
	 * Set the component Id for the component.
	 *
	 * @param componentID componentID for the component
	 */
	public void setComponentId(Uid componentId)
	{
		this.componentId = componentId;
	}

   /**
	 * Get the URL representation for this component url. This is a string of
	 * the form: 'component:{componentID}:{containerID}@{hostname}'.
	 *
	 * @return URL representation of the component url
	 */
	public String toUrl()
	{
		StringBuffer buf = new StringBuffer("component:");
		buf.append(componentId.toString()).append(':');
		buf.append(containerID.toString()).append('@').append(hostname);
		return buf.toString();
	}

	/**
	 * Get the URL representation for this component URL.
	 *
	 * @return the results of <code>toUrl()</code>
	 */
	public String toString()
	{
		return toUrl();
	}

   /**
	 * Determine if this ComponentUrl equals another ComponentUrl.
	 *
	 * @param o ComponentUrl to test for equality against
	 */
	public boolean equals(Object o)
	{
		if ((o == null) || !(o instanceof ComponentUrl))
		{
			return false;
		}

      ComponentUrl curl = (ComponentUrl) o;
      return (curl.componentId.equals(this.componentId)) &&
			(curl.containerID.equals(this.containerID)) &&
			(curl.hostname.equals(this.hostname));
	}

	/**
	 * Get a distinct hashcode for this ComponentUrl.
	 *
	 * @return semi-distinct hashcode
	 */
	public int hashCode()
	{
		return toUrl().hashCode();
	}
}
