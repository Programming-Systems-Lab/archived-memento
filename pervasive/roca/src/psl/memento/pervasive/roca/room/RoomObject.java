package psl.memento.pervasive.roca.room;

import psl.memento.pervasive.roca.data.*;

/** 
 * Describes a 3D object that may appear in a room.
 *
 * @author Kristina Holst
 */
public class RoomObject {

	private String mType;
	private SizeData mSize;
  private CartesianCoord mPosition;
	private RotationData mRotation;

  /** Creates a new instance of RoomObject.
   * @param iType name of the object, i.e. Monitor or Chair
   * @param iSize size of the object
   * @param iPosition position within the room of the object's origin (potentially different
   * for each object)
   * @param iRotation object's rotation information
   */
	public RoomObject(String iType, SizeData iSize, CartesianCoord iPosition, RotationData iRotation) {
		mType = iType;
    mSize = iSize;
    mPosition = iPosition;
    mRotation = iRotation;
	}

	public void setType(String iType) {
		mType = iType;
	}

	public String getType() {
		return mType;
	}

	public void setSize(SizeData iSize) {
		mSize = iSize;
	}

	public SizeData getSize() {
		return mSize;
	}
  
  private void setPosition(CartesianCoord iPosition) {
		mPosition = iPosition;
	}

	public CartesianCoord getPosition() {
		return mPosition;
	}

	private void setRotation(RotationData iRotation) {
		mRotation = iRotation;
	}

	public RotationData getRotation() {
		return mRotation;
	}

	public String toString() {
		return mType;
	}
}