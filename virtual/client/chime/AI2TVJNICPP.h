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
 *  $Revision: 1.1 $
 *  $Date: 2003-07-31 19:30:12 $
 *  $Source: /local/psl-cvs/psl/memento/virtual/client/chime/AI2TVJNICPP.h,v $
 */

#if !defined(_AI2TVJNICPP_H_)
#define _AI2TVJNICPP_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "jni_md.h"
#include "jni.h"
#include "psl_ai2tv_client_AI2TVJNIJava.h"

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
  
  // the following functions are called by the CPP side to execute
  // Java side methods
  void playPressed();
  void stopPressed();
  void pausePressed();
  void gotoPressed(int time);

  long currentTime();
  int videoLength();

  void setCacheDir(char* dir);
  char* getCacheDir();
  void setBaseURL(char* url);
  char* getBaseURL();

  void setLoginInfo(const char* info);
  void loadVideo(char* name, char* date);
  void initialize();
  int getAvailableVideos(char videoList[10][50]);

  void shutdown();

  // These functions are the CPP functions available to the Java side.  
  // I display these here for information purposes only.
  // Java_psl_ai2tv_client_AI2TVJNIJava_loadFrame(JNIEnv *env, jobject obj)
  // Java_psl_ai2tv_client_AI2TVJNIJava_displayFrame(JNIEnv *env, jobject obj)

 private:
  const char* JAVACLASS;
  char *classpath, *libpath,*baseURL, *sienaServer;
  JavaVM* _jvm;
  JNIEnv *_env;
  jobject _obj;   // this should really be a pointer 
  jclass _class;  // this should really be a pointer 
  JNIEnv* create_vm(JavaVM* jvm);
  void instantiateClasses();
  const static int NUM_VIDEOS = 3;
  const static int VIDEO_NAME_LENGTH = 15;
};

#endif // !defined(_AI2TVJNICPP_H_)
