#include "cssysdef.h"
#include "cssys/sysdriv.h"
#include "csws/csws.h"
#include "csver.h"
#include "ivideo/fontserv.h"
#include "ChimeWindowToolkit.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

/*************************************************************
 * Create window components
 *************************************************************/
ChimeAi2tvWindow::ChimeAi2tvWindow(csComponent *iParent)
  : csWindow(iParent, " AI2TV Player ", CSWS_TITLEBAR, cswfsThin)
  {

  SetRect (1, app->bound.Height() / 3 * 2 + 1, app->bound.Width() / 4 - 1, app->bound.Height() - 1);

  //////////create the dialog///////////////
  csDialog *d = new csDialog(this);
  this->SetDragStyle (this->GetDragStyle () & ~CS_DRAG_SIZEABLE);
  int h = 40, w = 10, y = bound.Height () / 4;

  //setup the "Play" button
  ChimeButton *but = new ChimeButton(d, AI2TV_PLAY);
  but->SetText("PLAY");
  but->SetRect (w, y, bound.Width () / 2 - w, y + h);

  but = new ChimeButton(d, AI2TV_PAUSE);
  but->SetText("PAUSE");
  but->SetRect (bound.Width () / 2 + w, y, bound.Width () - w, y + h);
  y += h + 5;

  but = new ChimeButton(d, AI2TV_STOP);
  but->SetText("STOP");
  but->SetRect (w, y, bound.Width () / 2 - w, y + h);

  but = new ChimeButton(d, AI2TV_EXIT);
  but->SetText("EXIT");
  but->SetRect (bound.Width () / 2 + w, y, bound.Width () - w, y + h);
}

bool ChimeAi2tvWindow::HandleEvent (iEvent &Event)
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
		case AI2TV_PLAY :
			if (driver->GetAi2tvInterface ())
				driver->GetAi2tvInterface ()->PlayPressed ();
			return true;
		case AI2TV_PAUSE :
			if (driver->GetAi2tvInterface ())
				driver->GetAi2tvInterface ()->PausePressed ();
			return true;
		case AI2TV_STOP :
			if (driver->GetAi2tvInterface ())
				driver->GetAi2tvInterface ()->StopPressed ();
			return true;
		case AI2TV_EXIT :
			if (driver->GetAi2tvInterface ())
				driver->GetAi2tvInterface ()->ShutDown ();
			return true;
		}
	}

	return false;
}