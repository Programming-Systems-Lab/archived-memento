/*
 * @(#)AI2TVJNICPP.h
 *
 * Copyright (c) 2001: The Trustees of Columbia University in the City of New York.  All Rights Reserved
 *
 * Copyright (c) 2001: @author Dan Phung
 * Last modified by: Dan Phung (dp2041@cs.columbia.edu)
 *
 * CVS version control block - do not edit manually
 *  $RCSfile: AI2TVJNICPP.h,v $
 *  $Revision: 1.3 $
 *  $Date: 2003-09-02 17:57:49 $
 *  $Source: /local/psl-cvs/psl/memento/virtual/client/chime/AI2TVJNICPP.h,v $
 */

#if !defined(_AI2TVJNICPP_H_)
#define _AI2TVJNICPP_H_

// #include "cssysdef.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "jni_md.h"
#include "jni.h"
#include "psl_ai2tv_client_AI2TVJNIJava.h"
// #include "ChimeSystemDriver.h"

/**
 * The CPP side JNI interface for the AI2TV client.
 *
 * @version	$$
 * @author	Dan Phung (dp2041@cs.columbia.edu)
 */
class AI2TVJNICPP
{
  int doDEBUG;

 public:
  AI2TVJNICPP();
  ~AI2TVJNICPP();
  
  int isActive();

  // the following functions are called by the CPP side to execute
  // Java side methods
  void playPressed();
  void stopPressed();
  void pausePressed();
  void gotoPressed(int time);

  long currentTime();
  int videoLength();

  void setCacheDir(const char* dir);
  char* getCacheDir();
  void setBaseURL(char* url);
  char* getBaseURL();

  void setLoginInfo(const char* login, const char* passwd, const char* server,
		    const char* uid, const char* gid);
  void loadVideo(char* name, char* date);
  void initialize();
  int getAvailableVideos(char videoList[10][50]);

  void shutdown();
  
  // These functions are the CPP functions available to the Java side.  
  // I display these here for information purposes only.
  // void Java_psl_ai2tv_client_AI2TVJNIJava_loadImage(JNIEnv *env, jobject obj)
  // jboolean Java_psl_ai2tv_client_AI2TVJNIJava_displayImage(JNIEnv *env, jobject obj)

 private:
  const char* JAVACLASS;
  char *classpath, *libpath,*baseURL, *sienaServer;
  JavaVM* _jvm;
  JNIEnv *_env;
  jobject _obj;   // this should really be a pointer 
  jclass _class;  // this should really be a pointer 
  JNIEnv* create_vm(JavaVM* jvm);
  void instantiateClasses();
  const static int NUM_VIDEOS = 10;
  const static int VIDEO_NAME_LENGTH = 50;
  int _isActive;
};

#endif // !defined(_AI2TVJNICPP_H_)
