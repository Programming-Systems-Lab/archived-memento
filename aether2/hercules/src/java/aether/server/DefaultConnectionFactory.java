package aether.server;

import aether.net.Connection;
import aether.net.DefaultConnection;
import org.apache.log4j.Logger;

import java.io.IOException;

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
 * @author Buko O. (aso22@columbia.edu)
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
