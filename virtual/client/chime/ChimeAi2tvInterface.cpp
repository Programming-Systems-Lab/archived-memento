/*******************************************************************
    ChimeAi2tvInterface.cpp
	Author: Mark Galagan @ 2003

	Implementation of the interface between CHIME and AI2TV
	clients.
********************************************************************/


#include "cssysdef.h"
#include "ChimeAi2tvInterface.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

/**********************************************************
 * Constructor simply creates an instance of AI2TV client
 **********************************************************/
ChimeAi2tvInterface::ChimeAi2tvInterface ()
{
	client = new AI2TVJNICPP ();
	if (client == NULL){
		printf("Error, AI2TV Client could not be created\n");
		return;
	}
}

/**********************************************************
 * Creates an instance of the interface and sets
 * necessary parameters
 **********************************************************/
ChimeAi2tvInterface* ChimeAi2tvInterface::GetInstance (const char *strUserName)
{
	ChimeAi2tvInterface* cai = new ChimeAi2tvInterface ();
 	cai->SetLoginInfo (strUserName);
	return cai;
}

/**********************************************************
 * set the base directory that will contain the image frames
 **********************************************************/
void ChimeAi2tvInterface::SetCacheDir (const char *cacheDir)
{
  if (client) client->setCacheDir (cacheDir);
}

/**********************************************************
 * Get the list of available videos from the client
 **********************************************************/
int ChimeAi2tvInterface::GetAvailableVideos (char videos[10][50])
{
  // temp array fill
  // for (int i = 0; i < 5; i++)
  // sprintf (videos[i], "Source %d", i+1);
  if (client) 
    return client->getAvailableVideos (videos);
  else 
    return 0;
}

/**********************************************************
 * Set login parameters
 **********************************************************/
void ChimeAi2tvInterface::SetLoginInfo (const char *strUserName)
{
	printf("ChimeAi2tvInterface: Login info: %s\n", strUserName);
	
	const char* passwd = "iLuvMinnie";
	const char* server = "disneyworld";
	const char* uid = "goofy";
	const char* gid = "disney";

	if (client) client->setLoginInfo (strUserName, passwd, server, uid, gid);
}

/**********************************************************
 * Tell client that a video was selected and the
 * the date was set so that client could load it.
 **********************************************************/
void ChimeAi2tvInterface::SelectVideo (char *strVideoSource, char *strVideoDate)
{
	printf("Selected video: %s @ %s\n", strVideoSource, strVideoDate);
	if (client)
	{
		client->loadVideo (strVideoSource, strVideoDate);
        client->initialize ();
	}
}

/**********************************************************
 * Shut down player
 **********************************************************/
void ChimeAi2tvInterface::ShutDown ()
{
	if (client) client->shutdown ();
	printf("Shutting down...\n");
}

/***********************************************************
 * Let client handle a pause request
 ***********************************************************/
void ChimeAi2tvInterface::PausePressed ()
{
	if (client) client->pausePressed ();
	printf("Pause pressed\n");
}

/***********************************************************
 * Let client handle a play request
 ***********************************************************/
void ChimeAi2tvInterface::PlayPressed ()
{
	if (client) client->playPressed ();
	printf("Play pressed\n");
}

/***********************************************************
 * Let client handle a stop request
 ***********************************************************/
void ChimeAi2tvInterface::StopPressed ()
{
	if (client) client->stopPressed ();
	printf("Stop pressed\n");
}

/***********************************************************
 * indicates whether the JNI component is active
 ***********************************************************/
int ChimeAi2tvInterface::isActive ()
{
  return client->isActive();
}
