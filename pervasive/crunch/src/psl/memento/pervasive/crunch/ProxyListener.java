package psl.memento.pervasive.crunch;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Peter Grimm
 */
public class ProxyListener extends Thread {
	public static final int DEFAULT_PORT = 8080;
	public static final int ACCEPT_TIMEOUT = 50;

	private boolean loop;
	private int port;
	private ServerSocket serverSocket;
	private LinkedList filters;

	private LinkedList recycledSockets = new LinkedList();

	public int getPort() {
		return port;
	}

	/**
	 * @param listen_port
	 *            the port the proxy should listen on.
	 */
	public ProxyListener(LinkedList ll, int listen_port) {
		super();

		loop = true;

		filters = ll;

		port = listen_port;
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
			System.out.println("Listening on port " + listen_port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Creates a new ProxyListener Listening on the default port of 8080
	 */
	public ProxyListener(LinkedList ll) {
		this(ll, DEFAULT_PORT);
	}

	public void recycle(Socket s) {
		synchronized (recycledSockets) {
			recycledSockets.add(s);
		}
	}

	/**
	 * Listens for connections and spawns worker threads for these connections
	 */
	public void run() {
		//loop until explicitly told to shutdown
		while (loop) {
			try {
				while (loop) { //run until an exception is thrown
					//get recycled sockets (persistent connections)
					synchronized (recycledSockets) {
						while (recycledSockets.size() > 0) {
							Socket socket =
								(Socket) recycledSockets.removeFirst();
							new ProxyThread(
								this,
								socket,
								filters.listIterator(0))
								.run();
						}
					}
					//get a socket from the serversocket
					try {
						Socket socket = serverSocket.accept();
						new ProxyThread(this, socket, filters.listIterator(0))
							.run();
					} catch (java.net.SocketTimeoutException ste) {
					}
				}
			} catch (NullPointerException npe) {
			} catch (Exception e) { //other error
				e.printStackTrace();
			}
		}
		try {
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the listening socket and halts the thread. Works by closing the
	 * server socket which causes the accept method to except, breaking the
	 * while loop.
	 */
	public void halt() {
		loop = false;
		try {
			serverSocket.close();
		} catch (IOException ioe) {

		}
	}
}
