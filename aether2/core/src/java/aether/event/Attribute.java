package aether.event;

/**
 * Defines the fundamental attributes that can be set on events.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface Attribute
{
    /**
     * Attributes common to all events.
     */
	public interface Event
	{
		public static final String EVENT_ID = "aether.event.id";
		public static final String TIME = "aether.event.time";
		public static final String EVENT_TYPE = "aether.event.type";
	}

    /**
     * Attributes unique to NOTICE events.
     */
	public interface Notice extends Event
	{
		public static final String TOPIC_ID = "aether.notice.topic.id";
	}

    /**
     * Attributes unique to Message events.
     */
	public interface Message
	{
		public static final String LINK_ID = "aether.message.link.id";
    }

    /**
     * Attributes unique to REQUEST events.
     */
	public interface Request extends Message
	{
        String DESTINATION = "aether.message.dest";
    }

    /**
     * Attributes unique to RESPONSE events.
     */
	public interface Response extends Message
	{
        public static final String RESPONSE_TO = "aether.response.response-to";
	}
}
