 /*
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 *    in the City of New York.  All Rights Reserved.
 *
 *
 */

#include "cssysdef.h"
#include "cssys/sysdriv.h"
#include "csws/csws.h"
#include "csver.h"
#include "ivideo/fontserv.h"
#include "ChimeWindowToolkit.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

//------------------------------------------//
//---- ChatBoxItem implementation -------//
//------------------------------------------//

/*************************************************************
 * Constructor initializes parameters
 *************************************************************/
ChatBoxItem::ChatBoxItem (csComponent *iParent, 
		const char *iUserName, const char *iUserSource, 
		int iID, csListBoxItemStyle iStyle)
		: csListBoxItem (iParent, iUserName, iID, iStyle)
{
	// copy user parameters
	strcpy (strUserName, iUserName);
	strcpy (strUserSource, iUserSource);
}

/*************************************************************
 * Returns true if given parameter is the same as user
 * parameters in the following form: "name source"
 *************************************************************/
bool ChatBoxItem::IsThisUser (char *strFullUserName)
{
	bool r = false;

	// parse name and source
	char name[100], source[100];
	sscanf (strFullUserName, "%s %s", name, source);
	printf("Name: %s, Source: %s\n", name, source);

	// see if the parameters are the same
	if (!strcmp (name, strUserName) && !strcmp (source, strUserSource))
		r = true;

	return r;
}

/*************************************************************
 * Copy user name into passed string
 *************************************************************/
void ChatBoxItem::GetUserName (char *iUserName)
{
	strcpy (iUserName, strUserName);
}

/*************************************************************
 * Copy user source into passed string
 *************************************************************/
void ChatBoxItem::GetUserSource (char *iUserSource)
{
	strcpy (iUserSource, strUserSource);
}

/*************************************************************
 * Add specific event handling
 *************************************************************/
bool ChatBoxItem::HandleEvent (iEvent &Event)
{
	switch (Event.Type)
	{
	// on double mouse click, activate this item
	case csevMouseDoubleClick:
		ActivateItem ();
		break;
	}

	return csListBoxItem::HandleEvent (Event);
}

/**************************************************************
 * Activate this item: start chat session
 **************************************************************/
void ChatBoxItem::ActivateItem ()
{
	// for now, just print parameters
	printf("User %s @ %s\n", strUserName, strUserSource);
}


//------------------------------------------//
//---- Chat Window implementation -------//
//------------------------------------------//

ChimeChatWindow::~ChimeChatWindow() {}

/*************************************************************
 * Create window components
 *************************************************************/
ChimeChatWindow::ChimeChatWindow(csComponent *iParent)
  : csWindow(iParent, " Chat ", CSWS_TITLEBAR, cswfsThin)
  {

  SetRect (1, app->bound.Height() / 3 + 1, app->bound.Width() / 4 - 1, app->bound.Height() / 3 * 2);

  int px = 15, py = 20;
  int labelw = 150;

  //////////create the dialog///////////////
  csDialog *d = new csDialog(this, csdfsAround);
  this->SetDragStyle (this->GetDragStyle () & ~CS_DRAG_SIZEABLE);
  
  //////////create the list box/////////////
  list_box = new csListBox (d, CSLBS_HSCROLL | CSLBS_VSCROLL, cslfsThickRect);
  list_box->SetRect (bound.Width() / 10, 3,  bound.Width() / 10 * 9, bound.Height () - 80);
  list_box->SetColor (CSPAL_DIALOG_BACKGROUND, cs_Color_White);

  //setup the "Chat"
  ChimeButton *ChatBut = new ChimeButton(d, CHAT_CHAT_PRESSED, CSBS_DEFAULTVALUE, csbfsThickRect);
  ChatBut->SetText("Chat");
  ChatBut->SetSize(100, 40);
  ChatBut->SetPos (bound.Width () / 4, bound.Height () - 70);
  ChatBut->Center (true, false);
  ChatBut->SetFont (driver->GetFont (driver->chButtonFont));
  
  selected_item = NULL;
}

/*************************************************************
 * Add an item to chat box, unless it already exists
 *************************************************************/
bool ChimeChatWindow::AddItem (char *iUserName, char *iUserSource)
{
	// if theitem is found, don't add
	if (FindItem (iUserName, iUserSource))
		return false;

	// add new item
	selected_item = new ChatBoxItem (list_box, iUserName, iUserSource);
	return true;
}

/*************************************************************
 * Remove user with given parameters
 *************************************************************/
bool ChimeChatWindow::RemoveItem (char *iUserName, char *iUserSource)
{
	return RemoveItem (FindItem (iUserName, iUserSource));
}

/*************************************************************
 * Remove given item
 *************************************************************/
bool ChimeChatWindow::RemoveItem (csComponent *comp)
{
	list_box->Delete (comp);
	return true;
}

/*************************************************************
 * Remove all items
 *************************************************************/
bool ChimeChatWindow::RemoveAllItems ()
{
	csComponent *comp = NULL;
	do
	{
		comp = list_box->ForEachItem (UserExists, NULL, false);
		if (comp) list_box->Delete (comp);
	}
	while (comp != NULL);
	return true;
}

/*************************************************************
 * Find list item corresponding to the parameters
 *************************************************************/
ChatBoxItem* ChimeChatWindow::FindItem (char *iUserName, char *iUserSource)
{
	// create full user title
	char full_user_name[200];
	strcpy (full_user_name, iUserName);
	strcat (full_user_name, " ");
	strcat (full_user_name, iUserSource);

	// see if the item exists
	ChatBoxItem *item = (ChatBoxItem*) list_box->ForEachItem (UserExists, full_user_name, false);
	return item;
}

/*********************************************************************************
 * See if given ChatBoxItem corresponds to given full user name
 *********************************************************************************/
bool ChimeChatWindow::UserExists (csComponent *item, void *iFullUserName)
{
	if (!iFullUserName) return true;
	return ((ChatBoxItem*) item)->IsThisUser ((char*) iFullUserName);
}


/************************************************************
 * Handle an event generated by the Chat Window
 ************************************************************/
bool ChimeChatWindow::HandleEvent (iEvent &Event)
{

  // do not allow window movement
  if (Event.Type == csevMouseMove)
      return true;
	
  // let the window handle the event
  if (csWindow::HandleEvent (Event))
    return true;

  if (Event.Type == csevCommand)
  {
      switch (Event.Command.Code)
      {
		// select an item
	    case cscmdListBoxItemClicked:
		case cscmdListBoxItemSelected:
			selected_item = (ChatBoxItem*) Event.Command.Info;
			return true;

        // if "Chat" button was pressed, activate selected item
		case CHAT_CHAT_PRESSED:
			if (selected_item) 
			{
				selected_item->ActivateItem ();
			}
		return true;
	  }
  }

  return false;
}
