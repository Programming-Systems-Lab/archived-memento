package psl.memento.server.container.event;

import java.net.MalformedURLException;;

/**
 * Represents a topic URL of the form {topic}@{host}:{port}.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Topic
{
	private String name;
	private String host;
	private int port;
	
	/**
	 * Construct a new Topic with the given URL of an event topic.
	 * 
	 * @param topicUrl A topic URL of the form {topic}@{host}:{port}
	 * @throws ParseException
	 *         if the topic URL is malformed
	 **/
	public Topic(String topicUrl) throws MalformedURLException
	{
		if (topicUrl == null)
		{
			String msg = "topicUrl cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		init(topicUrl);
	}
	
	private void init(String topicUrl) throws MalformedURLException
	{
		int atPos = topicUrl.indexOf('@');
		if (atPos < 1)
		{
			String msg = "no at sign or no topic";
			throw new MalformedURLException(msg);
		}
		
		name = topicUrl.substring(atPos);
		
		// get everything between the '@' and the ':'
		int colonPos = topicUrl.indexOf(':');
		if (colonPos < 1)
		{
			String msg = "no colon in the topic";
			throw new MalformedURLException(msg);
		}
		
		host = topicUrl.substring(atPos + 1, colonPos);
		
		// get the port
		port = Integer.parseInt(
			topicUrl.substring(colonPos + 1, topicUrl.length()));
	}
	
	/**
	 * Get the name of the topic.
	 * 
	 * @return name of the topic
	 **/	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set the name of the topic.
	 * 
	 * @param name name of the topic
	 **/
	public void setName(String name)
	{
		if (name == null)
		{
			String msg = "name cannot be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.name = name;
	}
	
	/**
	 * Get the IP address or host of the event server hosting a topic.
	 * 
	 * @return hostname of the event server hosting the topic
	 **/
	public String getHost()
	{
		return host;
	}
	
	/**
	 * Set the host or IP address of the host of the event server hosting a 
	 * topic
	 * 
	 * @param host host or IP of the event server hosting the topic
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
	 * Get the port of the event server hosting the topic.
	 * 
	 * @return port of the event server hosting the topic
	 **/
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Set the port of the event server hosting the topic.
	 * 
	 * @param port port of the event server hosting a topic
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
	
	/**
	 * Retrieve the Topic in the URL form. This is {topic}@{host}:{port}.
	 * 
	 * @return the topic URL in URL form
	 **/
	public String toUrl()
	{
		StringBuffer buf = new StringBuffer(name);
		buf.append('@').append(host).append(':').append(port);
		return buf.toString();
	}
	
	/**
	 * Determine if this Topic is the same as another.
	 * 
	 * @param o Object to test for equality
	 * @return <c>true</c> if this topic is the same as <c>o</c> else 
	 *         <c>false</c>
	 **/
	public boolean equals(Object o)
	{
		if ((o == null) || !(o instanceof Topic))
		{
			return false;
		}
		else
		{
			Topic t = (Topic) o;
			return (t.host.equals(host)) && (t.name.equals(name)) 
				&& (t.port == port);
		}
	}
	
	/**
	 * Get a distinct hashcode for this Topic.
	 * 
	 * @return distinct hashcode for this Topic
	 **/
	public int hashCode()
	{
		return toUrl().hashCode();
	}
	
	
	
	
}
