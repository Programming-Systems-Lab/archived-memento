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

  SetRect (1, 2, app->bound.Width() / 4 - 1, app->bound.Height() / 3);

  int px = 15, py = 20;
  int labelw = 150;

  //////////create the dialog///////////////
  csDialog *d = new csDialog(this);
  this->SetDragStyle (this->GetDragStyle () & ~CS_DRAG_SIZEABLE);

  //setup the "Play" button
  csButton *PlayBut = new csButton(d, -1);
  PlayBut->SetText("PLAY");
  PlayBut->SetSize(bound.Width ()/2, bound.Height() / 3);
  PlayBut->SetPos(bound.Width ()/4, bound.Height() / 2 + 1);
}