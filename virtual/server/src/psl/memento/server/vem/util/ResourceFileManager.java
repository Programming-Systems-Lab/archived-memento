/*
 * ResourceFileManager.java
 *
 * Created on May 26, 2002, 3:39 PM
 */

package psl.memento.server.vem.util;

import java.util.*;
import java.util.prefs.Preferences;
import java.io.*;

/**
 * Class for managing resource files such as 3DS files and images.
 *
 * @author  Vladislav Shchogolev
 * @version $Revision: 1.3 $
 */
public class ResourceFileManager
{
	// singleton object
	private static final ResourceFileManager kSingleton =
	new ResourceFileManager();
	
	// for determining base directory
	private static final Preferences kPrefs =
	Preferences.userNodeForPackage(ResourceFileManager.class);
	
	// key for configuration file
	private static final String kPropBaseDir = "vem.config.basedir";
	
	// key for preferences
	private static final String kKeyBaseDir = "ResourceBaseDir";
	
	// default value (system property)
	private static final String kBaseDirSystemProp = "user.dir";
	
	// subfolders of base
	public static final String kResourceDir = "resources";
	public static final String kThemeDir = "themes";
	
	// instance variables
	private String mBaseDir;
	private Hashtable mResourceFiles;
	
	/**
	 *	Access modifier is package to prevent external instantiation.
	 */
	ResourceFileManager()
	{
		mResourceFiles = new Hashtable();
		mBaseDir = null;
	}
	
	public String getBaseDir()
	{
		return mBaseDir;
	}
	
	private void setBaseDir(String iBaseDir)
	{
		mBaseDir = iBaseDir;
		
		try
		{
			mResourceFiles.clear();
			readResourceFiles();
		} catch (IOException ioe)
		{
			System.out.println("IOE: " + ioe.getMessage());
		}
	}
	
	private void readResourceFiles() throws IOException
	{
		// get File for resource directory
		File resourceDir = new File(getBaseDir() + File.separator + kResourceDir);
		
		//System.out.println(resourceDir.getAbsolutePath());
		
		File[] contents = resourceDir.listFiles();
		String relativePath;
		
		if (contents == null)
			return;
		
		// FIXME: need to recurse through subdirectories too
		for (int i = 0; i < contents.length; i++)
		{
			relativePath = contents[i].getName();
			addResourceFile(relativePath);
		}
	}
	
	private void addResourceFile(String relativePath) throws IOException
	{
		ResourceFile rf ;
		String relPathLC = relativePath.toLowerCase();
		String hashKey;
		
		// determine proper container
		if (relPathLC.endsWith(".3ds"))
			rf = new ModelResourceFile(relativePath);
		
		else if (relPathLC.endsWith(".gif") || relPathLC.endsWith(".jpg"))
			rf = new ImageResourceFile(relativePath);
		
		else
			rf = new ResourceFile(relativePath);
		
		// add to hashtable
		hashKey = getHashKey(rf.getResourceID());
		mResourceFiles.put(hashKey, rf);
	}
	
	/**
	 *	Given a resourceID, returns the associated ResourceFile.
	 */
	public ResourceFile getResourceFile(int iResourceID)
	{
		return (ResourceFile)
		mResourceFiles.get(getHashKey(iResourceID));
	}
	
	/**
	 *	Method to internally transform a Resource ID into a hash key.
	 *	This transformation must be injective.
	 */
	private String getHashKey(int iResourceID)
	{
		return Integer.toString(iResourceID);
	}
	
	public int getKeyForChecksum(long checksum)
	{
		return (int) checksum;
	}
	
	public int getKeyForChecksum(String checksum)
	{
		return Integer.parseInt(checksum);
	}
	
	/**
	 *	Returns the singleton instance of this class.
	 *
	 *	@param	iBaseDir	sets the base directory to the given string
	 *	@param	iUpdatePrefs
	 *					if true, updates the preferences backing
	 *					store to hold the new base directory value.
	 */
	public static ResourceFileManager getInstance(String iBaseDir, boolean iUpdatePrefs)
	{
		if (iBaseDir == null)
		{
			return getInstance();
		} else
		{
			kSingleton.setBaseDir(iBaseDir);
			if (iUpdatePrefs) kPrefs.put(kKeyBaseDir, iBaseDir);
			return kSingleton;
		}
	}
	
	/**
	 *	Creates a new instance of ResourceFileManager.
	 *	The base directory for file storage is assumed to be
	 *	<code>System.getProperty(kBaseDirSystemProp)</code>.
	 *	The value is stored as a <code>Preference</code> and can be modified
	 *	in the backing store after the initial run.
	 */
	public static ResourceFileManager getInstance()
	{
		if (kSingleton.getBaseDir() == null)
		{
			String defaultBaseDir = System.getProperty(kBaseDirSystemProp);
			kSingleton.setBaseDir(kPrefs.get(kKeyBaseDir, defaultBaseDir));
		}
		return kSingleton;
	}
	
	public static ResourceFileManager getInstance(java.util.Map properties)
	{
		return getInstance((String)properties.get(kPropBaseDir), true);
	}
	
	public void dumpFiles()
	{
		ResourceFile rf;
		ModelResourceFile mrf;
		Object key;
		
		System.out.println("Name     \tChecksum\tBounding Volume");
		
		Enumeration enum = mResourceFiles.keys();
		while (enum.hasMoreElements())
		{
			key = enum.nextElement();
			rf = (ResourceFile) mResourceFiles.get(key);
			System.out.print(rf.getRelativePath() + "\t" + rf.getChecksum());
			if (rf instanceof ModelResourceFile)
			{
				mrf = (ModelResourceFile) rf;
				System.out.print("\t" + mrf.getBoundingVolume());
			}
			System.out.println();
		}
	}
}
