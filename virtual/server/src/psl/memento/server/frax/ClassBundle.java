package psl.memento.server.frax;

import java.io.*;
import java.net.*;
import java.util.*;

import psl.memento.server.frax.util.MiscUtils;

public class ClassBundle implements Serializable {
  private String mLabel;
  private String mClassName;
  private byte[] mClassByteData;
  private String[] mDependenciesNames;
  private byte[][] mDependencyByteData;
  
  private ClassBundle() {
  }
  
  public static ClassBundle getInstance(Class iClass, String iLabel)
      throws FraxException {
    if (iClass == null) {
      return null;
    }
    
    FraxConfiguration config = Frax.getInstance().getConfiguration();
    
    try {   
      ClassBundle c = new ClassBundle();
      
      c.setLabel(iLabel);
      
      String name = iClass.getName();
      c.setClassName(name);

      String classFile = name.replace('.', '/') + ".class";      
      
      ClassLoader cl = MiscUtils.getFraxClassLoader();
      
      c.setClassByteData(getResourceBytes(cl, classFile));
      
      List deps = config.getDependencies(name);
      String[] depNames = new String[deps.size()];
      byte[][] depData = new byte[deps.size()][];
      for (int i = 0, n = deps.size(); i < n; i++) {
        depNames[i] = (String) deps.get(i);
        depData[i] = getResourceBytes(cl, depNames[i]);
      }
      c.setDependenciesNames(depNames);
      c.setDependencyByteData(depData);
      
      return c;
    } catch (IOException ex) {
      throw new FraxException(
        "Error getting byte data for class " + iClass, ex);
    }
  }
  
  private static byte[] getResourceBytes(ClassLoader iCL, String iPath)
      throws IOException {    
    InputStream is = iCL.getResourceAsStream(iPath);
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    int bufferSize = 8192;
    byte[] buffer = new byte[bufferSize];
    int numBytesRead;
    numBytesRead = is.read(buffer);
    while (numBytesRead != -1) {
      os.write(buffer, 0, numBytesRead);      
      numBytesRead = is.read(buffer);
    }

    is.close();

    return os.toByteArray();
  }
  
  public String getLabel() {
    return mLabel;
  }
  
  public void setLabel(String iLabel) {
    mLabel = iLabel;
  }
  
  public String getClassName() {
    return mClassName;
  }
  
  public void setClassName(String iName) {
    mClassName = iName;
  }
  
  public byte[] getClassByteData() {
    return mClassByteData;
  }
  
  public void setClassByteData(byte[] iData) {
    mClassByteData = iData;
  }
  
  public String[] getDependenciesNames() {
    return mDependenciesNames;
  }
  
  public void setDependenciesNames(String[] iNames) {
    mDependenciesNames = iNames;
  }
  
  public byte[][] getDependencyByteData() {
    return mDependencyByteData;
  }
  
  public void setDependencyByteData(byte[][] iData) {
    mDependencyByteData = iData;
  }
}