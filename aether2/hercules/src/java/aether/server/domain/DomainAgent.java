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

import aether.net.*;
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
    private MulticastSocket multicastSocket;
	private DomainInfo domainInfo;
    private ComponentRegistry localRegistry;
    private ComponentRegistry globalRegistry;

	private static final Logger log = Logger.getLogger(DomainAgent.class);

    /**
     * Get the MulticastSocket to be used by the DomainAgent.
     *
     * @return MulticastSocket used by the DomainAgent
     */
    public MulticastSocket getMulticastSocket()
    {
        return multicastSocket;
    }

    /**
     * Set the MulticastSocket used by the DomainAgent
     *
     * @param multicastSocket MulticastSocket used by the DomainAgent
     */
    public void setMulticastSocket(MulticastSocket multicastSocket)
    {
        this.multicastSocket = multicastSocket;
    }

	/**
	 * Set the info for the domain this agent will act for.
	 *
	 * @param domainInfo info for the domain this agent will act for
	 */
	public void setDomainInfo(DomainInfo domainInfo)
	{
        if (domainInfo == null)
        {
            String msg = "domainInfo can't be null";
            throw new IllegalArgumentException(msg);
        }
		this.domainInfo = domainInfo;
	}

    /**
     * Get the DomainInfo for the Domain being managed.
     *
     * @return DomainInfo for domain being managed
     */
    public DomainInfo getDomainInfo()
    {
        return domainInfo;
    }

    /**
     * Get the ComponentRegistry for components in the local container.
     *
     * @return registry for components in the local container
     */
    public ComponentRegistry getLocalRegistry()
    {
        return localRegistry;
    }

    /**
     * Set the local ComponentRegistry.
     *
     * @param localRegistry local ComponentRegistry
     */
    public void setLocalRegistry(ComponentRegistry localRegistry)
    {
        this.localRegistry = localRegistry;
    }

    /**
     * Get the global ComponentRegistry for tracking information about all
     * Components everywhere.
     *
     * @return global ComponentRegistry
     */
    public ComponentRegistry getGlobalRegistry()
    {
        return globalRegistry;
    }

    /**
     * Set the global ComponentRegistry.
     *
     * @param globalRegistry global ComponentRegistry
     */
    public void setGlobalRegistry(ComponentRegistry globalRegistry)
    {
        this.globalRegistry = globalRegistry;
    }

	public void start() throws IOException
	{
		bcml = new DomainListener();
		getBeanContext().addBeanContextMembershipListener(bcml);

		handler = new DomainHandler();

        multicastSocket.open();
        multicastSocket.subscribe(domainInfo.getDomainTopic());
		multicastSocket.addEventHandler(handler);
	}

	public void stop() throws IOException
	{
        multicastSocket.unsubscribe(domainInfo.getDomainTopic());
	    multicastSocket.removeEventHandler(handler);
        multicastSocket.close();

		handler = null;
		getBeanContext().removeBeanContextMembershipListener(bcml);
        bcml = null;
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

    private void doLocalRegister(String guid, ComponentInfo ci)
            throws RegistryException
    {
        try
        {
            fireRegistered(guid, ci);
        }
        catch (IOException ioe)
        {
            String msg = "failed to broadcast registration guid=" + guid +
                ", component-info=" + ci;
            log.warn(msg, ioe);

            // if the remote broadcast fails then don't attempt the local
            // registration
            // xxx: what to do instead here? if registration fails perhaps the
            // --- component shouldn't be allowed to enter the container? or
            // --- should be removed from the container?
            return;
        }
        localRegistry.register(guid, ci);
    }

    private void doLocalUnregister(String guid)
        throws RegistryException
    {
        // todo: fix this method!

        try
        {
            fireUnregistered(guid);
        }
        catch (IOException ioe)
        {
            String msg = "failed to broadcast unregistration guid=" + guid;
            log.warn(msg, ioe);

            // if the remote broadcast fails then don't do the local
            // unregistration? but this is risky, if the component has left
            // the container it's no longer registered!
        }
        localRegistry.unregister(guid);
    }

	private void doRegisterGlobal(String guid, ComponentInfo ci)
            throws RegistryException
	{
		globalRegistry.register(guid, ci);
	}

	private void doUnregisterGlobal(String guid)
        throws RegistryException
	{
        globalRegistry.unregister(guid);
	}

	private void fireRegistered(String guid, ComponentInfo ci)
            throws IOException
	{
		Notice notice = new Notice();
        // xxx: how does a DomainAgent know its own GUID?
		//notice.setSourceId(this.getGuid());

        // broadcast the registry over the domain topic
        multicastSocket.broadcast(notice, domainInfo.getDomainTopic());
	}

	private void fireUnregistered(String guid) throws IOException
	{
		Notice notice = new Notice();
		//notice.setSourceId(this.getGuid());
		notice.setTopicId(domainInfo.getDomainTopic());

        // broadcast the unregistry over the domain topic
        multicastSocket.broadcast(notice, domainInfo.getDomainTopic());
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
            // todo: implement this method

			Object[] children = bcme.toArray();

			for (int i = 0; i < children.length; ++i)
			{
				if (children[i] instanceof Identifiable)
				{
                    String guid = ((Identifiable) children[i]).getGuid();
					// doLocalRegister(guid, children[i]);
				}
			}
		}

		public void childrenRemoved(BeanContextMembershipEvent bcme)
		{
            // todo: implement this method

			Object[] c = bcme.toArray();

			for (int i = 0; i < c.length; ++i)
			{
                if (c[i] instanceof Identifiable)
				{
					// doLocalUnregister((Identifiable) c[i]);
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
