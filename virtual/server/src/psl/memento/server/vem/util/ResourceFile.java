/*
 * ResourceFile.java
 *
 * Created on May 26, 2002, 4:37 PM
 */

package psl.memento.server.vem.util;

import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

/**
 *	Stores meta data about a resource file (either a model or image).
 *
 *	@author  Vladislav Shchogolev
 *	@version $Revision: 1.5 $
 */
public class ResourceFile {

	private String relativePath;
	private long checksum;

	/**
	 *	Creates a new instance of ResourceFile
	 *
	 *	@param	iRelativePath	the relative path name of the file
	 */
	public ResourceFile(String iRelativePath) throws IOException {
		setRelativePath(iRelativePath);
	}

	/** Getter for property relativePath.
	 * @return Value of property relativePath.
	 */
	public String getRelativePath() {
		return this.relativePath;
	}

	/**
	 *	Setter for property relativePath.
	 *	Automatically updates the checksum property.
	 *
	 *	@param relativePath New value of property relativePath.
	 */
	public void setRelativePath(String relativePath) throws IOException {
		this.relativePath = relativePath;
		this.checksum = computeChecksum();
	}

	/** Getter for property checksum.
	 * @return Value of property checksum.
	 */
	public long getChecksum() {
		return this.checksum;
	}

	private long computeChecksum() throws IOException {
		Checksum checksum = new Adler32();
		FileInputStream fis = new FileInputStream(getFullPath());
		byte[] array = new byte[1024];
		int count = array.length;

		while (count == array.length) {
			count = fis.read(array);
			checksum.update(array, 0, count);
		}

		if (count != -1)
			checksum.update(array, 0, count);

		fis.close();

		return checksum.getValue();
	}

	/**
	 *	Getter for property fullPath.
	 *	Calls on the ResourceFileManager to return the base directory.
	 *
	 *	@return Value of property fullPath.
	 */
	public String getFullPath() {
		String base = ResourceFileManager.getInstance().getBaseDir();
		return base
			+ File.separator
			+ ResourceFileManager.kResourceDir
			+ File.separator
			+ getRelativePath();
	}

	/**
	 *	Returns the abstract file object for this resource.
	 *	Calls the getFullPath() method to get the path name.
	 */
	public File getFile() {
		return new File(getFullPath());
	}

	/**
	 *	Getter for property resourceID.
	 *	This is unique numeric identifier for resource. It should be unique
	 *	across all servers and clients.
	 *
	 *	This implementation simply uses the checksum of the file, but this can
	 *	be changed to suit future needs.
	 *
	 * @return Value of property resourceID.
	 */
	public int getResourceID() {
		return (int) this.checksum;
	}

}
