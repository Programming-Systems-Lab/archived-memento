package psl.memento.pervasive.roca.room;

import psl.memento.pervasive.roca.data.*;

/**
 * Describes a 3D object that may appear in a room.
 *
 * @author Kristina Holst
 */
public class RoomObject {
    
    private String mCategory;
    private String mType;
    private SizeData mSize, mPlacingSize;
    private CartesianCoord mPosition;
    private RotationData mRotation;
    
    /** Creates a new instance of RoomObject.
     * @param iCategory category of objects (i.e. pervasive or physical)
     * @param iType name of the object (i.e. monitor or chair)
     * @param iSize size of the object
     * @param iPosition position within the room of the object's origin (potentially different
     * for each object)
     * @param iRotation object's rotation information
     */
    public RoomObject(String iCategory, String iType, SizeData iSize, CartesianCoord iPosition, RotationData iRotation) {
        mCategory = iCategory;
        mType = iType;
        mSize = iSize;
        mPlacingSize = iSize;
        mPosition = iPosition;
        mRotation = iRotation;
    }
    
    /** Creates a new instance of RoomObject.
     * @param iCategory category of objects (i.e. pervasive or physical)
     * @param iType name of the object (i.e. monitor or chair)
     * @param iSize size of the object
     * @param iRotation object's rotation information
     */
    public RoomObject(String iCategory, String iType, SizeData iSize, RotationData iRotation) {
        mCategory = iCategory;
        mType = iType;
        mSize = iSize;
        mPlacingSize = iSize;
        mRotation = iRotation;
    }
    
    public void setCategory(String iCategory) {
        mCategory = iCategory;
    }
    
    public String getCategory() {
        return mCategory;
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
    
    public void setPlacingSize(SizeData iPlacingSize) {
        mPlacingSize = iPlacingSize;
    }
    
    public SizeData getPlacingSize() {
        return mPlacingSize;
    }
    
    public void setPosition(CartesianCoord iPosition) {
        mPosition = iPosition;
    }
    
    public CartesianCoord getPosition() {
        return mPosition;
    }
    
    public void setRotation(RotationData iRotation) {
        mRotation = iRotation;
    }
    
    public RotationData getRotation() {
        return mRotation;
    }
    
    public String toString() {
        return mType;
    }
}