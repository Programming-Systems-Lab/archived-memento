package psl.memento.pervasive.crunch;

import java.net.Socket;
import java.util.Iterator;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
public class ProxyThread extends Thread {
	Socket socket;
	Socket sSocket;
	Iterator filters;
	ProxyListener listener;
	public ProxyThread(ProxyListener pl, Socket s, Iterator pf) {
		super();
		listener = pl;
		socket = s;
		filters = pf;
	}

	public void run() {
		try {
			//send request to server
			HttpStream clientStream = new HttpStream(socket);
			InputStream fromServer = sendServerRequest(clientStream);

			//get response from server
			HttpStream serverStream = new HttpStream(sSocket);

			//filter if text/html
			String type = serverStream.getAttribute("Content-Type");
			System.out.println("Detected content type = " + type);
			if (type != null && -1 < type.toLowerCase().indexOf("text/html")) {
				filter(serverStream);
			}

			//send back to client
			System.out.println("Getting client output stream...");
			OutputStream outputStream = socket.getOutputStream();
			System.out.println("Sending header...");
			serverStream.sendHeaderToStream(outputStream);
			System.out.println("Sending content...");
			serverStream.sendContentToStream(outputStream);
			System.out.println("Done.");

			//close the server socket
			sSocket.close();
			socket.close();
			//check to see if the client socket should be recycled
			/*
			 * String keepalive =
			 * clientStream.getAttribute("proxy-connection");
			 * if(keepalive!=null &&
			 * keepalive.toLowerCase().equals("keep-alive")){
			 * listener.recycle(socket); }
			 */

		} catch (ReadTimeoutException rte) {
			try {
				socket.close();
			} catch (Exception ex) {
			}
			try {
				sSocket.close();
			} catch (Exception ex) {
			}
			System.out.println("Transfer Timeout Occurred.");
		} catch (Exception e) {
			try {
				socket.close();
			} catch (Exception ex) {
			}
			try {
				sSocket.close();
			} catch (Exception ex) {
			}
			e.printStackTrace();
		}
	}

	public InputStream sendServerRequest(HttpStream http) throws Exception {
		String host = http.getAttribute("Host");
		sSocket = new Socket(host, 80);
		OutputStream out = sSocket.getOutputStream();
		http.sendHeaderToStream(out);
		//http.sendContentToStream(out);
		return sSocket.getInputStream();
	}

	public void filter(HttpStream http) throws IOException {
		File workingFile = null;
		workingFile = http.downloadToFile();
		while (filters.hasNext()) {
			try {
				ProxyFilter filter = (ProxyFilter) (filters.next());
				System.out.println("Started filtering...");
				workingFile.deleteOnExit();
				workingFile = filter.process(workingFile);
				workingFile.deleteOnExit();
				http.setAttribute("content-type", filter.getContentType());
				System.out.println("Done filtering.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		http.replaceContentWithFile(workingFile);
		System.out.println("content replaced");
	}
}
