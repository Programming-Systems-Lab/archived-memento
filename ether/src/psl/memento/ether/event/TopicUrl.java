package psl.memento.ether.event;

import java.net.MalformedURLException;;

/**
 * Represents a topic URL of the form 'topic:{topic-name}@{hostname}:{port}',
 * similar to mailto URLs.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class TopicUrl
{
	private String name;
	private String hostname;
	private int port;

	/**
	 * Construct a new TopicUrl with the given URL of an event topic.
	 *
	 * @param topicUrl A topic URL of the form {topic}@{hostname}:{port}
	 * @throws ParseException
	 *         if the topic URL is malformed
	 **/
	public TopicUrl(String topicUrl) throws MalformedURLException
	{
		if (topicUrl == null)
		{
			String msg = "topicUrl cannot be null";
			throw new IllegalArgumentException(msg);
		}

		init(topicUrl);
	}

	/**
	 * Parse a topic URL of the form 'topic:{name}@{hostname}:{port} and
	 */
	private void init(String topicUrl) throws MalformedURLException
	{
		// chop off the first 6 letters, which are 'topic:'
		topicUrl = topicUrl.substring(6);

		// find the '@' sign
		int atPos = topicUrl.indexOf('@');
		if (atPos < 1)
		{
			String msg = "no at sign or no topic";
			throw new MalformedURLException(msg);
		}

      // the name is everything before the '@' sign
		name = topicUrl.substring(atPos);

		// get everything between the '@' and the ':'
		int colonPos = topicUrl.indexOf(':');
		if (colonPos < 1)
		{
			String msg = "no colon in the topic";
			throw new MalformedURLException(msg);
		}

		hostname = topicUrl.substring(atPos + 1, colonPos);

		// get the port
		port = Integer.parseInt(topicUrl.substring(colonPos + 1));
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
	 * Get the IP address or hostname of the event server hosting a topic.
	 *
	 * @return hostname of the event server hosting the topic
	 **/
	public String getHostname()
	{
		return hostname;
	}

	/**
	 * Set the hostname or IP address of the hostname of the event server
	 * hosting a topic
	 *
	 * @param hostname hostname or IP of the event server hosting the topic
	 **/
	public void setHostname(String host)
	{
		if (host == null)
		{
			String msg = "hostname can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.hostname = host;
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
	 * Retrieve the TopicUrl in the URL form. This is the form
	 * 'topic:{topic}@{hostname}:{port}'.
	 *
	 * @return the topic URL in URL form
	 **/
	public String toUrl()
	{
		StringBuffer buf = new StringBuffer("topic:").append(name);
		buf.append('@').append(hostname).append(':').append(port);
		return buf.toString();
	}

	/**
	 * Get the URL representation of the Topic.
	 *
	 * @return the same as <code>toUrl</code>
	 */
	public String toString()
	{
		return this.toUrl();
	}

	/**
	 * Determine if this TopicUrl is the same as another.
	 *
	 * @param o Object to test for equality
	 * @return <c>true</c> if this topic is the same as <c>o</c> else
	 *         <c>false</c>
	 **/
	public boolean equals(Object o)
	{
		if ((o == null) || !(o instanceof TopicUrl))
		{
			return false;
		}
		else
		{
			TopicUrl t = (TopicUrl) o;
			return (t.hostname.equals(hostname)) && (t.name.equals(name))
				&& (t.port == port);
		}
	}

	/**
	 * Get a distinct hashcode for this TopicUrl.
	 *
	 * @return distinct hashcode for this TopicUrl
	 **/
	public int hashCode()
	{
		return toUrl().hashCode();
	}
}

