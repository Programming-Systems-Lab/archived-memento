/*******************************************************************
    ChimeEngineView.cpp
	Author: Mark Galagan @ 2003

	Utility class to wrap a 3D view (iView) as a csComponent
	under a CSWS window. This way, the view lies within the bounds
	of the parent window and its moved, resized, etc. together
	with the parent window.
********************************************************************/


#include "cssysdef.h"
#include "ChimeEngineView.h"
#include "csws/csws.h"


/*****************************************************************
 * Constructor: gets the parents, creates the view for given
 * sector and location, and calls the super constructor to
 * initialize its CSWS parent window.
 *****************************************************************/
ChimeEngineView::ChimeEngineView (csComponent *iParent, iEngine *Engine,
  	iSector *Start, const csVector3& start_pos, iGraphics3D *G3D, csApp *app)
	: csComponent (iParent)
{
  chApplication = app;
  parentWindow = (csWindow*) iParent;

  // create the view
  view = csPtr<iView> (new csView (Engine, G3D));
  view->GetCamera ()->SetSector (Start);
  view->GetCamera ()->GetTransform ().SetOrigin (start_pos);

  // setup parameters
  SetState (CSS_SELECTABLE, true);
  if (parent)
    parent->SendCommand (cscmdWindowSetClient, (void *)this);

}

/*****************************************************************
 * Decstructor
 *****************************************************************/
ChimeEngineView::~ChimeEngineView ()
{

}

/*****************************************************************
 * SetRect overrides the super definition of the csComponent
 * SetRect function. It converts coordinates sent by the
 * parent window into the coordinates for the engine, and
 * adjusts its bounds accordingly.
 *****************************************************************/
bool ChimeEngineView::SetRect (int xmin, int ymin, int xmax, int ymax)
{
  // call the super function
  bool rc = csComponent::SetRect (xmin, ymin, xmax, ymax);

  // convert bounds into global coordinates
  parent->LocalToGlobal (xmin, ymin);
  parent->LocalToGlobal (xmax, ymax);

  // adjust the view to within the bounds
  ymin = chApplication->bound.Height () - ymin;
  ymax = chApplication->bound.Height () - ymax;
  windowRectangle = new csRect (xmin, ymax, xmax, ymin);
  view->SetRectangle (xmin, ymax, xmax - xmin, ymin - ymax);
  view->GetCamera ()->SetPerspectiveCenter (
  	(xmin + xmax) / 2, (ymin + ymax) / 2);

  return rc;
}