package aether.net;

import aether.event.Response;
import aether.event.Request;
import aether.event.EventHandler;

import java.io.IOException;

/**
 * Represents the destination of a socket to which clients may connect.
 *
 * Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface ServerSocket
{
    /**
     * Get the URI that represents this destination.
     *
     * @return URI that represents this destination
     */
    public String getDestination();

    /**
     * Get the ID of the component generating the responses.
     *
     * @return ID of the component generating the responses
     */
    public String getSourceId();

    /**
     * Set the EventHandler that will be notified of incoming requests.
     *
     * @param handler handler to be notified of incoming requests
     */
    public void setEventHandler(EventHandler handler);

    /**
     * Get the EventHandler that's notified of incoming requests.
     *
     * @return EventHandler notified of incoming requests
     */
    public EventHandler getEventHandler();

    /**
     * Bind the link destination to being receiving requests.
     *
     * @throws IOException
     *         if something goes wrong
     */
    public void bind() throws IOException;

    /**
     * Unbind the link destination.
     *
     * @throws IOException
     *         if something goes wrong
     */
    public void unbind() throws IOException;

    /**
     * Send a response to an incoming Request.
     *
     * @param request  Request that arrived over the link
     * @param response Response to the request
     * @throws IOException
     *         if the response send fails
     */
    public void sendResponse(Request request, Response response)
            throws IOException;
}
