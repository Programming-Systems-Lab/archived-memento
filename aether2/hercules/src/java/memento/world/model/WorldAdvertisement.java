package memento.world.model;

import aether.server.domain.Advertisement;

/**
 * Describes a world that is now available.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class WorldAdvertisement extends Advertisement
{
	/**
	 * Requests to change a world model are broadcast to this topic which is
	 * monitored by controllers and replicators.
	 */
	public static final String RequestTopic = "request-topic";

	/**
	 * Property identifies the topic that the world model resides at. Those
	 * interested in receiving updates to the world model should subscribe
	 * to this topic.
	 */
	public static final String ModelTopic = "view-topic";
}
