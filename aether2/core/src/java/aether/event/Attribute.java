package aether.event;

/**
 * Defines the fundamental attributes that can be set on events.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Attribute
{
	public interface Event
	{
		public static final String EventId = "aether.event.id";
		public static final String Time = "aether.event.time";
		public static final String EventType = "aether.event.type";
		public static final String SourceId = "aether.event.source.id";
		public static final String Headers = "aether.event.headers";
		public static final String Body = "aether.event.body";
	}

	public interface Notice extends Event
	{
		public static final String TopicId = "aether.notice.topic.id";
	}

	public interface Message
	{
		public static final String LinkId = "aether.message.link.id";
		public static final String Destination = "aether.message.dest";
	}

	public interface Request extends Message
	{
		public static final String Verb = "aether.request.verb";
		public static final String Query = "aether.request.query";
	}

	public interface Response extends Message
	{
		public static final String Code = "aether.response.code";
		public static final String ReasonLine = "aether.response.reasonLine";
	}
}
