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

ChatBoxItem::ChatBoxItem (csComponent *iParent, 
		const char *iUserName, const char *iUserSource, 
		int iID, csListBoxItemStyle iStyle)
		: csListBoxItem (iParent, iUserName, iID, iStyle)
{
	strUserName = (char*) malloc (100 * sizeof(char));
	strUserSource = (char*) malloc (100 * sizeof(char));

	strcpy (strUserName, iUserName);
	strcpy (strUserSource, iUserSource);

	csItemHint = NULL;
}

void ChatBoxItem::ShowHint ()
{
	char hint_text [200];
	strcpy (hint_text, strUserName);
	strcat (hint_text, " @ ");
	strcat (hint_text, strUserSource);
	csItemHint = new csHint (this, hint_text);
	csItemHint->Show ();
}

void ChatBoxItem::HideHint ()
{
	if (!csItemHint)
		return;

	csItemHint->Hide ();
	csItemHint->Close ();
	csItemHint = NULL;
}

bool ChatBoxItem::HandleEvent (iEvent &Event)
{
	/**
	switch (Event.Type)
	{
	case csevMouseDown:
		if (Event.Mouse.Button == 2) ShowHint ();
		break;
	case csevMouseUp:
		if (Event.Mouse.Button == 2) HideHint ();
		break;
	case csevMouseMove:
		HideHint ();
		break;
	}
	*/

	return csListBoxItem::HandleEvent (Event);
}


//------------------------------------------//
//---- Chat Window implementation -------//
//------------------------------------------//

// Scroll bar class default palette
ChimeChatWindow::~ChimeChatWindow() {}


ChimeChatWindow::ChimeChatWindow(csComponent *iParent)
  : csWindow(iParent, " Chat ", CSWS_TITLEBAR, cswfsThin)
  {

  SetRect (1, app->bound.Height() / 3 + 1, app->bound.Width() / 4, app->bound.Height() / 3 * 2);

  int px = 15, py = 20;
  int labelw = 150;

  //////////create the dialog///////////////
  csDialog *d = new csDialog(this);
  this->SetDragStyle (this->GetDragStyle () & ~CS_DRAG_SIZEABLE);
  
  //////////create the list box/////////////
  list_box = new csListBox (d, CSLBS_HSCROLL | CSLBS_VSCROLL, cslfsThinRect);
  list_box->SetRect (bound.Width() / 10, 1,  bound.Width() / 10 * 9, bound.Height() / 2 - 1);

  //setup the "Chat"
  csButton *ChatBut = new csButton(d, CHAT_CHAT_PRESSED);
  ChatBut->SetText("CHAT");
  ChatBut->SetSize(bound.Width ()/2, bound.Height() / 3);
  ChatBut->SetPos(bound.Width ()/4, bound.Height() / 2 + 1);
  
  selected_item = NULL;
  item_list = new csVector (8);
}

//Add an item to the Chat Box
bool ChimeChatWindow::AddItem (char *iUserName, char *iUserSource) {

	if (FindItem (iUserName, iUserSource))
		return false;
	selected_item = new ChatBoxItem (list_box, iUserName, iUserSource);
	item_list->Push (selected_item);
	return true;
}

//Find list item corresponding to the parameters
ChatBoxItem* ChimeChatWindow::FindItem (char *iUserName, char *iUserSource)
{
	ChatBoxItem *item = NULL;
	for (int i = 0; i < item_list->Length (); i++)
	{
		item = (ChatBoxItem*) item_list->Get (i);
		if (!strcmp (iUserName, item->GetUserName ()) && 
			!strcmp (iUserSource, item->GetUserSource ()))
			return item;
	}
	return NULL;
}


//Handle and Event generated by the Chat Window
bool ChimeChatWindow::HandleEvent (iEvent &Event)
{

  if (csWindow::HandleEvent (Event))
    return true;

  if (Event.Type == csevCommand)
  {
      switch (Event.Command.Code)
      {
		case cscmdListBoxItemClicked:
		case cscmdListBoxItemSelected:
			selected_item = (ChatBoxItem*) Event.Command.Info;
			return true;

        case CHAT_CHAT_PRESSED:
			if (selected_item) 
			{
				printf ("%s @ %s\n", selected_item->GetUserName (), 
					selected_item->GetUserSource ());
			}
		return true;
	  }
  }

  return false;
}
