package aether.server.domain;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.Beans;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;

import java.beans.beancontext.BeanContextMembershipListener;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.io.*;

import aether.net.Publisher;
import aether.net.Monitor;
import aether.net.Connection;
import aether.net.DefaultMonitor;
import aether.server.framework.Advertising;
import aether.server.framework.Identifiable;
import aether.event.Notice;
import aether.event.EventHandler;
import aether.event.Event;
import org.apache.log4j.Logger;

/**
 * Represents an agent in the peer-to-peer master server.
 *
 * TODO: this class should retreive Advertisement metadata for a component
 * --- by using BeanInfo objects
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DomainAgent extends BeanContextChildSupport
{
	private BeanContextMembershipListener bcml;
	private EventHandler handler;
	private String domainTopic;

	private Monitor monitor;
	private Connection connection;
	private DomainInfo domainInfo;
	private Map compMap = Collections.synchronizedMap(new HashMap());
	private Map remoteMap = Collections.synchronizedMap(new HashMap());
	private String guid;

	private static final Logger log = Logger.getLogger(DomainAgent.class);

    /**
     * Set the Connection to be used by the DomainAgent.
     *
     * @param conn Connection used to send events
     */
    public void setConnection(Connection conn)
    {
        if (conn == null)
        {
            String msg = "conn can't be null";
            throw new IllegalArgumentException(msg);
        }

        this.connection = conn;
    }

    /**
     * Get the Connection to be used by the DomainAgent.
     *
     * @return Connection used to send/receive events.
     */
    public Connection getConnection()
    {
        return connection;
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

	public void initialize() throws IOException
	{
        connection.open();
		monitor = new DefaultMonitor(connection, false);
        monitor.subscribe(getDomainTopic());

		bcml = new DomainListener();
		getBeanContext().addBeanContextMembershipListener(bcml);

		handler = new DomainHandler();

	    monitor.subscribe(getDomainTopic());
		monitor.addNoticeListener(handler);
	}

	public void dispose()
	{
		try
		{
			monitor.removeNoticeListener(handler);
			monitor.unsubscribe(this.getDomainTopic());
			handler = null;
		}
		catch (IOException ioe)
		{
			log.warn("failed to unsubscribe from domain topic");
		}

		getBeanContext().removeBeanContextMembershipListener(bcml);
	}

	private ComponentInfo createComponentInfo(Identifiable idf)
	{
		ComponentInfo ci = new ComponentInfo();
		ci.setComponentId(idf.getGuid());

        // xxx: can't use the class name as the component type! will need
        // --- possibly another interface, TypeInfo, that exposes type info
		ci.setType(idf.getClass().getName());

		if (idf instanceof Advertising)
		{
			Advertising adv = (Advertising) idf;
			ci.setAdvertisement(adv.getAdvertisement());
		}

		return ci;
	}

	private void register(Identifiable idf)
	{
		ComponentInfo ci = createComponentInfo(idf);
		compMap.put(idf.getGuid(), ci);

		log.info("registered component " + idf + " with info" + ci);
		fireRegistered(idf, ci);
	}

	private void unregister(Identifiable idf)
	{
		ComponentInfo ci = (ComponentInfo) compMap.remove(idf.getGuid());

		fireUnregistered(idf, ci);
	}

	private void fireRegistered(Identifiable idf, ComponentInfo ci)
	{
		Notice notice = new Notice();
        // xxx: how does a DomainAgent know its own GUID?
		//notice.setSourceId(this.getGuid());
		notice.setTopicId(this.getDomainTopic());

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(ci);
			//notice.setBody(baos.toByteArray());
		}
		catch (IOException ioe)
		{
			// todo: in the future tell someobdy we fucked up
			log.error("failed to write advertisement data", ioe);
		}

		// send the notice
		try
		{
			connection.publish(notice);
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
		//notice.setSourceId(this.getGuid());
		notice.setTopicId(this.getDomainTopic());

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(ci);
			// notice.setBody(baos.toByteArray());
		}
		catch (IOException ioe)
		{
			// todo: in the future tell someobdy we fucked up
			log.error("failed to write advertisement data", ioe);
		}

		// send the notice
		try
		{
			connection.publish(notice);
		}
		catch (IOException ioe)
		{
			// todo: in the future tell somebody we fucked up
			log.error("failed to send component register event", ioe);
		}
	}

	private void processRemoteRegister(Notice notice)
	{
		; // todo: implement this method
	}

	private void processRemoteUnregister(Notice notice)
	{
		; // todo: implement this method
	}

	/**
	 * Listener that watches as components enter and leave the container and
	 * notifies the domain agent component.
	 */
	private class DomainListener implements BeanContextMembershipListener
	{
		public void childrenAdded(BeanContextMembershipEvent bcme)
		{
			Object[] children = bcme.toArray();

			for (int i = 0; i < children.length; ++i)
			{
				if (children[i] instanceof Identifiable)
				{
					register((Identifiable) children[i]);
				}
			}
		}

		public void childrenRemoved(BeanContextMembershipEvent bcme)
		{
			Object[] c = bcme.toArray();

			for (int i = 0; i < c.length; ++i)
			{
                if (c[i] instanceof Identifiable)
				{
					unregister((Identifiable) c[i]);
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

			// xxx: ignore events from ourselves
			// if (notice.getSourceId().equals(DomainAgent.this.getGuid()))
			// {
			//	return;
			// }

		    // todo: implement this method
		}
	}
}
