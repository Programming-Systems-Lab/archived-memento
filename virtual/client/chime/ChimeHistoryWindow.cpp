
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
//---- HistoryBoxItem implementation -------//
//------------------------------------------//

/*************************************************************
 * Constructor initializes parameters
 *************************************************************/
HistoryBoxItem::HistoryBoxItem (csComponent *iParent, 
		const char *iSectorName, const char *iSectorSource, 
		int iID, csListBoxItemStyle iStyle)
		: csListBoxItem (iParent, iSectorName, iID, iStyle)
{
	// copy sector parameters
	strcpy (strSectorName, iSectorName);
	strcpy (strSectorSource, iSectorSource);
}

/*************************************************************
 * Returns true if given parameter is the same as sector
 * parameters in the following form: "name source"
 *************************************************************/
bool HistoryBoxItem::IsThisSector (char *strFullSectorName)
{
	bool r = false;
	
	// parse sector name and source
	char name[100], source[100];
	sscanf (strFullSectorName, "%s %s", name, source);
	printf("Name: %s, Source: %s\n", name, source);

	// see if these parameters are the same
	if (!strcmp (name, strSectorName) && !strcmp (source, strSectorSource))
		r = true;

	return r;
}

/*************************************************************
 * Copy sector name into passed string
 *************************************************************/
void HistoryBoxItem::GetSectorName (char *iSectorName)
{
	strcpy (iSectorName, strSectorName);
}

/*************************************************************
 * Copy sector source into passed string
 *************************************************************/
void HistoryBoxItem::GetSectorSource (char *iSectorSource)
{
	strcpy (iSectorSource, strSectorSource);
}

/*************************************************************
 * Add specific event handling
 *************************************************************/
bool HistoryBoxItem::HandleEvent (iEvent &Event)
{
	/**
	switch (Event.Type)
	{
	// on double mouse click, activate this item
	case csevMouseDoubleClick:
		ActivateItem ();
		break;
	}
	*/

	// let list handle the event
	return csListBoxItem::HandleEvent (Event);
}

/**************************************************************
 * Activate this item: transport the user to the selected room
 **************************************************************/
void HistoryBoxItem::ActivateItem ()
{
	driver->TransportToSector (strSectorName, strSectorSource);
}


//------------------------------------------//
//---- History Window implementation -------//
//------------------------------------------//

ChimeHistoryWindow::~ChimeHistoryWindow() {}

/*************************************************************
 * Create window components
 *************************************************************/
ChimeHistoryWindow::ChimeHistoryWindow(csComponent *iParent)
  : csWindow(iParent, " History ", CSWS_TITLEBAR, cswfsThin)
  {

  SetRect (1, 2, app->bound.Width() / 4 - 1, app->bound.Height() / 3);

  int px = 15, py = 20;
  int labelw = 150;

  //////////create the dialog///////////////
  csDialog *d = new csDialog(this);
  this->SetDragStyle (this->GetDragStyle () & ~CS_DRAG_SIZEABLE);
  
  //////////create the list box/////////////
  list_box = new csListBox (d, CSLBS_HSCROLL | CSLBS_VSCROLL, cslfsThinRect);
  list_box->SetRect (bound.Width() / 10, 1,  bound.Width() / 10 * 9, bound.Height() / 2 - 1);

  //setup the "Go There"
  csButton *GoBut = new csButton(d, HISTORY_GO_THERE_PRESSED);
  GoBut->SetText("GO THERE");
  GoBut->SetSize(bound.Width ()/2, bound.Height() / 3);
  GoBut->SetPos(bound.Width ()/4, bound.Height() / 2 + 1);
  
  selected_item = NULL;
}

/*************************************************************
 * Add an item to history box, unless it already exists
 *************************************************************/
bool ChimeHistoryWindow::AddItem (char *iSectorName, char *iSectorSource) {

	// if the item is found, don't add
	if (FindItem (iSectorName, iSectorSource))
		return false;

	// add new item
	selected_item = new HistoryBoxItem (list_box, iSectorName, iSectorSource);

	return true;
}

/*************************************************************
 * Find list item corresponding to the parameters
 *************************************************************/
HistoryBoxItem* ChimeHistoryWindow::FindItem (char *iSectorName, char *iSectorSource)
{
	// create full sector title
	char full_sector_name[200];
	strcpy (full_sector_name, iSectorName);
	strcat (full_sector_name, " ");
	strcat (full_sector_name, iSectorSource);

	// see if the item exists
	HistoryBoxItem *item = (HistoryBoxItem*) list_box->ForEachItem (SectorExists, full_sector_name, false);
	return item;
}

/*********************************************************************************
 * See if given HistoryBoxItem corresponds to given full sector name
 *********************************************************************************/
bool ChimeHistoryWindow::SectorExists (csComponent *item, void *iFullSectorName)
{
	return ((HistoryBoxItem*) item)->IsThisSector ((char*) iFullSectorName);
}

/************************************************************
 * Handle an event generated by the History Window
 ************************************************************/
bool ChimeHistoryWindow::HandleEvent (iEvent &Event)
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
			selected_item = (HistoryBoxItem*) Event.Command.Info;
			return true;

        // if "Go There" button was pressed, activate selected item
		case HISTORY_GO_THERE_PRESSED:
			if (selected_item) 
			{
				selected_item->ActivateItem ();
			}
		return true;
	  }
  }

  return false;
}
