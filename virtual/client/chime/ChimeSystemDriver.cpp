/*******************************************************************
    ChimeSystemDriver.cpp
	Author: Mark Galagan @ 2002

	Main driver for the CHIME system. It creates the system,
	as well as plugins. It also holds together all the components
	of the system. Its run-time functions include listening
	to user events and respond, as well as manage network
	communication
********************************************************************/

//*** Crystal Space Includes ***//
#include "cssysdef.h"
#include "iutil/vfs.h"
#include "csutil/vfscache.h"
#include "iutil/plugin.h"
#include "csutil/cscolor.h"
#include "cstool/csview.h"
#include "cstool/initapp.h"
#include "iutil/eventq.h"
#include "iutil/event.h"
#include "iutil/objreg.h"
#include "iutil/csinput.h"
#include "iutil/virtclk.h"
#include "iengine/sector.h"
#include "iengine/engine.h"
#include "iengine/camera.h"
#include "iengine/light.h"
#include "iengine/statlght.h"
#include "iengine/texture.h"
#include "iengine/mesh.h"
#include "iengine/movable.h"
#include "iengine/material.h"
#include "imesh/thing/polygon.h"
#include "imesh/thing/thing.h"
#include "imesh/sprite3d.h"
#include "imesh/object.h"
#include "imesh/crossbld.h"
#include "imesh/mdlconv.h"
#include "imesh/mdldata.h"
#include "imesh/lighting.h"
#include "cstool/mdltool.h"
#include "ivideo/graph3d.h"
#include "ivideo/graph2d.h"
#include "ivideo/texture.h"
#include "ivideo/texture.h"
#include "ivideo/material.h"
#include "ivideo/fontserv.h"
#include "ivideo/natwin.h"
#include "igraphic/imageio.h"
#include "imap/parser.h"
#include "ivaria/reporter.h"
#include "ivaria/stdrep.h"
#include "ivaria/dynamics.h"
#include "csutil/cmdhelp.h"
#include "ivaria/collider.h"
#include "igeom/polymesh.h"
#include "cstool/collider.h"
#include "igeom/objmodel.h"
#include "isound/source.h"
#include "isound/listener.h"
#include "isound/renderer.h"
#include "isound/wrapper.h"

#include <windows.h>
#include <conio.h>

//*** Chime Includes***//
#include "ChimeSystemDriver.h"

CS_IMPLEMENT_APPLICATION

//-----------------------------------------------------------------------------

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

// The global pointer to ChimeApp
extern ChimeApp *application;

/*****************************************************************
 * Constructor: sets up the object registry
 *****************************************************************/
ChimeSystemDriver::ChimeSystemDriver (iObjectRegistry* object_reg)
{
  ChimeSystemDriver::csObjectRegistry = object_reg;

  // INITIALIZE MEMBER VARIABLES
  user_speed = 1.0;
  chNeighborQueue = new ChimeNeighbors ();
  chCurrentSector = NULL;
  chSelectedEntity = NULL;
  csEntityMenu = NULL;
  chModularWindow = NULL;
  isSystemReady = isEnvironmentReady = false;
  chView = NULL;
  chMapView = NULL;
  chVisibleObjects = new csVector ();
  chMenuFont = chSystemFont = chLabelFont = chLabelFontNum = chButtonFont = 0;
  doRedraw = false;
  isRunning = true;
  usingOpenGL = false;
  str2DMessage[0] = '\0';
  chHistoryWindow = NULL;
  chChatWindow = NULL;
  chAi2tvWindow = NULL;
  chAi2tvInterface = NULL;
  schemaID = 0;
}

/*****************************************************************
 * Destructor: call ExitSystem
 *****************************************************************/
ChimeSystemDriver::~ChimeSystemDriver ()
{
	ExitSystem ();
}

/*****************************************************************
 * Clean the system and exit application
 *****************************************************************/
void ChimeSystemDriver::ExitSystem ()
{
	delete application;
	csInitializer::DestroyApplication (csObjectRegistry);
}

/***********************************************************
 * Load a new sector given its name and Source.
 * New sector is filled with objects and users returned by
 * the server, but neighbors are only loaded with sector
 * definitions, void of active objects (those are only loaded
 * when the user enters).
 ***********************************************************/
ChimeSector* ChimeSystemDriver::LoadNewSector (char *strSectorName, char *strSectorSource,
											   csVector3 &iSectorOrigin, 
											   csVector3 &iSectorRotation, bool updateEngine)
{
	// first check if this sector is already in the queue
	ChimeSector *new_sector = chNeighborQueue->FindSector (strSectorName, strSectorSource);

	if (new_sector)
		return new_sector;

	// receive the data from the server
	// parse XML into structures
	// build empty sectors
	// fill this sector with objects and doors
	// attach sectors to outer doors
	new_sector = ChimeSector::SetupSector (iSectorOrigin, iSectorRotation, 
		"none", strSectorName, strSectorSource, 
		chView ? chView->GetCamera ()->GetSector () : NULL);
	chNeighborQueue->AddSector (new_sector);

	// Update engine if necessary
	if (updateEngine) UpdateEngine (new_sector->GetRegion ());

	// update history window
	char name[100], source[100];
	new_sector->GetSectorTitle (name, source);
	if (chHistoryWindow)
        chHistoryWindow->AddItem (name, source);

	return new_sector;
}

/***********************************************************
 * Creates a texture from file, using location 'source'
 * and name 'name'. It creates a material for the texture
 * and registers it with the engine.
 ***********************************************************/
bool ChimeSystemDriver::LoadTexture (char *name, char *source)
{
	iTextureWrapper* txt;
	try {
		txt = csLoader->LoadTexture (name,
		source, CS_TEXTURE_3D, csTxtManager, true);
	}
	catch (...) {printf("Error loading texture\n");}
	if (txt == NULL)
	{
		Report ("Chime.Application.ChimeSystemDriver",
    				"Error loading texture!");
		return false;
	}
	return true;
}


/***********************************************************
 * Loads new font into font system.
 * Based on the type, assign the font's index
 * to some pointer, e.g. system font index, etc.
 ***********************************************************/
bool ChimeSystemDriver::LoadFont (char *file, char *type)
{

	// If font is loaded, assiugn new index based on type
	if (csFontServer->LoadFont (file))
	{
		if (!strcmp (type, "label"))
		{
			chLabelFont = csFontServer->GetFontCount () - 1;
			chLabelFontNum++;
			return true;
		}

		if (!strcmp (type, "system"))
		{
			chSystemFont = csFontServer->GetFontCount () - 1;
			return true;
		}

		if (!strcmp (type, "menu"))
		{
			chMenuFont = csFontServer->GetFontCount () - 1;
			return true;
		}

		if (!strcmp (type, "button"))
		{
			chButtonFont = csFontServer->GetFontCount () - 1;
			return true;
		}

		return true;
	}

	return false;
}



/***********************************************************
 * Creates an object from file using given texture
 * and 3DS object file name.
 ***********************************************************/
bool ChimeSystemDriver::LoadObject (char *name, char *file, char *texture)
{
	// First check if the texture exists.
	iMaterialWrapper *material = csEngine->GetMaterialList()->FindByName(texture);
	if (!material)
	{
		Report ("Chime.Application.ChimeSystemDriver",
			"Can't find material in memory!");
		return false;
	}

	// Read in the model file
	csRef<iDataBuffer> buf = csVFS->ReadFile (file);
	if (buf == NULL)
	{
		Report ("Chime.Application.ChimeSystemDriver",
			"There was an error reading the data!");
		return false;
	}

	csRef<iModelData> model = csModelConverter->Load (buf->GetUint8 (), buf->GetSize ());
	if (!model)
	{
		Report ("Chime.Application.ChimeSystemDriver",
			"There was an error retrieving the model!");
		return false;
	}

	try {
	csModelDataTools::SplitObjectsByMaterial (model);
	csModelDataTools::MergeObjects (model, false);
	iMeshFactoryWrapper *mesh = csCrossBuilder->BuildSpriteFactoryHierarchy (model, csEngine, material);
	mesh->QueryObject ()->SetName (name);
	} catch (...) {printf("Could not create the mesh\n");}

	return true;
}


/*****************************************************************
 * Sets up the next frame by updating positions and views
 *****************************************************************/
void ChimeSystemDriver::SetupFrame ()
{
  // First get elapsed time from the virtual clock.
  csTicks elapsed_time = csVirtualClock->GetElapsedTicks ();

  // calculate frames per second
  static bool fpsstarted = false;
  static int numframes = 0;
  static int numtime = 0;
  static float fps = 0.0;
  if(!fpsstarted && (elapsed_time > 0))
  {fpsstarted = true; fps = 1. / (float(elapsed_time)*.001);}
  numtime += elapsed_time;
  numframes ++;
  if(numtime >= 1000)
  {
     fps = float(numframes) / (float(numtime)*.001);
     numtime = 0;
     numframes = 0;
  }
	
  // checkVertical is needed so as not to test for vertical
  // position twice, for being off the ground, and then moving
  bool checkVertical = true;
	
  // First, see if the user is on the ground, and if not
  // move him closer to it, simulating falling
  if (chUser && !isOnGround && isRunning) 
  {
	  chUser->MoveUser (csVector3 (0, 0, 0), fps, true);
	  checkVertical = false;
  }

  // Now rotate the camera according to keyboard state
  float speed = (elapsed_time / 1000.0) * (0.03 * 50);
  speed *= user_speed;

  // Keep track of user movement
  bool userMoved = false;
  
  //Update user's position according to keyboard event
  //Update map camera rotation with the user
  if (chUser && isRunning)
  {
      if (csKeyboardDriver->GetKeyState (CSKEY_RIGHT))
	  {
		  chUser->RotateUser(CS_VEC_ROT_RIGHT, speed);
		  chMapView->GetCamera ()->GetTransform ().RotateOther (CS_VEC_ROT_RIGHT, speed);
	  }
	  if (csKeyboardDriver->GetKeyState (CSKEY_LEFT))
	  {
          chUser->RotateUser(CS_VEC_ROT_LEFT, speed);
		  chMapView->GetCamera ()->GetTransform ().RotateOther (CS_VEC_ROT_LEFT, speed);
	  }
      if (csKeyboardDriver->GetKeyState (CSKEY_PGUP))
          chUser->RotateUser(CS_VEC_TILT_UP, speed);
      if (csKeyboardDriver->GetKeyState (CSKEY_PGDN))
          chUser->RotateUser(CS_VEC_TILT_DOWN, speed);
      if (csKeyboardDriver->GetKeyState (CSKEY_UP))
	  {
          chUser->MoveUser(CS_VEC_FORWARD * 4 * speed, fps, checkVertical);
		  userMoved = true;
	  }
      if (csKeyboardDriver->GetKeyState (CSKEY_DOWN))
	  {
          chUser->MoveUser(CS_VEC_BACKWARD * 4 * speed, fps, checkVertical);
		  userMoved = true;
	  }
  }

  // If user moved, update map camera's position
  if (userMoved)
  {
	  // set sector
	  if (chView->GetCamera ()->GetSector () != chMapView->GetCamera ()->GetSector ())
		  chMapView->GetCamera ()->SetSector (chView->GetCamera ()->GetSector ());

	  // set location (just keep vertical position)
	  csVector3 map_pos (chView->GetCamera ()->GetTransform ().GetOrigin ());
	  map_pos.y = chMapView->GetCamera ()->GetTransform ().GetOrigin ().y;
	  chMapView->GetCamera ()->GetTransform ().SetOrigin (map_pos);
  }

  // Tell 3D driver we're going to display 3D things.
  if (!csGraphics3D->BeginDraw (csEngine->GetBeginDrawFlags () | CSDRAW_3DGRAPHICS))
    return;

  // Clear background id the flag is set
  if (doRedraw || usingOpenGL) csGraphics2D->Clear (csGraphics2D->FindRGB (125, 125, 125));
  
  // Tell the views to render into the frame buffer.
  if (chView)
      chView->Draw ();
  if (chMapView)
      chMapView->Draw();

  // Start drawing 2D graphics.
  if (!csGraphics3D->BeginDraw (CSDRAW_2DGRAPHICS)) return;

  // Draw labels for active entities
  DrawLabels ();

  if (str2DMessage[0] != '\0')
  {
	  int x = chView->GetWindowRectangle ()->Width ();
	  int y = chView->GetWindowRectangle ()->Height () / 2;
	  int msg_len = strlen (str2DMessage);
	  x = (x - msg_len)/2;
	  chView->GetWindow ()->LocalToGlobal (x, y);
	  csGraphics2D->Write (GetFont (chSystemFont), x, y, csGraphics2D->FindRGB (200, 0, 20), -1, str2DMessage);
  }

  // Draw "frames per second" inside main view
  if (chView && chDebugMode == DEBUG)
  {
      char buf[50];
      sprintf (buf, "FPS: %g", fps);
      int x = 10, y = 30;
      chView->GetWindow()->LocalToGlobal (x, y);
      csGraphics2D->DrawBox (x-5, y-5, 100, 30, csGraphics2D->FindRGB (0, 0, 0));
      csGraphics2D->DrawBox (x-3, y-3, 96, 26, csGraphics2D->FindRGB (255, 255, 255));
      csGraphics2D->Write (GetFont (chSystemFont), x, y, csGraphics2D->FindRGB (200, 0, 20), -1, buf);
  }
}


/*****************************************************************
 * Finishes drawing a frame
 *****************************************************************/
void ChimeSystemDriver::FinishFrame ()
{
    // Finish drawing
	csGraphics3D->FinishDraw ();
    csGraphics3D->Print (NULL);

	// Redraw windows
	if (doRedraw || usingOpenGL)
	{
        if (chChatWindow) chChatWindow->Invalidate (true);
        if (chHistoryWindow) chHistoryWindow->Invalidate (true);
		if (chAi2tvWindow) chAi2tvWindow->Invalidate (true);
        if (chView) chView->GetWindow ()->Invalidate (false);
        if (chMapView) chMapView->GetWindow ()->Invalidate (false);
		doRedraw = false;
	}

	// If there is an open menu, draw it on top
	// of everything else
	if (csEntityMenu && csEntityMenu->Select ())
		csEntityMenu->Invalidate (true, NULL);

	// If there is an open modular window,
	// draw it on top
	if (chModularWindow && chModularWindow->Select ()) 
		chModularWindow->Invalidate (true, NULL);

	// If system is ready, draw 2D windows
	if (isSystemReady)
	{
		SetupWindows ();
		isSystemReady = false;
	}

	// If environment is ready, setup 3D environment
	if (isEnvironmentReady)
	{
		InitializeEnvironment ();
		isEnvironmentReady = false;
	}
}

/*****************************************************************
 * Event handling system. Coordinates user events.
 *****************************************************************/
bool ChimeSystemDriver::HandleEvent (iEvent& event)
{
	// First, let the application handle its events
	application->HandleEvent (event);

	// If the event refers to window, other than main 3D view,
	// let them handle it
	HandleEventFromOtherWindows (event);

	// If there is an open menu, let it handle the event
	HandleMenuEvent (event);
	
	// Handle the event based on its type
	switch (event.Type)
	{
        // Handle a broadcast event
		case csevBroadcast:
			switch (event.Command.Code)
			{
                case cscmdProcess:
                    driver->SetupFrame ();
                    break;
                case cscmdFinalProcess:
                    driver->FinishFrame ();
                    break;
			}
			break;

        // For a left mouse button double click...
        case csevMouseDoubleClick:
            HandleLeftMouseDoubleClick(event);
            break;

		// For a mouse button click...
        case csevMouseDown:
            // Right button...
			if(event.Mouse.Button == 2)
			{
                HandleRightMouseClick(event);
			}
			// Left button...
            else if(event.Mouse.Button == 1)
			{
                HandleLeftMouseClick(event);
			}
            break;

		// For mouse button release...
        case csevMouseUp:
            if(event.Mouse.Button == 1)
			{
                chSelectedEntity = NULL;
			}
            break;

		// For a mouse move...
        case csevMouseMove:
            HandleMouseMove (event);
            break;

		// For a key event...
		case csevKeyDown:
			HandleKeyEvent (event);
			break;
	}

	return true;
}

/*****************************************************************
 * Event handler function used by CS to handle user events
 *****************************************************************/
bool ChimeSystemDriver::EventHandler (iEvent& ev)
{
  return driver->HandleEvent (ev);
}

/*****************************************************************
 * Setup CHIME windows
 *****************************************************************/
bool ChimeSystemDriver::SetupWindows ()
{
  // history window	
  chHistoryWindow = new ChimeHistoryWindow (application);
  chHistoryWindow->SetFont (GetFont (chSystemFont));

  // chat window
  chChatWindow = new ChimeChatWindow (application);
  chChatWindow->SetFont (GetFont (chSystemFont));

  return true;
}

/*****************************************************************
 * Create a user with given parameters
 *****************************************************************/
void ChimeSystemDriver::CreateUser (const char *strUserName, const char *strUserPassword,
                                    const char *strUserSource, const char *strUserID, 
									const char *strGroupID)
{
	// Create new user
	chUser = new ChimeUser (strUserName, strUserPassword, strUserSource,
		strUserID, strGroupID);
	chUser->PrintUserParameters ();

	// Tell system is ready
	PrepareSystem ();
}

/*****************************************************************
 * Creates the initial room for user to drop into
 *****************************************************************/
bool ChimeSystemDriver::ReadInitialSector ()
{
  // Set up the CHIME sector
  csVector3 origin (0, 0, -10), rot (0, 0, 0);
  SetCurrentSector (LoadNewSector ("Yahoo! Main", "http://www.yahoo.com", origin, rot, false));

  //------------------ Set up 3D views of this system --------------------------------//

  // Main view
  csWindow *w = new csWindow (application, "3D View", 0, cswfsThin);
  w->SetTitlebarHeight (0);
  w->SetFont (GetFont (chSystemFont));
  w->SetRect (application->bound.Width()/4, 0, application->bound.Width()-1, application->bound.Height()*2/3);
  chView = new ChimeEngineView (w, csEngine, chCurrentSector->GetDefaultRoom(), chCurrentSector->GetDefaultLocation(), csGraphics3D);

  // Map view
  w = new csWindow (application, "Map View", 0, cswfsThin);
  w->SetTitlebarHeight (0);
  w->SetFont (GetFont (chSystemFont));
  w->SetRect (application->bound.Width()/4, application->bound.Height()*2/3 + 2, application->bound.Width()-1, application->bound.Height()-1);
  chMapView = new ChimeEngineView (w, csEngine, chCurrentSector->GetDefaultRoom(), chCurrentSector->GetDefaultLocation(), csGraphics3D);
  chMapView->GetCamera ()->GetTransform ().SetOrigin (chMapView->GetCamera ()->GetTransform ().GetOrigin () + csVector3 (0, 5, 0));
  chMapView->GetCamera ()->GetTransform ().RotateThis (CS_VEC_TILT_DOWN, 1.57);

  return true;
}

/*****************************************************************
 * Sets up the graphics system
 *****************************************************************/
bool ChimeSystemDriver::Initialize ()
{
	
  if (!csInitializer::RequestPlugins (csObjectRegistry,
  	CS_REQUEST_VFS,
	CS_REQUEST_SOFTWARE3D,
	CS_REQUEST_ENGINE,
	CS_REQUEST_FONTSERVER,
	CS_REQUEST_IMAGELOADER,
	CS_REQUEST_LEVELLOADER,
	CS_REQUEST_REPORTER,
	CS_REQUEST_REPORTERLISTENER,
	CS_REQUEST_PLUGIN ("crystalspace.modelconverter.3ds", iModelConverter),
	CS_REQUEST_PLUGIN("crystalspace.mesh.crossbuilder", iCrossBuilder),
	CS_REQUEST_PLUGIN("crystalspace.collisiondetection.rapid", iCollideSystem),
	CS_REQUEST_PLUGIN("crystalspace.sound.render.software", iSoundRender),
	CS_REQUEST_END))
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"Can't initialize plugins!");
    return false;
  }

  // Assign event handler function
  if (!csInitializer::SetupEventHandler (csObjectRegistry, EventHandler))
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"Can't initialize event handler!");
    return false;
  }

  // Check for commandline help.
  if (csCommandLineHelper::CheckHelp (csObjectRegistry))
  {
    csCommandLineHelper::Help (csObjectRegistry);
    return false;
  }

  // The plug-in panager
  csPluginManager = CS_QUERY_REGISTRY (csObjectRegistry, iPluginManager);
  if (csPluginManager == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"Can't find the plug-in manager!");
    return false;
  }

  // The virtual clock.
  csVirtualClock = CS_QUERY_REGISTRY (csObjectRegistry, iVirtualClock);
  if (csVirtualClock == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"Can't find the virtual clock!");
    return false;
  }

  // Find the pointer to engine plugin
  csEngine = CS_QUERY_REGISTRY (csObjectRegistry, iEngine);
  if (csEngine == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"No iEngine plugin!");
    return false;
  }

  // Set up the loader plugin
  csLoader = CS_QUERY_REGISTRY (csObjectRegistry, iLoader);
  if (csLoader == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
    		"No iLoader plugin!");
    return false;
  }

  csVFS = CS_QUERY_REGISTRY (csObjectRegistry, iVFS);
  if (!csVFS)
  {
	Report ("Chime.Application.ChimeSystemDriver",
    		"No iVFS plugin!");
	return false;
  }

  // Get the configuration manager
  csRef<iConfigManager> csConfigMgr = CS_QUERY_REGISTRY (csObjectRegistry, iConfigManager);
  if (!csConfigMgr)
  {
	  Report ("Chime.Application.ChimeSystemDriver",
    		"No iConfigManager plugin!");
      return false;
  }

  // Set up the collider plugin
  csCollisionSystem = CS_QUERY_REGISTRY (csObjectRegistry, iCollideSystem);
  if (csCollisionSystem == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
    		"No iCollideSystem plugin!");
    return false;
  }

  // Set up the 3D graphics plugin
  csGraphics3D = CS_QUERY_REGISTRY (csObjectRegistry, iGraphics3D);
  if (csGraphics3D == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"No iGraphics3D plugin!");
    return false;
  }
  csGraphics3D->GetDriver2D ()->DoubleBuffer (false);
  
  // Set up the 2D graphics plugin
  csGraphics2D = csGraphics3D->GetDriver2D ();
  if (csGraphics2D == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
			"No iGraphics2D plugin!");
    return false;
  }

  // Keyboard driver handle
  csKeyboardDriver = CS_QUERY_REGISTRY (csObjectRegistry, iKeyboardDriver);
  if (csKeyboardDriver == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
    		"No iKeyboardDriver plugin!");
    return false;
  }

  // Find the model converter plugin
  csModelConverter = CS_QUERY_REGISTRY (csObjectRegistry, iModelConverter);
  if (!csModelConverter)
  {
	Report ("Chime.Application.ChimeSystemDriver",
    		"No iModelConverter plugin!");
	return false;
  }

  // Find the model crossbuilder plugin
  csCrossBuilder = CS_QUERY_REGISTRY (csObjectRegistry, iCrossBuilder);
  if (!csCrossBuilder)
  {
	Report ("Chime.Application.ChimeSystemDriver",
    		"No iCrossBuilder plugin!");
	return false;
  }

  // Open the main system. This will open all the previously loaded plug-ins.
  iNativeWindow* nw = csGraphics2D->GetNativeWindow ();
  if (nw) nw->SetTitle ("CHIME 3D Client");
  if (!csInitializer::OpenApplication (csObjectRegistry))
  {
    Report ("Chime.Application.ChimeSystemDriver",
    		"Error opening system!");
    return false;
  }

  // Set up the texture manager
  csTxtManager = csGraphics3D->GetTextureManager ();
  if (csTxtManager == NULL)
  {
    Report ("Chime.Application.ChimeSystemDriver",
    		"No iTextureManager plugin!");
    return false;
  }

  // Set up reporter output
  csRef<iStandardReporterListener> stdReporterListener = CS_QUERY_REGISTRY (csObjectRegistry, iStandardReporterListener);
  if (stdReporterListener)
  {
	  stdReporterListener->SetMessageDestination (CS_REPORTER_SEVERITY_ERROR,
	  true, false, true, false, true);
	  stdReporterListener->SetMessageDestination (CS_REPORTER_SEVERITY_BUG,
	  true, false, true, false, true);
	  stdReporterListener->SetMessageDestination (CS_REPORTER_SEVERITY_WARNING,
	  false, false, false, false, false);
	  stdReporterListener->SetMessageDestination (CS_REPORTER_SEVERITY_NOTIFY,
	  false, false, false, false, false);
	  stdReporterListener->SetMessageDestination (CS_REPORTER_SEVERITY_DEBUG,
	  true, false, true, false, true);
  }

  // Get font server
  csFontServer = CS_QUERY_REGISTRY (csObjectRegistry, iFontServer);

  //if we are in openGL mode, allow moving the AWS window
  if (!strcmp(csConfigMgr->GetStr("System.Plugins.iGraphics3D"), 
      "crystalspace.graphics3d.opengl"))
      usingOpenGL = true;

  // Load objects for CHIME library
  LoadLibraries ("libraries.cfg");

  //Initialize collision system
  chCollider = new ChimeCollider();

  //Tell the engine to recalculate the Z-Buffer every frame
  csEngine->SetClearZBuf (true);

  //Prepare engine
  csEngine->Prepare ();

  return true;
}


/************************************************************************
 * Initialize default envirnoment (setup user, load initial sector, etc.
 ************************************************************************/
bool ChimeSystemDriver::InitializeEnvironment ()
{
  // Build the initial CHIME environment //
  if (!ReadInitialSector())
  {
	Report ("Chime.Application.ChimeSystemDriver", "Could not create initial room for this user!");
    return false;
  }

  // Create CHIME user //
  isOnGround = false;
  if (!chUser)
      chUser = new ChimeUser();
  if (!chUser->SetupUser(NULL, chView->GetCamera()))
  {
	Report ("Chime.Application.ChimeSystemDriver", "Could not create this user!");
    return false;
  }
  
  // Place user
  chUser->PlaceUser(chCurrentSector->GetDefaultLocation(), chCurrentSector->GetDefaultRoom());

  // Create colliders, now that user is placed
  driver ->GetCollider ()->CreateUserCollider ();

  //Redraw windows
  Redraw ();

  return true;
}


/************************************************************
 * Load object templates from CHIME library definition file
 ************************************************************/
bool ChimeSystemDriver::LoadLibraries (char *libname)
{
	printf("Loading libraries...\n");
	
	//Prepare texture manager
	csTxtManager->SetVerbose (false);

	//Read library from the file
	FILE *fp = fopen(libname, "r");
	if(!fp) {
		Report ("Chime.Application.ChimeSystemDriver",
			"Could not load CHIME library file");
		return false;
	}
	int bufSize = 100;
	char buf[100];
	char command[100];
	char err[200];

	fgets(buf, bufSize, fp);
	sscanf(buf, "%s", command);

	while( !feof(fp) && strcmp(command, "END"))
	{
		if(command[0] == ';')
		{//Skip comments
		}
		else if(!strcmp(command, "TEXT"))
		{//Read texture
			char name[100], file[100];
			sscanf(buf, "%s %s %s", command, name, file);
			if(!LoadTexture (name, file))
			{
				strcpy(err,"Error Loading Texture: ");
				strcat(err, name);
				Report ("Chime.Application.ChimeSystemDriver", err);
			}
		}
		else if(!strcmp(command, "OBJ"))
		{//Read 3D object
			char name[100], file[100], text[100];
			sscanf(buf, "%s %s %s %s", command, name, file, text);
			if(!LoadObject (name, file, text))
			{
				strcpy(err,"Error Loading Object: ");
				strcat(err, name);
				Report ("Chime.Application.ChimeSystemDriver", err);
			}
		}
		else if(!strcmp(command, "FONT"))
		{//Read a new font
			char file[100], type[50];
			sscanf(buf, "%s %s %s", command, file, type);
			if(!LoadFont (file, type))
			{
				strcpy(err,"Error Loading Font: ");
				strcat(err, file);
				Report ("Chime.Application.ChimeSystemDriver", err);
			}
		}


		fgets(buf, bufSize, fp);
		sscanf(buf, "%s", command);
	}

	csTxtManager->PrepareMaterials();

	fclose(fp);
	printf("Done\n");

	return true;
}


/************************************************************
 * Handle a left-button mouse single click
 ************************************************************/
bool ChimeSystemDriver::HandleLeftMouseClick (iEvent &Event)
{
	// See if a mesh was selected
	chSelectedEntity = SelectEntity (Event.Mouse.x, Event.Mouse.y);

	// Close csEntityMenu if one was opened before
	CloseMenu ();
	
	// If an entity was selected, perform the appropriate action
	if (chSelectedEntity)
	{
		chSelectedEntity->HandleLeftMouseClick(Event);
	}
	return true;
}

/************************************************************
 * Handle a left-button mouse double click
 ************************************************************/
bool ChimeSystemDriver::HandleLeftMouseDoubleClick(iEvent &Event)
{
	// See if a mesh was selected
	chSelectedEntity = SelectEntity (Event.Mouse.x, Event.Mouse.y);

	// Close csEntityMenu if one was opened before
	CloseMenu ();
	
	// If an entity was selected, perform the appropriate action
	if (chSelectedEntity)
	{
		chSelectedEntity->HandleLeftMouseDoubleClick(Event);
	}
	return true;
}

/************************************************************
 * Handle a right-button mouse single click
 ************************************************************/
bool ChimeSystemDriver::HandleRightMouseClick(iEvent &Event)
{
	// See if a mesh was selected
	chSelectedEntity = SelectEntity (Event.Mouse.x, Event.Mouse.y);

	// Close csEntityMenu if one was opened before
	CloseMenu ();
	
	// If an entity was selected, perform the appropriate action
	if (chSelectedEntity)
	{
		chSelectedEntity->HandleRightMouseClick(Event);
	}
	return true;
}

/************************************************************
 * Handle a right-button mouse double click
 ************************************************************/
bool ChimeSystemDriver::HandleRightMouseDoubleClick(iEvent &Event)
{
	// See if a mesh was selected
	chSelectedEntity = SelectEntity (Event.Mouse.x, Event.Mouse.y);

	// Close csEntityMenu if one was opened before
	CloseMenu ();
	
	// If an entity was selected, perform the appropriate action
	if (chSelectedEntity)
	{
		chSelectedEntity->HandleRightMouseDoubleClick (Event);
	}
	return true;
}


/************************************************************
 * Move selected mesh to new location pointed by mouse
 ************************************************************/
bool ChimeSystemDriver::HandleMouseMove (iEvent &Event)
{
	if (chSelectedEntity && csEntityMenu)
		return false;

	if (!chSelectedEntity)
		return true;
	
	csVector2 p (Event.Mouse.x, application->bound.Height()-Event.Mouse.y);
	
	chSelectedEntity->HandleMouseMove (csLastMousePosition, p, chView->GetCamera ());

	csLastMousePosition.Set (p);

	return true;
}


/************************************************************
 * See if any other windows want to handle this event
 ************************************************************/
bool ChimeSystemDriver::HandleEventFromOtherWindows (iEvent &Event)
{
	if (chHistoryWindow && chHistoryWindow->HandleEvent (Event))
		return true;

	if (chChatWindow && chChatWindow->HandleEvent (Event))
		return true;

	if (chAi2tvWindow && chAi2tvWindow->HandleEvent (Event))
		return true;

	return false;
}


/************************************************************
 * Handle a 'key-pressed' event
 ************************************************************/
bool ChimeSystemDriver::HandleKeyEvent (iEvent &Event)
{
	switch (Event.Key.Code)
	{
        // Key '+'...
        case ('='):
			if (chView)
			{
                if (user_speed < 2)
                    user_speed += 0.1;
			}
            break;

		// Key '-'...
        case ('-'):
			if (chView)
			{
                if (user_speed > 0.5)
                    user_speed -= 0.1;
			}
            break;

		// Key 'g'...
        case ('g'):
			schemaID = 1;
            break;
		
		// Key 's'...
		case ('s'):
			if (chView && chCurrentSector && 
				Event.Key.Code == 115 && Event.Key.Char == 19)
			{
				// Setup Ai2TV interface for this user
				chAi2tvInterface = ChimeAi2tvInterface::GetInstance (chUser->GetUserName ());
				if (chAi2tvInterface)
				{
                    char sources [10][50];
					chAi2tvInterface->GetAvailableVideos (sources);
                    new Ai2tvSourceSelectWindow (application, sources, 5);
				}
			}
			break;

		// Key 'f'...
        case ('f'):
			if (chView && chCurrentSector)
			{
                DisplayFrame ("woodfloor.jpg");
			}
            break;

		// Key 'p'...
        case ('p'):
			if (chHistoryWindow || chChatWindow)
			{
                PrepareEnvironment ();
			}
            break;

		// Key 'd'...
        case ('d'):
            if (chCurrentSector && chView)
			{
				csStrVector pn (6); csVector p (6);
				pn.Push ("origin"); p.Push (new csVector3 
					(chView->GetCamera ()->GetTransform ().GetOrigin () - 
					csVector3 (0, 1.5, 0)));
				char room_name[50]; 
				strcpy (room_name, chCurrentSector->GetCurrentRoom ()->QueryObject ()->GetName ());
				printf("Adding door to room: %s\n", room_name);
				pn.Push ("room"); p.Push (room_name);
				pn.Push ("target_name"); p.Push ("Yahoo! Images");
				pn.Push ("target_source"); p.Push ("www.yahoo.com/images");
				pn.Push ("door_texture"); p.Push ("door_text");
				pn.Push ("texture_size"); p.Push (new csVector3 (2, 3, 1));
				chCurrentSector->AddEntity ("door1", ENTITY_TYPE_DOOR, pn, p);
				pn.SetLength (0); p.SetLength (0);
			}
            break;

		// Key 'o'...
        case ('o'):
            if (chCurrentSector && chView)
			{
				csStrVector pn (5); csVector p (5);
				pn.Push ("origin"); p.Push (new csVector3 
					(chView->GetCamera ()->GetTransform ().GetOrigin () - 
					csVector3 (1, 0, 1)));
				char room_name[50]; 
				strcpy (room_name, chCurrentSector->GetCurrentRoom ()->QueryObject ()->GetName ());
				printf("Adding object to room: %s\n", room_name);
				pn.Push ("room"); p.Push (room_name);
				pn.Push ("object_source"); p.Push ("www.yahoo.com/images/logo.jpeg");
				pn.Push ("object_model"); p.Push ("user");
				pn.Push ("object_material"); p.Push ("user_text");
				if (chCurrentSector->AddEntity ("Yahoo! Logo in JPEG", ENTITY_TYPE_USER, pn, p) && chChatWindow)
					chChatWindow->AddItem ("Yahoo! Logo in JPEG", "www.yahoo.com/images/logo.jpeg");
				pn.SetLength (0); p.SetLength (0);
			}
            break;
	}

	return true;
}


/************************************************************
 * Handle and event from an open menu
 ************************************************************/
bool ChimeSystemDriver::HandleMenuEvent (iEvent &Event)
{
	// see if a menu is open
	if (!csEntityMenu || !chSelectedEntity)
		return false;

	// pass event code to selected entity
	printf("Command code: %d\n", Event.Command.Code);
	printf("Command key: %d\n", Event.Command.Info);
	return chSelectedEntity->HandleMenuEvent (Event.Command.Code);
}


/************************************************************
 * Build AI2TV screen at current user location
 ************************************************************/
bool ChimeSystemDriver::BuildAi2tvScreen ()
{
	if (!chCurrentSector)
		return false;

    // build a screen
	chCurrentSector->BuildScreen (chView->GetCamera ()->GetTransform ());
	DisplayFrame ("ai2tv_loading");

	// create ai2tv window
    chAi2tvWindow = new ChimeAi2tvWindow (application);
    chAi2tvWindow->SetFont (GetFont (chSystemFont));
	return true;
}


/************************************************************
 * Load given image under given frame name
 ************************************************************/
void ChimeSystemDriver::LoadFrame (char *strFileName, char *strMaterialName)
{
	// add AI2TV folder location to file name
	char full_name[100];
	strcpy (full_name, "lib/ai2tv/");
	strcat (full_name, strFileName);

	// create texture
	iTextureWrapper *texture = csLoader->LoadTexture (strMaterialName, 
		full_name, CS_TEXTURE_3D, csTxtManager, true);
	if (!texture)
		return;

	// prepare texture
	texture->Register (csTxtManager);
	texture->SetImageFile (NULL);

	// create material
	iMaterialWrapper *material = csEngine->CreateMaterial (strMaterialName, texture);
	if (!material)
		return;
	material->Register (csTxtManager);
}


/************************************************************
 * Display given frame on screen
 ************************************************************/
void ChimeSystemDriver::DisplayFrame (char *strFrameName)
{
	// find material
	iMaterialWrapper *material = csEngine->FindMaterial (strFrameName);

	// display material on screen
	if (material && chCurrentSector)
		chCurrentSector->DisplayImageOnScreen (material);
}


/************************************************************
 * Returns a newly created menu
 ************************************************************/
csMenu* ChimeSystemDriver::CreateMenu (int x, int y)
{
	CloseMenu ();
	csEntityMenu = new csMenu (application, csmfs3D, 0);
	csEntityMenu->SetFont (GetFont (chMenuFont));
	Stop3D ();

	return csEntityMenu;
}

/************************************************************
 * Close csEntityMenu if one is open
 ************************************************************/
bool ChimeSystemDriver::CloseMenu ()
{
	if (csEntityMenu)
	{
		try
		{
			csEntityMenu->Close ();
			csEntityMenu = NULL;
			Start3D ();
		}
		catch (...) {return false;}
	}
	return true;
}

/************************************************************
 * Select a sector entity that corresponds to given
 * screen coordinates
 ************************************************************/
ChimeSectorEntity* ChimeSystemDriver::SelectEntity (float x, float y)
{
	if (!chView && !chCurrentSector)
		return false;

	// Find the point on the creen where the user clicked
	csVector2 screenCoord (x, application->bound.Height() - y - 1);
	csLastMousePosition.Set (screenCoord);

	csVector3 v;
	chView->GetCamera ()->InvPerspective (screenCoord, 1, v);
	
	csVector3 vw = chView->GetCamera()->GetTransform().This2Other(v);
	csVector3 origin = chView->GetCamera()->GetTransform().GetO2TTranslation();
	csVector3 isect;

	return chCurrentSector->SelectEntity (origin, origin + (vw-origin) * 20, isect);
}

/************************************************************
 * Print error reports to standard output
 ************************************************************/
void ChimeSystemDriver::Report(char* source, char* message)
{
	if (chDebugMode == DEBUG)
		csReport (csObjectRegistry, CS_REPORTER_SEVERITY_ERROR, source,	message);
	else
		printf("CHIME encountered a problem. Attempting to continue...\n");
}

/***********************************************************
 * Start running the 3D rendering
 ***********************************************************/
void ChimeSystemDriver::Start ()
{
	csDefaultRunLoop (csObjectRegistry);
}

/***********************************************************
 * Set debugging mode for this run
 * Mode DEBUG means outputting debugging messages
 * Mode NODEBUG means outputting only brief messages for
 * the casual user
 ***********************************************************/
void ChimeSystemDriver::SetDebugMode (int mode)
{
  chDebugMode = mode;
}


/***********************************************************
 * Returns object registry
 ***********************************************************/
iObjectRegistry* ChimeSystemDriver::GetObjectRegistry ()
{
  return csObjectRegistry;
}

/***********************************************************
 * Returns collision detector
 ***********************************************************/
ChimeCollider* ChimeSystemDriver::GetCollider ()
{
  return chCollider;
}

/*****************************************************************
 * Returns the 2D coordinates of mouse's last position on screen
 *****************************************************************/
csVector2 ChimeSystemDriver::GetLastMousePosition ()
{
  return csLastMousePosition;
}

/***********************************************************
 * Returns current CHIME sector
 ***********************************************************/
ChimeSector* ChimeSystemDriver::GetCurrentSector ()
{
  return chCurrentSector;
}

/***********************************************************
 * Returns current AI2TV interface
 ***********************************************************/
ChimeAi2tvInterface* ChimeSystemDriver::GetAi2tvInterface ()
{
	return chAi2tvInterface;
}


/*********************************************************************
 * Returns ChimeSector that contains given room
 *********************************************************************/
ChimeSector* ChimeSystemDriver::FindSectorByRoom (iSector* room)
{
	return chNeighborQueue->FindSector (room);
}


/*********************************************************************
 * Returns ChimeSector that has given title
 *********************************************************************/
ChimeSector* ChimeSystemDriver::FindSectorByTitle (char *strSectorName, 
                                                   char *strSectorSource)
{
	return chNeighborQueue->FindSector (strSectorName, strSectorSource);
}


/***********************************************************
 * Select from the list of active objects inside the
 * current sector only those whose labels are visible inside
 * the current view and are not further than
 * MAX_VISIBILITY_DISTANCE
 ***********************************************************/
void ChimeSystemDriver::SelectVisibleObjects (csVector* iEntityList, csVector *iVisibleObjectList)
{
	// First, clear the old visible entities list
	iVisibleObjectList->DeleteAll ();

	// Initialize variables
	ChimeSectorObject *object = NULL;
	csOrthoTransform cam_trans = chView->GetCamera ()->GetTransform ();
	csVector3 screen_pos1, screen_pos2;
	int i = 0, j = 0;

	// Go through the whole list of active entities
	for (i = 0; i < iEntityList->Length (); i++)
	{
		// if entity is a door, skip it
		if (!((ChimeSectorEntity*) iEntityList->Get (i))->HasLabel ())
			continue;

		// otherwise, assign the object pointer
		object = (ChimeSectorObject*) iEntityList->Get (i);

		// Find the screen coordinates for the center of the entity's label
		screen_pos1 = cam_trans.Other2This (*object->GetObjectLabelCenter ());

		// If the label is further than MAX_VISIBILITY_DISTANCE,
		// discard it as invisible
		if (screen_pos1.Norm () > MAX_VISIBILITY_DISTANCE)
			continue;

		// If the entity itself is visible...
		if (object && object->IsObjectVisible (chView->GetCamera (), *chView->GetWindowRectangle ()))
		{
			// Sort through the selected entities to insert
			// this entity according to its Z distance from the camera.
			// This ensures that entities that are closer have their
			// labels drawn over those that are further away.
			for (j = 0; j < iVisibleObjectList->Length (); j++)
			{
				screen_pos2 = cam_trans.Other2This ( *(((ChimeSectorObject*)iVisibleObjectList->Get (j))->GetObjectLabelCenter ()));
				if (screen_pos2.z < screen_pos1.z)
					break;
			}
			iVisibleObjectList->Insert (j, object);
		}
	}
}


/***********************************************************
 * Draw 2D labels for the entities that are visible
 * inside the current camera ciew
 ***********************************************************/
void ChimeSystemDriver::DrawLabels ()
{
	if (!chCurrentSector)
		return;

    // Get visible entities
    SelectVisibleObjects (chCurrentSector->GetActiveEntities (), chVisibleObjects);

    // Initialize some variables
    ChimeSectorObject *visibleObject = NULL;
    char *objectLabel = NULL, labelText[100];
    csVector3 *objectLabelCenter = NULL, camera_pos;
    int fontWidth, fontHeight, fontIndex, labelWidth;
	int xmin, ymin, left, right, xborder = chView->GetWindowRectangle ()->xmin + 1;

	// For each visible entity...
    for (int i = 0; i < chVisibleObjects->Length (); i++)
	{
        // Get the entity's label text and center
		visibleObject = (ChimeSectorObject*) chVisibleObjects->Get (i);
        objectLabel = visibleObject->GetObjectLabel ();
        objectLabelCenter = visibleObject->GetObjectLabelCenter ();

		// If both exist...
        if (objectLabel && objectLabelCenter)
		{
			// Calculate the center of the label in the
			// screen coordinates
            csVector2 *screen_point  = new csVector2 ();
            csVector3 camera_pos = chView->GetCamera ()->GetTransform ().Other2This (*objectLabelCenter);
            chView->GetCamera()->Perspective (camera_pos, *screen_point);

			// Select appropriate font for calculated object Z-distance
			fontIndex = (int)camera_pos.z / (MAX_VISIBILITY_DISTANCE / chLabelFontNum);
			fontIndex = MIN (chLabelFontNum - 1, fontIndex);
			fontIndex = MAX (0, chLabelFont - fontIndex);
			csRef<iFont> labelFont = NULL;
			labelFont = GetFont (fontIndex);

			// Get width and height of an average font letter
			labelFont->GetGlyphSize ('N', fontWidth, fontHeight);

			// See if the label text will fit vertically
			ymin = csGraphics2D->GetHeight () - screen_point->y - fontHeight/2 - 2;
			if (ymin <= csGraphics2D->GetHeight () - chView->GetWindowRectangle ()->ymax ||
				ymin + fontHeight + 4 >= csGraphics2D->GetHeight () - chView->GetWindowRectangle ()->ymin)
				continue;

			// Select a part of the label text that will fit horizontally
			labelWidth = strlen (objectLabel);
			left = 0; right = labelWidth - 1;

			// Find the index of the rightmost letter that will fit
			xmin = screen_point->x + (labelWidth * fontWidth)/2;
			while (xmin > chView->GetWindowRectangle ()->xmax - 1)
			{
				right--;
				xmin -= fontWidth;
			}

			// Find the index of the leftmost letter that will fit
			xmin = screen_point->x - (labelWidth * fontWidth)/2;
			while (xmin < chView->GetWindowRectangle ()->xmin + 1)
			{
				left++;
				xmin += fontWidth;
			}
			
			// If no letters fit, don't draw anything
			if (right - left <= 0)
				continue;

			// Copy the new string that will fit
			labelWidth = right - left + 1;
			//labelText = (char*) malloc ((labelWidth+1) * sizeof (char));
			for (int i = 0; i < labelWidth; i++)
				labelText[i] = objectLabel[left + i];
			labelText[labelWidth] = '\0';

			// Draw the backgrounf for label text
			csGraphics2D->DrawBox (xmin-1, ymin-2, labelWidth*fontWidth, 
				fontHeight + 2, csGraphics2D->FindRGB (255, 0, 0));
			csGraphics2D->DrawBox (xmin, ymin-1, labelWidth*fontWidth-2, 
				fontHeight, csGraphics2D->FindRGB (255, 255, 255));

			// Select a color
			int labelColor = csGraphics2D->FindRGB (0, 0, 0);
			
			// Draw the text
            csGraphics2D->Write (labelFont, xmin+5, ymin, labelColor, -1, labelText);
		}
	}
}

/************************************************************
 * Start running 3D animation
 ************************************************************/
void ChimeSystemDriver::Start3D ()
{
	isRunning = true;
}

/************************************************************
 * Stop running 3D animation
 ************************************************************/
void ChimeSystemDriver::Stop3D ()
{
	isRunning = false;
}

/************************************************************
 * Displays given text message as 2D message on top of the
 * 3D screen
 ************************************************************/
void ChimeSystemDriver::Display2DMessage (char *strMessage)
{
	Stop3D ();
	strcpy (str2DMessage, strMessage);
}

/************************************************************
 * Deletes existing 2D message from screen
 ************************************************************/
void ChimeSystemDriver::Delete2DMessage ()
{
	str2DMessage[0] = '\0';
	Start3D ();
}

/************************************************************
 * Return font for given font type
 ************************************************************/
iFont* ChimeSystemDriver::GetFont (int iFontType)
{
	return csFontServer->GetFont (iFontType);
}

/************************************************************
 * Return pointer to main application
 ************************************************************/
ChimeApp* ChimeSystemDriver::GetApplication ()
{
	return application;
}

/************************************************************
 * Transport the user to the default location in the sector
 * whose parameters (name & source) are given
 ************************************************************/
void ChimeSystemDriver::TransportToSector (char *strSectorName, char *strSectorSource)
{
	// first find the sector
	ChimeSector *sector = chNeighborQueue->FindSector (strSectorName, strSectorSource);

	if (!sector)
		return;

	// now get the room and location
	iSector *room = sector->GetDefaultRoom ();
	csVector3 pos = sector->GetDefaultLocation ();

	// place the user there
	chUser->PlaceUser (pos, room);
	SetCurrentSector (sector);

	isRunning = true;
	isOnGround = false;
}


/************************************************************
 * The user is physically in the given room.
 * Find which sector this room belongs to and
 * ,if necessary, update the system with new sector. 
 ************************************************************/
void ChimeSystemDriver::UpdateCurrentSector (iSector* room)
{
	// if this room is in the current sector,
	// simply tell the sector we're now in this room
	if (chCurrentSector->IsRoomInThisSector (room))
	{
		chCurrentSector->SetCurrentRoom (room);
		return;
	}

	// otherwise, find the sector to which this
	// room belongs and update the system
	ChimeSector* sector = chNeighborQueue->FindSector (room);
	if (sector)
		SetCurrentSector (sector);
	sector->SetCurrentRoom (room);
}


/*************************************************************
 * Update the system to the new sector, i.e.
 * update history and chat windows to reflect the
 * contents of the new sector
 *************************************************************/
void ChimeSystemDriver::SetCurrentSector (ChimeSector* sector)
{
	if (!sector)
		return;
	
	// set current sector
	chCurrentSector = sector;

	// update history window
	/**
	if (chHistoryWindow)
	{
        char name[100], source[100];
        chCurrentSector->GetSectorTitle (name, source);
        chHistoryWindow->AddItem (name, source);
	}
	*/

	// update chat window
	if (chChatWindow)
	{
		chChatWindow->RemoveAllItems ();
		ChimeSectorUser *user = NULL;
        char source[100];
        csVector *user_list (chCurrentSector->FindAllEntities (ENTITY_TYPE_USER));
        for (int i = 0; i < user_list->Length (); i++)
		{
            user = (ChimeSectorUser*) user_list->Get (i);
            if (user)
			{
                user->GetObjectSource (source);
                chChatWindow->AddItem (user->GetEntityName (), source);
			}
		}
	}
}


/*************************************************************
 * Force system to redraw 2D components
 *************************************************************/
void ChimeSystemDriver::Redraw ()
{
	doRedraw = true;
}

/*************************************************************
 * Shine all lights in the room on all meshes in the room
 *************************************************************/
void ChimeSystemDriver::ShineLights (iSector *room)
{
	printf("\nShining lights in room %s...\n", room->QueryObject ()->GetName ());
	printf("-------------------------------------\n");

	// shine lights on all meshes in the room
	int i = 0;
	for (i = 0; i < room->GetMeshes ()->GetCount (); i++)
		ShineLights (room, room->GetMeshes ()->Get (i));

	printf("Finished shining lights\n");
	printf("-------------------------------------\n");
}

/*************************************************************
 * Shine all lughts in the room on the given mesh
 *************************************************************/
void ChimeSystemDriver::ShineLights (iSector* room, iMeshWrapper* mesh)
{
	printf("Shining lights on mesh %s...", mesh->QueryObject ()->GetName ());

	// reset mesh lighting info
	mesh->GetLightingInfo ()->InitializeDefault ();

    // prepare mesh lighting info
    mesh->GetLightingInfo ()->PrepareLighting ();

	// shine lights on the mesh
	room->ShineLights (mesh);

	printf(" Done\n");
}

/***************************************************************
 * Set modular window and stop 3D movement
 ***************************************************************/
void ChimeSystemDriver::OpenModularWindow (ModularWindow* window)
{
	chModularWindow = window;
	chModularWindow->Center ();
	Stop3D ();
}

/***************************************************************
 * Set modular window to NULL and restart 3D movement
 ***************************************************************/
void ChimeSystemDriver::CloseModularWindow (ModularWindow* window)
{
	if (chModularWindow == window)
		chModularWindow = NULL;
	Redraw ();
	Start3D ();
}

/***************************************************************
 * Startup system by popping-up a login window
 ***************************************************************/
bool ChimeSystemDriver::LoginUser ()
{
	new LoginWindow (application);
	return true;
}

/***************************************************************
 * Tell the system that default environment was loaded
 ***************************************************************/
void ChimeSystemDriver::PrepareEnvironment ()
{
	isEnvironmentReady = true;
}

/*****************************************************************
 * Tell the system that system is ready after loggin in the user
 *****************************************************************/
void ChimeSystemDriver::PrepareSystem ()
{
	isSystemReady = true;
}

DWORD WINAPI UpdateThread (LPVOID lParam)
{
	driver->csEngine->ShineLights ((iRegion*) lParam);
	driver->Redraw ();
	return 0;
}

void ChimeSystemDriver::UpdateEngine (iRegion* region)
{
	DWORD dwID;
	CreateThread (NULL, 0, UpdateThread, region, 0, &dwID);

	printf(" Done\n");
}