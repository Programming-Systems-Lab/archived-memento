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
#define MODULAR_WINDOW_ACCEPT MODULAR_WINDOW_BASE+1
#define MODULAR_WINDOW_CANCEL MODULAR_WINDOW_BASE+2
class ModularWindow : public csWindow
{
	protected:
		//close the window
		virtual void Close();

		//'Accept' button clicked
		virtual void Accept () { Close (); }

		//'Cancel' button clicked
		virtual void Cancel () { Close (); }

	public:

		//stop the animation when creating a window
		ModularWindow(csComponent *iParent, const char *iTitle, 
			int iWindowStyle=CSWS_TITLEBAR, csWindowFrameStyle iFrameStyle=cswfs3D);

		//handle an event
		virtual bool HandleEvent (iEvent &Event);
};

class ChimeButton : public csButton 
{
private:
	int currentSchemaID;
public:
	ChimeButton (csComponent *iParent, int iCommandCode, 
		int iButtonStyle=CSBS_DEFAULTVALUE, 
		csButtonFrameStyle iFrameStyle=csbfsOblique);

	void Draw ();
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
  bool RemoveItem (char *iUserName, char *iUserSource);					// remove given user
  bool RemoveItem (csComponent *comp);									// remove given component
  bool RemoveAllItems ();												// remove all items
  ChatBoxItem* FindItem (char *iUserName, char *iUserSource);			// find item based on given parameters
  virtual bool HandleEvent (iEvent &Event);								// add specific item handling
  static bool UserExists (csComponent *item, void *iFullUserName);		// tell if this is the same user
};


//////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// AI2TV Windows ////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
#define AI2TV_PLAY AI2TV_WINDOW_BASE + 1
#define AI2TV_PAUSE AI2TV_WINDOW_BASE + 2
#define AI2TV_STOP AI2TV_WINDOW_BASE + 3
#define AI2TV_EXIT AI2TV_WINDOW_BASE + 4
/**************************************************************
 * AI2TV player window controls AI2TV playback
 **************************************************************/
class ChimeAi2tvWindow : public csWindow
{

private:

public:
  ChimeAi2tvWindow (csComponent *iParent);
  bool HandleEvent (iEvent &Event);
};

/**************************************************************
 * Login Window
 **************************************************************/
class LoginWindow : public ModularWindow
{
private:
	void Accept ();
	void Cancel ();

	csInputLine *ilUserName, *ilUserPassword, *ilUserSource, *ilUserID, *ilGroupID;

public:
    LoginWindow (csComponent *iParent);
};

/**************************************************************
 * AI2TV Source Select Window
 **************************************************************/
class Ai2tvSourceSelectWindow : public ModularWindow
{
private:
	void Accept ();
	void Cancel ();

	csListBox *csSourceListBox;
	csListBoxItem *csSelectedSource;

public:
    Ai2tvSourceSelectWindow (csComponent *iParent, char sources[10][50], int numSources);
	bool HandleEvent (iEvent &Event);
};

/**************************************************************
 * AI2TV Set Time Window
 **************************************************************/
class Ai2tvSetTimeWindow : public ModularWindow
{
private:
	void Accept ();
	void Cancel ();

	char strVideoSource[50];
	csInputLine *ilYear, *ilMonth, *ilDay, *ilHour, *ilMinute, *ilSecond;

public:
    Ai2tvSetTimeWindow (csComponent *iParent, char* source);
};


#endif