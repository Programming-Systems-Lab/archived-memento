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

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

//stop 3D and open this window
ModularWindow::ModularWindow(csComponent *iParent, const char *iTitle, int iWindowStyle, 
						 csWindowFrameStyle iFrameStyle) 
	: csWindow(iParent, iTitle, iWindowStyle=CSWS_TITLEBAR | CSWS_BUTCLOSE | CSWS_BUTMAXIMIZE , 
				iFrameStyle=cswfs3D) {

		driver->Stop3D();
}

//close this window and start animation
void ModularWindow::Close() {	
	driver->Start3D();
	csWindow::Close();
}