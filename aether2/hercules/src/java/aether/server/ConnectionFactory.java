package aether.server;

import aether.net.Connection;

import java.io.IOException;

/**
 * ConnectionFactory component provides a basic mechanism for other components
 * in the container to retreive connections.
 *
 * Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface ConnectionFactory
{
    /**
     * Construct a new Connection.
     *
     * @param host host of the server to connect to
     * @param port port of the server to connect to
     * @return Connection object to that server
     * @throws IOException
     *         if the connection can't be opened
     */
    Connection create(String host, int port) throws IOException;
}
