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
#include "ChimeSystemDriver.h"
#include "ChimeWindowToolkit.h"

#include <stdio.h>
#include <time.h>

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////  Modular Window Basic Definitions  ///////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/********************************************************************
 * Create a modular window
 ********************************************************************/
ModularWindow::ModularWindow(csComponent *iParent, 
							 const char *iTitle, int iWindowStyle,
                             csWindowFrameStyle iFrameStyle) 
	: csWindow (iParent, iTitle, iWindowStyle, iFrameStyle) 
{
	// set system font
	SetFont (driver->GetFont (driver->chSystemFont));

	// set open modular window
    driver->OpenModularWindow (this);
}

/********************************************************************
 * Close a modular window
 ********************************************************************/
void ModularWindow::Close()
{
	// tell system to close modular window
    driver->CloseModularWindow (this);

	// close the window
	csWindow::Close();
}

/********************************************************************
 * Handle event: respond to clicking of 'Accept' or 'Cancel' button
 ********************************************************************/
bool ModularWindow::HandleEvent (iEvent &Event)
{
	// handle a button press event
	if (Event.Type == csevCommand)
	{
        switch (Event.Command.Code)
		{
		// if 'Accept' button is clicked...
		case MODULAR_WINDOW_ACCEPT :
			Accept ();
			break;
		// if 'Cancel' button is clicked...
		case MODULAR_WINDOW_CANCEL :
			Cancel ();
			break;
		}
	}

	// let window to handle event
	return csWindow::HandleEvent (Event);
}


/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////  Login Window Definitions  /////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/********************************************************************
 * Create a login window
 ********************************************************************/
LoginWindow::LoginWindow (csComponent *iParent)
	: ModularWindow (iParent, "User Login")
{
	/////// Setup window dimension
	SetSize (500, 300);

	/////// Setup window components
	// create dialog
	csDialog *d = new csDialog (this, csdfsNone);
	this->SetDragStyle (this->GetDragStyle () & CS_DRAG_MOVEABLE);

	////// Create input fields
	int x = bound.Width () / 4, y = 5, height = bound.Height () / 12, delta = height/2;
	ilUserName = new csInputLine (d);
	ilUserName->SetRect (x, y, d->bound.Width () - 5, y+height);
	csStatic *label = new csStatic (d, ilUserName, "Login: ");
	label->SetRect (10, y, x - 5, y+height);
	y += height + delta;
	ilUserName->SetText ("chimeUser");

	ilUserPassword = new csInputLine (d);
	ilUserPassword->SetRect (x, y, d->bound.Width () - 5, y+height);
	label = new csStatic (d, ilUserPassword, "Password: ");
	label->SetRect (10, y, x - 5, y+height);
	y += height + delta;
	ilUserPassword->SetText ("FooBArf");

	ilUserSource = new csInputLine (d);
	ilUserSource->SetRect (x, y, d->bound.Width () - 5, y+height);
	label = new csStatic (d, ilUserSource, "Server: ");
	label->SetRect (10, y, x - 5, y+height);
	y += height + delta;
	ilUserSource->SetText ("grand.psl.cs.columbia.edu");

	ilUserID = new csInputLine (d);
	ilUserID->SetRect (x, y, d->bound.Width () - 5, y+height);
	label = new csStatic (d, ilUserID, "User ID: ");
	label->SetRect (10, y, x - 5, y+height);
	y += height + delta;
	ilUserID->SetText ("chimeUser");

	ilGroupID = new csInputLine (d);
	ilGroupID->SetRect (x, y, d->bound.Width () - 5, y+height);
	label = new csStatic (d, ilGroupID, "Group ID: ");
	label->SetRect (10, y, x - 5, y+height);
	y += 2*(height + delta);
	ilGroupID->SetText ("psl");
	
	////// Create buttons
	// 'Accept' button
	ChimeButton *but = new ChimeButton (d, MODULAR_WINDOW_ACCEPT, CSBS_DEFAULTVALUE, csbfsThinRect);
	but->SetText ("Login");
	but->SetFont (driver->GetFont (driver->chButtonFont));
	but->SetRect (15, y, d->bound.Width () / 2 - 10, d->bound.Height () - 10);
	// 'Cancel' button
	but = new ChimeButton (d, MODULAR_WINDOW_CANCEL, CSBS_DEFAULTVALUE, csbfsThinRect);
	but->SetText ("Cancel");
	but->SetFont (driver->GetFont (driver->chButtonFont));
	but->SetRect (d->bound.Width () / 2 + 10, y, d->bound.Width () - 15, d->bound.Height () - 10);

	Center ();
}

/********************************************************************
 * On accept, login the user
 ********************************************************************/
void LoginWindow::Accept ()
{
	driver->CreateUser (ilUserName->GetText (), ilUserPassword->GetText (),
		ilUserSource->GetText (), ilUserID->GetText (), ilGroupID->GetText ());
	Close ();
	driver->Redraw ();
}

/********************************************************************
 * On cancel, close the application
 ********************************************************************/
void LoginWindow::Cancel ()
{
	Close ();
	driver->ExitSystem ();
}


/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////  Ai2tv Source Selection Window Definitions  //////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/********************************************************************
 * Create a window to select AI2TV source
 ********************************************************************/
Ai2tvSourceSelectWindow::Ai2tvSourceSelectWindow (csComponent *iParent,
												  char sources[10][50], int numSources)
	: ModularWindow (iParent, "Select AI2TV Source")
{
	/////// Setup window dimension
	SetSize (500, 300);

	/////// Setup window components
	// create dialog
	csDialog *d = new csDialog (this, csdfsNone);
	this->SetDragStyle (this->GetDragStyle () & CS_DRAG_MOVEABLE);

	// create list box
	csSourceListBox = new csListBox (d, CSLBS_HSCROLL | CSLBS_VSCROLL, cslfsThickRect);
	csSourceListBox->SetRect (bound.Width() / 10, 3,  bound.Width() / 10 * 9, bound.Height () - 80);
    csSourceListBox->SetColor (CSPAL_DIALOG_BACKGROUND, cs_Color_White);

	// add list box items
	for (int i = 0; i < numSources; i++)
		new csListBoxItem (csSourceListBox, sources[i]);

	////// Create buttons
	// 'Accept' button
	ChimeButton *but = new ChimeButton (d, MODULAR_WINDOW_ACCEPT, CSBS_DEFAULTVALUE, csbfsThinRect);
	but->SetText ("Select");
	but->SetFont (driver->GetFont (driver->chButtonFont));
	but->SetRect (15, bound.Height () - 75, d->bound.Width () / 2 - 10, d->bound.Height () - 10);
	// 'Cancel' button
	but = new ChimeButton (d, MODULAR_WINDOW_CANCEL, CSBS_DEFAULTVALUE, csbfsThinRect);
	but->SetText ("Cancel");
	but->SetFont (driver->GetFont (driver->chButtonFont));
	but->SetRect (d->bound.Width () / 2 + 10, bound.Height () - 75, d->bound.Width () - 15, d->bound.Height () - 10);

	Center ();
}

/********************************************************************
 * On accept, select the selected source
 ********************************************************************/
void Ai2tvSourceSelectWindow::Accept ()
{
	if (csSelectedSource)
	{
		new Ai2tvSetTimeWindow (parent, csSelectedSource->GetText ());
        Close ();
	}
}

/********************************************************************
 * On cancel, close the window
 ********************************************************************/
void Ai2tvSourceSelectWindow::Cancel ()
{
	Close ();
}

/********************************************************************
 * Override event handler to remember a selected item
 ********************************************************************/
bool Ai2tvSourceSelectWindow::HandleEvent (iEvent &Event)
{
    
	if (Event.Type == csevCommand &&
        Event.Command.Code == cscmdListBoxItemClicked ||
		Event.Command.Code == cscmdListBoxItemDoubleClicked ||
        Event.Command.Code == cscmdListBoxItemSelected)
	{
		csSelectedSource = (csListBoxItem*) Event.Command.Info;
		if (Event.Command.Code == cscmdListBoxItemDoubleClicked)
			Accept ();
	}

	return ModularWindow::HandleEvent (Event);
}


/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////  Ai2tv Set Time Window Definitions  //////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/********************************************************************
 * Create a window to set time for loading video
 ********************************************************************/
Ai2tvSetTimeWindow::Ai2tvSetTimeWindow (csComponent *iParent, char* source)
	: ModularWindow (iParent, "Set Time")
{
	/////// Setup window dimension
	SetSize (500, 300);

	////// Get current time
	char sYear[10], sMonth[10], sDay[10], sDayOfWeek[10], sHour[4], sMinute[4], sSecond[4], sTime[20];
	time_t rawtime;
    struct tm * timeinfo;
    time ( &rawtime );
    timeinfo = localtime ( &rawtime );
	sscanf (asctime (timeinfo), "%s %s %s %s %s", sDayOfWeek, sMonth, sDay, sTime, sYear);

	// parse time into hours/minutes/seconds
	sHour[0] = sTime[0]; sHour[1] = sTime[1]; sHour[2] = '\0';
	sMinute[0] = sTime[3]; sMinute[1] = sTime[4]; sMinute[2] = '\0';
	sSecond[0] = sTime[6]; sSecond[1] = sTime[7]; sSecond[2] = '\0';

	// convert month name into month number
	if(!strcmp (sMonth, "Jan")) strcpy (sMonth, "01");
	if(!strcmp (sMonth, "Feb")) strcpy (sMonth, "02");
	if(!strcmp (sMonth, "Mar")) strcpy (sMonth, "03");
	if(!strcmp (sMonth, "Apr")) strcpy (sMonth, "04");
	if(!strcmp (sMonth, "May")) strcpy (sMonth, "05");
	if(!strcmp (sMonth, "Jun")) strcpy (sMonth, "06");
	if(!strcmp (sMonth, "Jul")) strcpy (sMonth, "07");
	if(!strcmp (sMonth, "Aug")) strcpy (sMonth, "08");
	if(!strcmp (sMonth, "Sep")) strcpy (sMonth, "09");
	if(!strcmp (sMonth, "Oct")) strcpy (sMonth, "10");
	if(!strcmp (sMonth, "Nov")) strcpy (sMonth, "11");
	if(!strcmp (sMonth, "Dec")) strcpy (sMonth, "12");

	////// Set window title
	if (source)
	{
		char title[50];
		sprintf (title, "Set Time For: %s", source);
		SetText (title);
		strcpy (strVideoSource, source);
	}

	/////// Setup window components
	// create dialog
	csDialog *d = new csDialog (this, csdfsNone);
	this->SetDragStyle (this->GetDragStyle () & CS_DRAG_MOVEABLE);

	csStatic* label = NULL;
	int w = bound.Width () / 5, h = bound.Height () / 10, dw = w / 2, dh = bound.Height () / 25, x = dw, y = dh;

	// create date fields
	label = new csStatic (d, csscsLabel);
	label->SetText ("Year/Month/Day");
	label->SetRect (bound.Width () / 2 - 50, y, bound.Width () / 2 + 50, y + dh);
	y += h + dh;

	ilYear = new csInputLine (d);
	ilYear->SetRect (x, y, x + w, y + h);
	ilYear->SetText (sYear);
	x += w + dw;

	ilMonth = new csInputLine (d);
	ilMonth->SetRect (x, y, x + w, y + h);
	ilMonth->SetText (sMonth);
	x += w + dw;

	ilDay = new csInputLine (d);
	ilDay->SetRect (x, y, x + w, y + h);
	ilDay->SetText (sDay);
	x = dw; y += dh + h;

	// create time fields
	label = new csStatic (d, csscsLabel);
	label->SetText ("Hour/Minute/Second");
	label->SetSize (100, 40);
	label->SetRect (bound.Width () / 2 - 60, y, bound.Width () / 2 + 60, y + dh);
	y += dh + h;

	ilHour = new csInputLine (d);
	ilHour->SetRect (x, y, x + w, y + h);
	ilHour->SetText (sHour);
	x += w + dw;

	ilMinute = new csInputLine (d);
	ilMinute->SetRect (x, y, x + w, y + h);
	ilMinute->SetText (sMinute);
	x += w + dw;

	ilSecond = new csInputLine (d);
	ilSecond->SetRect (x, y, x + w, y + h);
	ilSecond->SetText (sSecond);
	x = dw; y += 2*(h + dh);

	////// Create buttons
	// 'Accept' button
	ChimeButton *but = new ChimeButton (d, MODULAR_WINDOW_ACCEPT, CSBS_DEFAULTVALUE, csbfsThinRect);
	but->SetText ("Select");
	but->SetFont (driver->GetFont (driver->chButtonFont));
	but->SetRect (x, y, d->bound.Width () / 2 - 10, d->bound.Height () - 10);
	// 'Cancel' button
	but = new ChimeButton (d, MODULAR_WINDOW_CANCEL, CSBS_DEFAULTVALUE, csbfsThinRect);
	but->SetText ("Cancel");
	but->SetFont (driver->GetFont (driver->chButtonFont));
	but->SetRect (d->bound.Width () / 2 + 10, y, d->bound.Width () - 15, d->bound.Height () - 10);

	Center ();
}

/********************************************************************
 * On accept, set time
 ********************************************************************/
void Ai2tvSetTimeWindow::Accept ()
{
	char full_date [100];
	sprintf (full_date, "%s-%s-%s;%s:%s:%s\n",
		ilYear->GetText (),
		ilMonth->GetText (),
		ilDay->GetText (),
		ilHour->GetText (),
		ilMinute->GetText (),
		ilSecond->GetText ()
		);
	if (driver->GetAi2tvInterface ())
		driver->GetAi2tvInterface ()->SelectVideo (strVideoSource, full_date);
	driver->BuildAi2tvScreen ();
	Close ();
}

/********************************************************************
 * On cancel, just close the window
 ********************************************************************/
void Ai2tvSetTimeWindow::Cancel ()
{
	Close ();
}
