package psl.memento.pervasive.crunch;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Enumeration;

public class HttpStream {
	public static final String TEMP_FILE_SUFFIX = ".temp";
	public static final String TEMP_FILE_PREFIX = "proxy.";
	public static final String TEMP_FILE_DIR = "." + File.separator + "cache";

	public static final long TIME_OUT = 5000;
	public static final int SLEEP_TIME = 50;
	public static final int BUFFER_SIZE = 4096;
	public static final byte[] NEWLINE = "\r\n".getBytes();
	public static final byte[] SEPARATOR = ": ".getBytes();

	private String firstLine;
	private Hashtable attributes;
	private BufferedInputStream inStream;
	private Socket socket;
	private boolean hasContent;

	public boolean hasContent() {
		return hasContent;
	}

	public HttpStream(Socket s) throws IOException, ReadTimeoutException {
		socket = s;
		inStream = new BufferedInputStream(socket.getInputStream());
		attributes = new Hashtable();
		readHeader();
	}

	public HttpStream(String first_line, Hashtable attribs, File data) {
		firstLine = first_line;
		attributes = attribs;
		try {
			inStream = new BufferedInputStream(new FileInputStream(data));
		} catch (Exception e) {
			System.out.println("Error creating stream...");
			e.printStackTrace();
		}
	}

	private void readHeader() throws ReadTimeoutException {
		LineInputStream lines = new LineInputStream(inStream);
		try {
			hasContent = false;
			firstLine = lines.readLine();
			System.out.println("\t" + firstLine);

			String input = lines.readLine();
			int index = input.indexOf(":");

			while (index > -1) {
				System.out.println("\t" + input);
				String key = input.substring(0, index).toLowerCase().trim();
				String value =
					input.substring(index + 1, input.length()).trim();
				attributes.put(key, value);

				input = lines.readLine();

				index = input.indexOf(":");
			}
			hasContent = true;
		} catch (ReadTimeoutException rte) {
			throw new ReadTimeoutException("Timeout while reading http header.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	public String getAttribute(String attributeKey) {
		return (String) (attributes.get(attributeKey.toLowerCase()));
	}

	public void setAttribute(String attributeKey, String value) {
		attributes.put(attributeKey, value);
	}

	public File downloadToFile() throws IOException {
		File tempFile = null;
		try {
			tempFile =
				File.createTempFile(
					TEMP_FILE_PREFIX,
					TEMP_FILE_SUFFIX,
					new File(TEMP_FILE_DIR));
			sendContentToStream(
				new BufferedOutputStream(new FileOutputStream(tempFile)));
		} catch (ReadTimeoutException rte) {
			System.out.println("File Download Timed Out.");
		} catch (IOException e) {
			try {
				tempFile.delete();
			} catch (Exception ex) {
			}
			throw e;
		}
		return tempFile;
	}

	/**
	 * Replaces the contents of an http message with the contents of a file.
	 */
	public void replaceContentWithFile(File f) {
		try {
			//change the instream to a FileInputStream
			inStream = new BufferedInputStream(new FileInputStream(f));

			//fix the size of the stream
			attributes.remove("content-length");
			attributes.put("content-length", String.valueOf(f.length()));
			hasContent = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the header of an http message to a stream (no content)
	 */
	public void sendHeaderToStream(OutputStream outStream) throws IOException {
		try {
			//write the first line
			outStream.write(firstLine.getBytes());
			outStream.write(NEWLINE);

			//write the rest of the header
			Enumeration keys = attributes.keys();
			while (keys.hasMoreElements()) {
				String currentKey = (String) (keys.nextElement());
				String currentValue = (String) (attributes.get(currentKey));
				outStream.write(currentKey.getBytes());
				outStream.write(SEPARATOR);
				outStream.write(currentValue.getBytes());
				outStream.write(NEWLINE);
			}
			outStream.write(NEWLINE);
			outStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the contents (no header) of the http message to a stream
	 * 
	 * @param outStream
	 *            the stream to write the content portion of the http message
	 *            to
	 */
	public void sendContentToStream(OutputStream outStream)
		throws IOException, ReadTimeoutException {
		System.out.println("http::sendingContentToStream...");
		long length = getContentLength();
		if (length > -1) {
			sendKnownContentToStream(outStream, length);
		} else {
			sendUnknownContentToStream(outStream);
		}
		System.out.println("http::done");
	}

	private void sendKnownContentToStream(OutputStream outStream, long length)
		throws IOException, ReadTimeoutException {
		byte[] dataBuffer = new byte[BUFFER_SIZE];
		long lastWriteTime = System.currentTimeMillis();
		while (length > 0) {
			//find the available bytes
			int available = inStream.available();
			if (available > BUFFER_SIZE)
				available = BUFFER_SIZE;

			//transfer the bytes
			if (available > 0) {
				available = inStream.read(dataBuffer, 0, available);
				outStream.write(dataBuffer, 0, available);
				length -= (long) available;
				lastWriteTime = System.currentTimeMillis();
			}
			//wait for more bytes, and time out if necessary
			else {
				if (System.currentTimeMillis() > lastWriteTime + TIME_OUT) {
					try {
						outStream.flush();
					} catch (Exception e) {
					}
					throw new ReadTimeoutException("Transfer Timed Out.");
				}
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	/**
	 * Unknown content always comes from sockets because we know the size of
	 * file streams
	 */
	private void sendUnknownContentToStream(OutputStream outStream)
		throws IOException, ReadTimeoutException {
		byte[] dataBuffer = new byte[BUFFER_SIZE];
		long lastWriteTime = System.currentTimeMillis();
		while (!socket.isInputShutdown()) {
			//find available bytes
			int available = inStream.available();
			if (available > BUFFER_SIZE)
				available = BUFFER_SIZE;

			//transfer the bytes
			if (available > 0) {
				available = inStream.read(dataBuffer, 0, available);
				outStream.write(dataBuffer, 0, available);
				lastWriteTime = System.currentTimeMillis();
			}
			//wait for more bytes, and time out if necessary
			else {
				if (System.currentTimeMillis() > lastWriteTime + TIME_OUT) {
					try {
						outStream.flush();
					} catch (Exception e) {
					}
					throw new ReadTimeoutException("Transfer Timed Out.");
				}
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	/**
	 * returns the content length or -1 if there was an error
	 */
	private long getContentLength() {
		try {
			return Long.parseLong((String) attributes.get("content-length"));
		} catch (Exception e) {
			return -1;
		}
	}
}
