package psl.memento.pervasive.roca.data;

/**
 * Holds yaw (turning side to side), roll (rolling left/right),
 * and pitch (tilting up or down) values.
 *
 * @author Kristina Holst
 */
public class RotationData {
  
  private int mYaw, mRoll, mPitch;
  
  /* Directional constants */
  public static final int N = 0, NE = 45, E = 90, SE = 135, S = 180, SW = 225, W = 270, NW = 315;
  
  /** Creates a new instance of RotationData
   * @param iYaw yaw value
   * @param iRoll roll value
   * @param iPitch pitch value
   */
  public RotationData(int iYaw, int iRoll, int iPitch) {
    mYaw = iYaw;
    mRoll = iRoll;
    mPitch = iPitch;
  }
  
  public void setYaw(int iYaw) {
    mYaw = iYaw;
  }
  
  public int getYaw() {
    return mYaw;
  }
  
  public void setRoll(int iRoll) {
    mRoll = iRoll;
  }
  
  public int getRoll() {
    return mRoll;
  }
  
  public void setPitch(int iPitch) {
    mPitch = iPitch;
  }
  
  public int getPitch() {
    return mPitch;
  }
  
  public String toString() {
    return "yaw: " + mYaw + ", roll: " + mRoll + ", pitch: " + mPitch;
  }
}