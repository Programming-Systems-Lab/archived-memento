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


//*** Chime Includes***//
#include "ChimeSystemDriver.h"

CS_IMPLEMENT_APPLICATION

//-----------------------------------------------------------------------------

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

/*****************************************************************
 * Constructor: sets up the object registry
 *****************************************************************/
ChimeSystemDriver::ChimeSystemDriver (iObjectRegistry* object_reg)
{
  ChimeSystemDriver::csObjectRegistry = object_reg;

  // INITIALIZE MEMBER VARIABLES
  chNeighborQueue = new ChimeNeighbors ();
  chCurrentSector = NULL;
  chSelectedEntity = NULL;
  csEntityMenu = NULL;
  chVisibleObjects = new csVector ();
  chMenuFont = chSystemFont = chLabelFont = chLabelFontNum = 0;
  clearBackground = false;
}

/*****************************************************************
 * Destructor: cleans up the sysytem
 *****************************************************************/
ChimeSystemDriver::~ChimeSystemDriver ()
{
	delete driver;
	driver = NULL;
	csInitializer::DestroyApplication (csObjectRegistry);
}

/***********************************************************
 * Load a new sector given its name and URL.
 * New sector is filled with objects and users returned by
 * the server, but neighbors are only loaded with sector
 * definitions, void of active objects (those are only loaded
 * when the user enters).
 ***********************************************************/
ChimeSector* ChimeSystemDriver::LoadNewSector (char *strSectorName, char *strSectorURL, 
											   csVector3 const &iSectorOrigin, 
											   csVector3 const &iSectorRotation)
{
	// first check if this sector is already in the queue
	ChimeSector *new_sector = chNeighborQueue->FindSector (strSectorName, strSectorURL);
	if (new_sector)
		return new_sector;

	// receive the data from the server
	// parse XML into structures
	// build empty sectors
	// fill this sector with objects and doors
	// attach sectors to outer doors
	new_sector = ChimeSector::SetupSector (csVector3 (iSectorOrigin - csVector3 (0, 0, 0.2)), iSectorRotation, 
		"none", strSectorName, strSectorURL, chCurrentSector ? chCurrentSector : NULL);

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
  if (!isOnGround) 
  {
	  chUser->MoveUser (csVector3 (0, 0, 0), fps, true);
	  checkVertical = false;
  }

  // Now rotate the camera according to keyboard state
  float speed = (elapsed_time / 1000.0) * (0.03 * 50);

  //Update user's position according to keyboard event
  if (!csEntityMenu)
  {
      if (csKeyboardDriver->GetKeyState (CSKEY_RIGHT))
		chUser->RotateUser(CS_VEC_ROT_RIGHT, speed);
	  if (csKeyboardDriver->GetKeyState (CSKEY_LEFT))
          chUser->RotateUser(CS_VEC_ROT_LEFT, speed);
      if (csKeyboardDriver->GetKeyState (CSKEY_PGUP))
          chUser->RotateUser(CS_VEC_TILT_UP, speed);
      if (csKeyboardDriver->GetKeyState (CSKEY_PGDN))
          chUser->RotateUser(CS_VEC_TILT_DOWN, speed);
      if (csKeyboardDriver->GetKeyState (CSKEY_UP))
          chUser->MoveUser(CS_VEC_FORWARD * 4 * speed, fps, checkVertical);
      if (csKeyboardDriver->GetKeyState (CSKEY_DOWN))
          chUser->MoveUser(CS_VEC_BACKWARD * 4 * speed, fps, checkVertical);
  }

  // Tell 3D driver we're going to display 3D things.
  if (!csGraphics3D->BeginDraw (csEngine->GetBeginDrawFlags () | CSDRAW_3DGRAPHICS))
    return;

  // Tell the views to render into the frame buffer.
  chView->Draw ();
  chMapView->Draw();

  // Clear background id the flag is set
  if (clearBackground)
  {
	  csGraphics2D->Clear (csTxtManager->FindRGB (125, 125, 125));
	  clearBackground = false;
  }

  // Start drawing 2D graphics.
  if (!csGraphics3D->BeginDraw (CSDRAW_2DGRAPHICS)) return;

  // If there is no open menu, draw labels for active entities
  if (!csEntityMenu)
	  DrawLabels ();

  // Draw "frames per second" inside main view
  if (chDebugMode == DEBUG)
  {
      char buf[50];
      sprintf (buf, "FPS: %g", fps);
      int x = 10, y = 30;
      chView->GetWindow()->LocalToGlobal (x, y);
      csGraphics2D->DrawBox (x-5, y-5, 100, 30, csTxtManager->FindRGB (0, 0, 0));
      csGraphics2D->DrawBox (x-3, y-3, 96, 26, csTxtManager->FindRGB (255, 255, 255));
      csGraphics2D->Write (csFontServer->GetFont (chSystemFont), x, y, csTxtManager->FindRGB (200, 0, 20), -1, buf);
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

	// If there is an open menu, draw it on top
	// of everything else
	if (csEntityMenu && csEntityMenu->Select ())
		csEntityMenu->Invalidate (true, NULL);
}

/*****************************************************************
 * Event handling system. Coordinates user events.
 *****************************************************************/
bool ChimeSystemDriver::HandleEvent (iEvent& event)
{
	chApplication->HandleEvent (event);
	
	switch (event.Type)
	{
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
        case csevMouseDoubleClick:
            HandleLeftMouseDoubleClick(event);
            break;
        case csevMouseDown:
            if(event.Mouse.Button == 2)
			{
                HandleRightMouseClick(event);
			}
            else if(event.Mouse.Button == 1)
			{
                HandleLeftMouseClick(event);
			}
            break;
        case csevMouseUp:
            if(event.Mouse.Button == 1)
			{
                chSelectedEntity = NULL;
			}
            break;
        case csevMouseMove:
            HandleMouseMove (event);
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
 * Creates the initial room for user to drop into
 *****************************************************************/
bool ChimeSystemDriver::ReadInitialSector ()
{
  // Set up the CHIME sector
  chCurrentSector = ChimeSector::SetupSector (csVector3 (0, 0, -10), csVector3 (0, 0, 0), "none", "sector0", "www.default.com/", NULL);
  chNeighborQueue->AddSector (chCurrentSector);

  //------------------ Set up 3D views of this system --------------------------------//

  // Main view
  csWindow *w = new csWindow (chApplication, "3D View", CSWS_TITLEBAR, cswfsThin);
  w->SetFont (csFontServer->GetFont (chSystemFont));
  w->SetRect (chApplication->bound.Width()/4, 0, chApplication->bound.Width()-1, chApplication->bound.Height()*2/3);
  chView = new ChimeEngineView (w, csEngine, chCurrentSector->GetDefaultRoom(), chCurrentSector->GetDefaultLocation(), csGraphics3D, chApplication);

  // Map view
  w = new csWindow (chApplication, "Map View", CSWS_TITLEBAR, cswfsThin);
  w->SetFont (csFontServer->GetFont (chSystemFont));
  w->SetRect (chApplication->bound.Width()*2/3, chApplication->bound.Height()*2/3 + 2, chApplication->bound.Width()-1, chApplication->bound.Height()-1);
  chMapView = new ChimeEngineView (w, csEngine, chCurrentSector->GetDefaultRoom(), chCurrentSector->GetDefaultLocation(), csGraphics3D, chApplication);

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

  // Get font server
  csFontServer = CS_QUERY_REGISTRY (csObjectRegistry, iFontServer);

  // Load objects for CHIME library
  LoadLibraries ("libraries.cfg");

  //Initialize collision system
  chCollider = new ChimeCollider();

  //Tell the engine to recalculate the Z-Buffer every frame
  csEngine->SetClearZBuf (true);

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
  chUser = new ChimeUser();
  if (!chUser->SetupUser(NULL, chView->GetCamera()))
  {
	Report ("Chime.Application.ChimeSystemDriver", "Could not create this user!");
    return false;
  }
  chUser->PlaceUser(chCurrentSector->GetDefaultLocation(), chCurrentSector->GetDefaultRoom());

  return true;
}


/************************************************************
 * Load object templates from CHIME library definition file
 ************************************************************/
bool ChimeSystemDriver::LoadLibraries (char *libname)
{
	printf("Loading libraries... ");
	
	//Prepare texture manager
	csTxtManager->SetVerbose (true);
	csTxtManager->ResetPalette ();

	// Allocate a uniformly distributed in R,G,B space palette for console
	// The console will crash on some platforms if this isn't initialize properly
	int r,g,b;
	for (r = 0; r < 8; r++)
	{
		for (g = 0; g < 8; g++)
		{
			for (b = 0; b < 4; b++)
			{
				csTxtManager->ReserveColor (r * 32, g * 32, b * 64);
			}
		}
	}

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
	csTxtManager->SetPalette ();

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
	if (chSelectedEntity)
		printf("Entity selected.\n");

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
		//chSelectedEntity->HandleLeftMouseDoubleClick(Event);
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
		chSelectedEntity->HandleRightMouseDoubleClick(Event);
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
	
	csVector2 p (Event.Mouse.x, chApplication->bound.Height()-Event.Mouse.y);
	
	chSelectedEntity->HandleMouseMove (csLastMousePosition, p, chView->GetCamera ());

	csLastMousePosition.Set (p);

	return true;
}


/************************************************************
 * Returns a newly created menu
 ************************************************************/
csMenu* ChimeSystemDriver::CreateMenu (int x, int y)
{
	CloseMenu ();
	csEntityMenu = new csMenu (chApplication, csmfs3D, 0);
	csEntityMenu->SetFont (csFontServer->GetFont (chMenuFont));

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

	// Find the point on the creen where the user clicked
	csVector2 screenCoord (x, chApplication->bound.Height() - y - 1);
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
 * Set the controlling instance of ChimeApp that is
 * the parent for this ChimeSystemDriver
 ***********************************************************/
void ChimeSystemDriver::SetApplication (ChimeApp* app)
{
  chApplication = app;
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
			labelFont = csFontServer->GetFont (fontIndex);

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
				fontHeight + 2, csTxtManager->FindRGB (255, 0, 0));
			csGraphics2D->DrawBox (xmin, ymin-1, labelWidth*fontWidth-2, 
				fontHeight, csTxtManager->FindRGB (255, 255, 255));

			// Select a color
			int labelColor = csTxtManager->FindRGB (0, 0, 0);
			
			// Draw the text
            csGraphics2D->Write (labelFont, xmin+5, ymin, labelColor, -1, labelText);
		}
	}
}


/*****************************************************************************
 * Redraw all windows and background.
 * Usually called after engine has been updated
 * (e.g. iEngine->Prepare (), iTextureManager->PrepareTextures (), etc.)
 *****************************************************************************/
void ChimeSystemDriver::Redraw ()
{
	// clear background
	clearBackground = true;
	
	// redraw CSWS windows
	if (chView)
		chView->GetWindow ()->Invalidate (true);
	if (chMapView)
		chMapView->GetWindow ()->Invalidate (true);
}