package psl.memento.ether.message;

import psl.memento.ether.event.TopicUrl;
import psl.memento.ether.event.ComponentUrl;

import java.net.MalformedURLException;

/**
 * A MessageUrl identifies a component on a remote server which can receive
 * point-to-point messages. A message URL is actually composed of two URLs
 * (though this is an implementation detail which might change, clients should
 * treat message URLs opaquely): a component URL which denotes the component
 * that should receive the message addressed to this URL and a TopicUrl which
 * identifies the topic that events (which contain messages) should be sent to.
 * A MessageUrl is generally of the form:
 * 'message:{componentURL}!{topicUrl}' which expands to
 * 'message:component:{componentID}:{containerID}@{host}!
 * 	topic:{name}@{host}:{port}
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class MessageUrl
{
	private ComponentUrl compUrl;
	private TopicUrl msgTopicUrl;

	/**
	 * Construct a new MessageUrl from a string representation.
	 *
	 * @param messageUrl string representation of a message url
	 * @throws MalformedUrlException
	 *         if the message url is invalid
	 */
	public MessageUrl(String messageUrl) throws MalformedURLException
	{
		if (messageUrl == null)
		{
			String msg = "messageUrl can't be null";
			throw new IllegalArgumentException(msg);
		}

		init(messageUrl);
	}

	/**
	 * Construct a messageUrl from its constiuent topic and component Url
	 * parts.
	 *
	 * @param topicUrl topic URL portion of the message url
	 * @param compUrl  component url portion of the message url
	 */
	public MessageUrl(TopicUrl topicUrl, ComponentUrl compUrl)
	{
		if ((topicUrl == null) || (compUrl == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.msgTopicUrl = topicUrl;
		this.compUrl = compUrl;
	}

	/**
	 * Parse a messageUrl and initialize this object of the form
	 * 'message:{componentUrl}!{topicUrl}
	 *
	 * @param messageUrl string representation of the message url
	 * @throws MalformedUrlException
	 *         if the message url is invalid
	 */
	private void init(String messageUrl) throws MalformedURLException
	{
		// cut off the first 8 characters, the 'message:'
		messageUrl = messageUrl.substring(8);

		// find the '!' sign
		int separatorPos = messageUrl.indexOf('!');
		if (separatorPos < 0)
		{
			String msg = "no exclamation mark separator in " + messageUrl;
			throw new MalformedURLException(msg);
		}

		// the componentUrl is everything before the separator
		String componentUrlStr = messageUrl.substring(0, separatorPos);
		compUrl = new ComponentUrl(componentUrlStr);

		// the topicUrl is everything after the the separator
		String topicUrlStr = messageUrl.substring(separatorPos + 1);
		msgTopicUrl = new TopicUrl(topicUrlStr);
	}

	/**
	 * Get the topic URL portion of the message url.
	 *
	 * @return topicUrl portion of the message url
	 */
	public TopicUrl getMessageTopicUrl()
	{
		return msgTopicUrl;
	}

	/**
	 * Set the topic URL portion of the message url.
	 *
	 * @param msgTopicUrl topic which message-events should be sent to
	 */
	public void setMessageTopicUrl(TopicUrl msgTopicUrl)
	{
		if (msgTopicUrl == null)
		{
			String msg = "msgTopicUrl can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.msgTopicUrl = msgTopicUrl;
	}

	/**
	 * Get the component URL portion of the message url.
	 *
	 * @return component URL portion of the message url
	 */
	public ComponentUrl getComponentUrl()
	{
		return compUrl;
	}

	/**
	 * Set the component URL portion of the message url.
	 *
	 * @param compUrl component URL portion of the message url
	 */
	public void setComponentUrl(ComponentUrl compUrl)
	{
		if (compUrl == null)
		{
			String msg = "compUrl can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.compUrl = compUrl;
	}

   /**
	 * Get a string representation of the message url.
	 *
	 * @return string representation of this url
	 */
	public String toUrl()
	{
		StringBuffer buf = new StringBuffer("message:");
		buf.append(compUrl.toUrl()).append('!').append(msgTopicUrl.toUrl());
		return buf.toString();
	}

	/**
	 * Get a string representation of the message url.
	 *
	 * @return the results of <code>toUrl()</code>
	 */
	public String toString()
	{
		return toUrl();
	}

   /**
	 * Determine if this message url equals another one.
	 *
	 * @param o Object to test against for equality
	 * @return <code>true</code> if this message url equals another
	 */
	public boolean equals(Object o)
	{
		if ((o == null) || !(o instanceof MessageUrl))
		{
			return false;
		}

		MessageUrl murl = (MessageUrl) o;
		return (murl.compUrl.equals(compUrl)) &&
			(murl.msgTopicUrl.equals(msgTopicUrl));
	}

	/**
	 * Get a semi-distinct hashcode for this message url.
	 *
	 * @return a semi-distinct hashcode for this url
	 */
	public int hashCode()
	{
		return toUrl().hashCode();
	}
}
