/*******************************************************************
    ChimeAi2tvInterface.h
	Author: Mark Galagan @ 2003

	Collection of interfaces for communicating with
	AI2TV classes
********************************************************************/

#ifndef __ChimeAi2tvInterface_H__
#define __ChimeAi2tvInterface_H__

#include "AI2TVJNICPP.h"

class ChimeAi2tvInterface
{
private:

	AI2TVJNICPP *client;
	ChimeAi2tvInterface ();
	
public:

	static ChimeAi2tvInterface* GetInstance (const char *strUserName);

	int GetAvailableVideos (char videos[10][50]);
	void SetLoginInfo (const char *strUserName);
	void SetCacheDir (const char *cacheDir);
	void SelectVideo (char* strVideoSource, char* strVideoDate);

	// void LoadFrame (const char *strFileName, const char *strFrameName);
	// void DisplayFrame (const char *strFrameName);

	void ShutDown ();

	void PlayPressed();
	void StopPressed();
	void PausePressed();
	void GotoPressed(int time);
	int isActive();
};

#endif //__ChimeAi2tvInterface_H__
