/*
 * ImageFileAnalyzer.java
 *
 * Created on May 26, 2002, 3:41 AM
 */

package psl.chime4.server.vem.util;

import java.awt.Toolkit;
import java.awt.Image;
import java.io.*;

/**
 *	Class for representing a raster image.
 *
 *	@author  Vladislav Shchogolev
 */
public class ImageResourceFile extends ResourceFile
{
	// member variables
	private int mWidth, mHeight;
	
	/** Creates a new instance of ImageFileAnalyzer */
	public ImageResourceFile(String iRelFilename) throws IOException
	{
		super(iRelFilename);
		
		// FIXME: determine dimensions here
		mWidth = 0;
		mHeight = 0;
	}
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
}
