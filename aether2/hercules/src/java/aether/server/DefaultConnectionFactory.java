package aether.server;

import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.Publisher;

import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServiceProvider;
import java.util.*;
import java.io.IOException;

import org.apache.log4j.Logger;
import net.concedere.dundee.AbstractProvider;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.framework.Startable;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;

/**
 * This component provides Connection objects to other components in the
 * container.
 *
 * TODO: in the future this class should cache Connection objects so multiple
 * --- connections to the same server can be reused
 * TODO: in the future this class should proxy Connection objects so that
 * --- if one component closes a shared component it only decreases a refcount
 * --- instead of closing the connection
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultConnectionFactory implements ConnectionFactory
{
    private static Logger log = Logger.getLogger(DefaultConnectionFactory.class);

    public Connection create(String host, int port) throws IOException
    {
        if (host == null)
        {
            String msg = "host can't be null";
            throw new IllegalArgumentException(msg);
        }

        Connection conn = new DefaultConnection(host, port);
        conn.open();
        return conn;
    }
}
