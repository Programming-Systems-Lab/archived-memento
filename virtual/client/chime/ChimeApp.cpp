/*******************************************************************
    ChimeApp.cpp
	Author: Mark Galagan @ 2002

	Extends Crystal Space csApp as the container
	for the entire application. It takes care of
	directing events, controling windows, and creating
	menus.
********************************************************************/

#include "cssysdef.h"
#include "cstool/initapp.h"

#include "ChimeApp.h"
#include "ChimeSystemDriver.h"

//-----------------------------------------------------------------------------

// The global pointer to ChimeSystemDriver
ChimeSystemDriver	*driver;
ChimeApp			*application;

/****************************************************************************************
 * Constructor does nothing but construct the CS application
 ****************************************************************************************/
ChimeApp::ChimeApp (iObjectRegistry *object_reg, csSkin &skin) : csApp(object_reg, skin)
{

}

/***************************
 * Destructor does nothing
 ***************************/
ChimeApp::~ChimeApp ()
{
	delete driver;
	ShutDown ();
}

/****************************************************************************************
 * Start application
 ****************************************************************************************/
bool ChimeApp::StartApplication ()
{
	// Initialize environment
	if (!driver->LoginUser ())
	{
		driver->Report ("Chime.Application.ChimeApp", "Failed to initialize environment!");
		return false;
	}

	// Start application
	driver->Start ();
    return true;
}

/****************************************************************************************
 * Initialize the environment
 ****************************************************************************************/
bool ChimeApp::Initialize (iObjectRegistry *object_reg)
{
	if (!csInitializer::SetupConfigManager (object_reg, "lib/config/chime.cfg", "CHIME"))
	{
		driver->Report ("Chime.Application.ChimeApp", "Failed to initialize configuration!");
		return false;
	}

	if (!driver->Initialize ())
	{
		driver->Report ("Chime.Application.ChimeApp", "Failed to initialize system!");
		return false;
	}

	if (!csApp::Initialize ())
	{
		driver->Report ("Chime.Application.ChimeApp", "Failed to initialize application!");
		return false;
	}

	return true;
}


/*---------------------------------------------------------------------*
 * Main function
 *---------------------------------------------------------------------*/
CSWS_SKIN_DECLARE_DEFAULT (DefaultSkin);
int main (int argc, char* argv[])
{
  // Create object registry
  iObjectRegistry* object_reg = csInitializer::CreateEnvironment (argc, argv);

  // Create ChimeApp and ChimeSystemDriver instances
  application  = new ChimeApp (object_reg, DefaultSkin);
  driver = new ChimeSystemDriver (object_reg);
  driver->SetDebugMode (DEBUG);

  // Initialize application
  application->Initialize (object_reg);

  // Start application
  if (!application->StartApplication())
      return -1;

  // Shut down
  delete driver;
  application->ShutDown();

  return 0;
}