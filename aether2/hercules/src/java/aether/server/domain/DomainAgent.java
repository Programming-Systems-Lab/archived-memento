package aether.server.domain;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.Beans;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;

import java.beans.beancontext.BeanContextMembershipListener;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.io.*;

import aether.net.Publisher;
import aether.net.Monitor;
import aether.net.Connection;
import aether.server.framework.Advertising;
import aether.server.framework.Identifiable;
import aether.event.Notice;
import aether.event.EventHandler;
import aether.event.Event;
import org.apache.log4j.Logger;

/**
 * Represents an agent in the peer-to-peer master server.
 *
 * TODO: test this class!
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DomainAgent extends DefaultComponent implements Initializable,
		Disposable, Identifiable
{
	private BeanContextMembershipListener bcml;
	private EventHandler handler;
	private String domainTopic;
	private Publisher publisher;
	private Monitor monitor;
	private Connection connection;
	private DomainInfo domainInfo;
	private Map compMap = Collections.synchronizedMap(new HashMap());
	private Map remoteMap = Collections.synchronizedMap(new HashMap());
	private String guid;

	private static final Logger log = Logger.getLogger(DomainAgent.class);

	public void initialize() throws ComponentException
	{
		connection = (Connection)
				requireService(this, this, Connection.class, null, this);
		publisher = (Publisher)
				requireService(this, this, Publisher.class, null, this);
		monitor = (Monitor)
				requireService(this, this, Monitor.class, connection, this);

		bcml = new DomainListener();
		getContainer().addBeanContextMembershipListener(bcml);

		handler = new DomainHandler();
		try
		{
			monitor.subscribe(getDomainTopic());
			monitor.addNoticeListener(handler);
		}
		catch (IOException ioe)
		{
			String msg = "couldn't subscribe to domain topic "
					+ getDomainTopic();
			throw new ComponentException(msg, ioe);
		}
	}

	public void dispose() throws ComponentException
	{
		getContainer().releaseService(this, this, connection);
		getContainer().releaseService(this, this, publisher);

		try
		{
			monitor.removeNoticeListener(handler);
			monitor.unsubscribe(this.getDomainTopic());
			handler = null;
		}
		catch (IOException ioe)
		{
			log.warn("failed to unsubscribe from domain topic");
			throw new ComponentException("bad connection", ioe);
		}
		getContainer().releaseService(this, this, monitor);

		getContainer().removeBeanContextMembershipListener(bcml);
	}

	/**
	 * Get the domain topic on which domain notices are exchanged.
	 *
	 * @return domain topic on which domain notices are exchanged
	 */
	public String getDomainTopic()
	{
		return domainTopic;
	}

	/**
	 * Set the domain topic on which domain notices are exchanged.
	 *
	 * @param domainTopic domain topic on which domain notices are exchanged
	 */
	public void setDomainTopic(String domainTopic)
	{
		this.domainTopic = domainTopic;
	}

	/**
	 * Get the info for the domain this agent is acting for.
	 *
	 * @return info for the domain this agent is acting for
	 */
	public DomainInfo getDomainInfo()
	{
		return domainInfo;
	}

	/**
	 * Set the info for the domain this agent will act for.
	 *
	 * @param domainInfo info for the domain this agent will act for
	 */
	public void setDomainInfo(DomainInfo domainInfo)
	{
		this.domainInfo = domainInfo;
	}

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	private ComponentInfo createComponentInfo(Identifiable idf)
	{
		ComponentInfo ci = new ComponentInfo();
		ci.setComponentId(idf.getGuid());
		ci.setType(idf.getClass().getName());

		if (getContainer().getBeans().isInstanceOf(idf, Advertising.class))
		{
			Advertising adv = (Advertising) getContainer().getBeans()
					.getInstanceOf(idf, Advertising.class);
			ci.setAdvertisement(adv.getAdvertisement());
		}

		return ci;
	}

	private void register(Advertising adv)
	{
		ComponentInfo ci = createComponentInfo(adv);
		compMap.put(adv.getGuid(), ci);

		log.info("registered component " + adv + " with info" + ci);
		// fire the remote event
		fireRegistered(adv, ci);
	}

	private void register(Identifiable idf)
	{
		ComponentInfo ci = createComponentInfo(idf);
		compMap.put(idf.getGuid(), ci);

		log.info("registered component " + idf + " with info" + ci);
		fireRegistered(idf, ci);
	}

	private void unregister(Advertising adv)
	{
		ComponentInfo ci = (ComponentInfo) compMap.remove(adv.getGuid());

		fireUnregistered(adv, ci);
	}

	private void unregister(Identifiable idf)
	{
		ComponentInfo ci = (ComponentInfo) compMap.remove(idf.getGuid());

		fireUnregistered(idf, ci);
	}

	private void fireRegistered(Identifiable idf, ComponentInfo ci)
	{
		Notice notice = new Notice();
		notice.setSourceId(this.getGuid());
		notice.setTopicId(this.getDomainTopic());
		notice.setHeader("dae-type", "register");
		notice.setHeader("re", idf.getGuid());

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(ci);
			notice.setBody(baos.toByteArray());
		}
		catch (IOException ioe)
		{
			// todo: in the future tell someobdy we fucked up
			log.error("failed to write advertisement data", ioe);
		}


		// send the notice
		try
		{
			publisher.publish(notice);
		}
		catch (IOException ioe)
		{
			// todo: in the future tell somebody we fucked up
			log.error("failed to send component register event", ioe);
		}
	}

	private void fireUnregistered(Identifiable idf, ComponentInfo ci)
	{
		Notice notice = new Notice();
		notice.setSourceId(this.getGuid());
		notice.setTopicId(this.getDomainTopic());
		notice.setHeader("dae-type", "unregister");
		notice.setHeader("re", idf.getGuid());

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(ci);
			notice.setBody(baos.toByteArray());
		}
		catch (IOException ioe)
		{
			// todo: in the future tell someobdy we fucked up
			log.error("failed to write advertisement data", ioe);
		}

		// send the notice
		try
		{
			publisher.publish(notice);
		}
		catch (IOException ioe)
		{
			// todo: in the future tell somebody we fucked up
			log.error("failed to send component register event", ioe);
		}
	}

	private void processRemoteRegister(Notice notice)
	{
		String remoteAgentId = notice.getSourceId();
		String remoteCompId = notice.getHeader("re");

		ComponentInfo ci = null;
		if (notice.getBody() != null)
		{
			try
			{
				ObjectInputStream in = new ObjectInputStream(
						new ByteArrayInputStream(notice.getBody()));
				ci = (ComponentInfo) in.readObject();
			}
			catch (Exception ioe)
			{
				// todo: in the future, tell somebody we messed
				log.warn("failed to read advertisement data", ioe);
			}
		}

		remoteMap.put(remoteAgentId + ":" + remoteCompId, ci);

		log.info("remote agent " + remoteAgentId + " registered component "
			+ remoteCompId + " with info " + ci);
	}

	private void processRemoteUnregister(Notice notice)
	{
		String remoteAgentId = notice.getSourceId();
		String remoteCompId = notice.getHeader("re");

		ComponentInfo ci = (ComponentInfo)
				remoteMap.remove(remoteAgentId + ":" + remoteCompId);

		log.info("remote agent " + remoteAgentId + " unregistered component "
			+ remoteCompId + " with info " + ci);
	}

	/**
	 * Listener that watches as containers enter and leave the container and
	 * notifies the domain agent component.
	 */
	private class DomainListener implements BeanContextMembershipListener
	{
		public void childrenAdded(BeanContextMembershipEvent bcme)
		{
			Object[] children = bcme.toArray();
			Beans beans = getContainer().getBeans();

			for (int i = 0; i < children.length; ++i)
			{
				if (beans.isInstanceOf(children[i], Advertising.class))
				{
					Advertising adv = (Advertising) beans
							.getInstanceOf(children[i], Advertising.class);
					register(adv);
				}
				else if (beans.isInstanceOf(children[i], Identifiable.class))
				{
					Identifiable idf = (Identifiable) beans
							.getInstanceOf(children[i], Identifiable.class);
					register(idf);
				}
			}
		}

		public void childrenRemoved(BeanContextMembershipEvent bcme)
		{
			Beans beans = getContainer().getBeans();
			Object[] c = bcme.toArray();

			for (int i = 0; i < c.length; ++i)
			{
				if (beans.isInstanceOf(c[i], Advertising.class))
				{
					Advertising adv = (Advertising) beans
							.getInstanceOf(c[i], Advertising.class);
					unregister(adv);
				} else if (beans.isInstanceOf(c[i], Identifiable.class))
				{
					Identifiable idf = (Identifiable) beans
							.getInstanceOf(c[i], Identifiable.class);
					unregister(idf);
				}
			}
		}
	}

	/**
	 * An EventHandler that watches for notifications of new events in remote
	 * containers.
	 */
	private class DomainHandler implements EventHandler
	{
		public void handle(Event event)
		{
			Notice notice = (Notice) event;

			// ignore events from ourselves
			if (notice.getSourceId().equals(DomainAgent.this.getGuid()))
			{
				return;
			}

			if (notice.getHeader("dae-type").equals("register"))
			{
				processRemoteRegister(notice);
			} else if (notice.getHeader("dae-type").equals("unregister"))
			{
				processRemoteUnregister(notice);
			}
		}
	}
}
