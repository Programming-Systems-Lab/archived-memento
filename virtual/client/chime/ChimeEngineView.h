/*******************************************************************
    ChimeEngineView.h
	Author: Mark Galagan @ 2003

	Header file for the CHIME engine view
********************************************************************/

#ifndef __ChimeEngineView_H__
#define __ChimeEngineView_H__

#include "cstool/csview.h"
#include "csws/cscomp.h"
#include "csws/cswindow.h"
#include "iengine/sector.h"
#include "iengine/engine.h"
#include "iengine/camera.h"
#include "ivideo/graph3d.h"
#include "ivideo/graph2d.h"
#include "ivideo/natwin.h"

/******************************************
 * Class definition for ChimeEngineView   *
 ******************************************/
class ChimeEngineView : public csComponent
{
private:

  csRef<iView> view;				// actual view
  csApp* chApplication;				// parent csApp instance
  csWindow* parentWindow;			// parent window
  csRect* windowRectangle;			// rectagle of this window in clobal coordinates

public:

  // Constructor & Destructor
  ChimeEngineView (csComponent *iParent, iEngine *Engine, iSector *Start,
    const csVector3& start_pos, iGraphics3D *G3D, csApp *app);
  virtual ~ChimeEngineView ();

  // Track movement of the window and update engine.
  virtual bool SetRect (int xmin, int ymin, int xmax, int ymax);

  // Redraw the engine view.
  virtual void Draw () { view->Draw(); }

  // Get the view.
  iView* GetView () { return view; }

  // Get the camera
  iCamera* GetCamera () { return view->GetCamera (); }

  // Get the parent window
  csWindow* GetWindow () { return parentWindow; }

  // Get the view's rectangle
  csRect* GetWindowRectangle () { return windowRectangle; }
};

#endif // __ChimeEngineView_H__