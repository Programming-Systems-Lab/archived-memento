package psl.memento.pervasive.roca.data;

/**
 * Holds width, length, and height values.
 *
 * @author Kristina Holst
 */
public class SizeData {

	private double mWidth, mLength, mHeight;

  /** Creates a new instance of SizeData
   * @param iWidth width value
   * @param iLength length value
   * @param iHeight height value
   */
	public SizeData(double iWidth, double iLength, double iHeight) {
		mWidth = iWidth;
    mLength = iLength;
		mHeight = iHeight;
	}

  public void setWidth(double iWidth) {
   mWidth = iWidth; 
  }
  
	public double getWidth() {
		return mWidth;
	}
  
  public void setLength(double iLength) {
   mLength = iLength; 
  }

	public double getLength() {
		return mLength;
	}
  
  public void setHeight(double iHeight) {
   mHeight = iHeight; 
  }

	public double getHeight() {
		return mHeight;
	}
}