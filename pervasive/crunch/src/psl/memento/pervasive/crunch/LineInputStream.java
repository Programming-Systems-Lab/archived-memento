package psl.memento.pervasive.crunch;

import java.io.InputStream;
import java.io.IOException;
/**
 * An extension of an inputstream to add readline functionality for the purpose
 * of reading http header lines.
 * 
 * @author Peter Grimm (pmg23@cs.columbia.edu)
 */
public class LineInputStream extends InputStream {
	public static final long READ_TIMEOUT = 5000;
	public static final int SLEEP_TIME = 100;
	public static final int BUFFER_SIZE = 16384;
	private byte[] lineBuffer = new byte[BUFFER_SIZE];
	private InputStream inputStream;
	private int lineLength;

	/**
	 * Creates a new LineInputStream that gets its data from the given
	 * InputStream.
	 * 
	 * @param is
	 *            where to read lines from
	 */
	public LineInputStream(InputStream is) {
		inputStream = is;
	}

	/**
	 * Reads one line. Expects lines from the input stream to be terminated by
	 * a CRLF. Lines are returned without the trailing CRLF. Will throw an
	 * IOException for lines that exceed BUFFER_SIZE, if a timeout occurs while
	 * waiting for data, or if there are problems reading the data.
	 * 
	 * @return one line
	 */
	public String readLine() throws IOException, ReadTimeoutException {
		try {
			waitForAvailable();
			lineBuffer[0] = (byte) inputStream.read();
			waitForAvailable();
			lineBuffer[1] = (byte) inputStream.read();
			lineLength = 2;
			while (lineBuffer[lineLength - 2] != (byte) 13
				|| lineBuffer[lineLength - 1] != (byte) 10) {
				waitForAvailable();
				lineBuffer[lineLength] = (byte) inputStream.read();
				lineLength++;
			}
			return new String(lineBuffer, 0, lineLength - 2);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			throw new IOException("Line too long.");
		}
	}

	/**
	 * Waits for the input stream to become available. If this does not occur
	 * after a while, it times out and throws an IOException
	 */
	private void waitForAvailable() throws IOException, ReadTimeoutException {
		long expireTime = System.currentTimeMillis() + READ_TIMEOUT;
		while (inputStream.available() < 1) {
			if (System.currentTimeMillis() > expireTime) {
				throw new ReadTimeoutException("Read Timed Out.");
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException ie) {
			}
		}
	}

	public int available() throws IOException {
		return inputStream.available();
	}
	public void close() throws IOException {
		inputStream.close();
	}
	public void mark(int readlimit) {
		inputStream.mark(readlimit);
	}
	public boolean markSupported() {
		return inputStream.markSupported();
	}
	public int read() throws IOException {
		return inputStream.read();
	}
	public int read(byte[] b) throws IOException {
		return inputStream.read(b);
	}
	public int read(byte[] b, int off, int len) throws IOException {
		return inputStream.read(b, off, len);
	}
	public void reset() throws IOException {
		inputStream.reset();
	}
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}
}
