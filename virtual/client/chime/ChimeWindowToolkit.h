 /*
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 *    in the City of New York.  All Rights Reserved.
 *
 *
 */
#ifndef CHIME_WINDOW_TOOLKIT_H
#define CHIME_WINDOW_TOOLKIT_H

#include <stdarg.h>
#include "cssys/sysdriv.h"
#include "csgeom/math2d.h"
#include "csgeom/math3d.h"
#include "csws/csws.h"
#include "csws/cswindow.h"

#include "ChimeCodeBase.h"


// Scroll bar class default palette
static int palette [] =
{
  cs_Color_Gray_D,			// Application workspace
  cs_Color_Green_L,			// End points
  cs_Color_Red_L,			// lines
  cs_Color_White			// Start points
};


//////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// Base Windows ///////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

/**************************************************************
 * A modular window is to a pop-up dialog that persists until
 * it is properly closed. All 3D animation is suspended
 * while a modular window is open.
 **************************************************************/
class ModularWindow : public csWindow
{
	public:
		//stop the animation when creating a window
		ModularWindow(csComponent *iParent, const char *iTitle, 
			int iWindowStyle=CSWS_DEFAULTVALUE, csWindowFrameStyle iFrameStyle=cswfs3D);

		//start the animation when closing the window
		virtual void Close();
};


//////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// History Window /////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

/****************************************************
 * History window item holds pertinent information
 * about related ChimeSector
 ****************************************************/
class HistoryBoxItem : public csListBoxItem
{
private:
	// parameters that define a sector (name + source)
	char strSectorName [100], strSectorSource [100];

public:
	HistoryBoxItem (csComponent *iParent, 
		const char *iSectorName, const char *iSectorSource, 
		int iID=0, csListBoxItemStyle iStyle=cslisNormal);

	bool IsThisSector (char *strFullSectorName);		// returns true if this user has given full name
	void GetSectorName (char *iSectorName);				// returns sector name
	void GetSectorSource (char *iSectorSource);			// returns sector source
	bool HandleEvent (iEvent &Event);					// add specific event handling
	void ActivateItem ();								// activate this item
};

/**************************************************************
 * History window is a list of visited ChimeSectors
 **************************************************************/
#define HISTORY_GO_THERE_PRESSED HISTORY_WINDOW_BASE + 1
class ChimeHistoryWindow : public csWindow
{

private:
	// list box of items
	csListBox *list_box;

	// selected item
	HistoryBoxItem *selected_item;

public:

  ChimeHistoryWindow (csComponent *iParent);
  bool AddItem (char *iSectorName, char *iSectorSource);					// add another item
  HistoryBoxItem* FindItem (char *iSectorName, char *iSectorSource);		// find item based on parameters
  virtual bool HandleEvent (iEvent &Event);									// add specific event handling
  static bool SectorExists (csComponent *item, void *iFullSectorName);		// find out if sector exists
};


//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// Chat Window /////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

/****************************************************
 * Chat window item holds pertinent information
 * about related user
 ****************************************************/
class ChatBoxItem : public csListBoxItem
{
private:
	// parameters that define a user (name + source)
	char strUserName [100], strUserSource [100];

public:
	ChatBoxItem (csComponent *iParent, 
		const char *iUserName, const char *iUserSource, 
		int iID=0, csListBoxItemStyle iStyle=cslisNormal);

	bool IsThisUser (char *strFullUserName);			// tell if this is the user
	void GetUserName (char *iUserName);					// return user name
	void GetUserSource (char *iUserSource);				// return user source
	bool HandleEvent (iEvent &Event);					// add specific item handling
	void ActivateItem ();								// activate this item
};

/**************************************************************
 * Chat window is a list of visited ChimeSectors
 **************************************************************/
#define CHAT_CHAT_PRESSED CHAT_WINDOW_BASE + 1
class ChimeChatWindow : public csWindow
{

private:
	// list box of users
	csListBox *list_box;

	// selected item
	ChatBoxItem *selected_item;

public:

  ChimeChatWindow (csComponent *iParent);
  bool AddItem (char *iUserName, char *iUserSource);					// add one item with given parameters
  ChatBoxItem* FindItem (char *iUserName, char *iUserSource);			// find item based on given parameters
  virtual bool HandleEvent (iEvent &Event);								// add specific item handling
  static bool UserExists (csComponent *item, void *iFullUserName);		// tell if this is the same user
};


//////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// AI2TV Player Window ////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
/**************************************************************
 * Chat window is a list of visited ChimeSectors
 **************************************************************/
class ChimeAi2tvWindow : public csWindow
{

private:

public:

  ChimeAi2tvWindow (csComponent *iParent);
};

#endif