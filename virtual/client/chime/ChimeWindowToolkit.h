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
	char *strSectorName, *strSectorSource;
	csHint *csItemHint;

public:
	HistoryBoxItem (csComponent *iParent, 
		const char *iSectorName, const char *iSectorSource, 
		int iID=0, csListBoxItemStyle iStyle=cslisNormal);

	char* GetSectorName () { return strSectorName; }
	char* GetSectorSource () { return strSectorSource; }
	bool HandleEvent (iEvent &Event);
	void ShowHint ();
	void HideHint ();
};

/**************************************************************
 * History window is a list of visited ChimeSectors
 **************************************************************/
#define HISTORY_GO_THERE_PRESSED HISTORY_WINDOW_BASE + 1
class ChimeHistoryWindow : public csWindow
{

private:
	csListBox *list_box;
	HistoryBoxItem *selected_item;
	csVector *item_list;

public:

  ChimeHistoryWindow (csComponent *iParent);
  bool AddItem (char *iSectorName, char *iSectorSource);
  HistoryBoxItem* FindItem (char *iSectorName, char *iSectorSource);
  virtual bool HandleEvent (iEvent &Event);
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
	char *strUserName, *strUserSource;
	csHint *csItemHint;

public:
	ChatBoxItem (csComponent *iParent, 
		const char *iUserName, const char *iUserSource, 
		int iID=0, csListBoxItemStyle iStyle=cslisNormal);

	char* GetUserName () { return strUserName; }
	char* GetUserSource () { return strUserSource; }
	bool HandleEvent (iEvent &Event);
	void ShowHint ();
	void HideHint ();
};

/**************************************************************
 * Chat window is a list of visited ChimeSectors
 **************************************************************/
#define CHAT_CHAT_PRESSED CHAT_WINDOW_BASE + 1
class ChimeChatWindow : public csWindow
{

private:
	csListBox *list_box;
	ChatBoxItem *selected_item;
	csVector *item_list;

public:

  ChimeChatWindow (csComponent *iParent);
  bool AddItem (char *iUserName, char *iUserSource);
  ChatBoxItem* FindItem (char *iSectorName, char *iSectorSource);
  virtual bool HandleEvent (iEvent &Event);
};


#endif