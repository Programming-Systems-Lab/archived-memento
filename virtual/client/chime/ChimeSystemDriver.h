/*******************************************************************
    ChimeSystemDriver.h
	Author: Mark Galagan @ 2002

	Header file for the CHIME main driver
********************************************************************/

#ifndef __ChimeSystemDriver_H__
#define __ChimeSystemDriver_H__

#include <stdarg.h>
#include "csutil/ref.h"
#include "csgeom/vector3.h"
#include "csutil/csvector.h"
#include "isound/handle.h"

#include "ChimeSector.h"
#include "ChimeUser.h"
#include "ChimeApp.h"
#include "ChimeCollider.h"
#include "ChimeEngineView.h"
#include "ChimeNeighbors.h"
#include "ChimeSectorEntities.h"
#include "ChimeWindowToolkit.h"

#define DEBUG	0
#define NODEBUG	1
#define MAX_VISIBILITY_DISTANCE 15

#define MAX(A,B)	((A) > (B) ? (A) : (B))
#define MIN(A,B)	((A) < (B) ? (A) : (B))

struct iEngine;
struct iLoader;
struct iGraphics3D;
struct iKeyboardDriver;
struct iVirtualClock;
struct iObjectRegistry;
struct iEvent;
struct iSector;
struct iView;
struct iDynamics;
struct iDynamicSystem;
struct iMeshWrapper;
struct iCollider;
struct iCollideSystem;
struct iModelConverter;
struct iCrossBuilder;
struct iVFS;
struct iMeshList;

/******************************************
 * Class definition for ChimeSystemDriver *
 ******************************************/
class ChimeSystemDriver
{
private:

  //Crystal Space engine variables
  iObjectRegistry* csObjectRegistry;									//object registry
  csRef<iVFS> csVFS;													//VFS plug-in
  csRef<iGraphics3D> csGraphics3D;										//3D plug-in
  csRef<iGraphics2D> csGraphics2D;										//2D plug-in
  csRef<iKeyboardDriver> csKeyboardDriver;								//keyboard driver
  csRef<iVirtualClock> csVirtualClock;									//virtual clock
  csRef<iModelConverter> csModelConverter;								//model converter
  csRef<iCrossBuilder> csCrossBuilder;									//crossbuilder
  csRef<iFontServer> csFontServer;										//font server
  
  //CHIME-specific variables
  ChimeSector*				chCurrentSector;							//current CHIME sector
  ChimeUser*				chUser;										//CHIME user
  ChimeCollider*			chCollider;									//collision detector
  ChimeEngineView			*chView, *chMapView;						//main and map views of the world
  ChimeNeighbors			*chNeighborQueue;							//queue of Chime sectors loaded
  ChimeSectorEntity			*chSelectedEntity;							//pointer to the entity selected with a single mouse click

  //other windows
  csNotebook*			notebook;
  ChimeHistoryWindow*	chHistoryWindow;								//history list window
  ChimeChatWindow*		chChatWindow;									//chat window
  ChimeAi2tvWindow*		chAi2tvWindow;									//AI2TV player window

  //helper variables
  int	chDebugMode;													//defines level of debugging output
  csVector2 csLastMousePosition;										//holds the last screen coordinates where the mouse was
  csMenu *csEntityMenu;													//pointer to a csEntityMenu, used to draw all variable menus
  csVector *chVisibleObjects;											//list of objects visible in the current view
  csRect *chMainViewWindowRect, *chMapViewWindowRect;					//pointers to rectangles of windows holding engine views
  int chSystemFont, chMenuFont, chLabelFont, chLabelFontNum;			//indeces that keep track of fonts for the system
  bool clearBackground;													//flag that tells the system whether to clear 2D background
  bool isRunning;														//flag used to start or stop 3D animation
  bool isScreenUpdated;													//flags whether screen was updated after a change was made
  char str2DMessage[100];												//text of a 2D message

  //private member functions

  //Crystal Space functions
  static bool EventHandler (iEvent& ev);								//CS function to handle user events
  bool HandleEvent (iEvent& event);										//actually handles user events
  void SetupFrame ();													//prepare to render next frame
  void FinishFrame ();													//finish rendering next frame
  bool LoadLibraries (char *libname);									//load libraries used in CHIME from 'libname' file
  bool LoadTexture (char *name, char *file);							//load texture from a file
  bool LoadObject (char *name, char *file, char *texture);				//load a 3DS object from the file with given texture
  bool LoadFont (char *file, char *type);								//load a new font into font system
  bool HandleLeftMouseClick (iEvent &Event);							//respond to a single click from left mouse button
  bool HandleLeftMouseDoubleClick (iEvent &Event);						//respond to a double click from right mouse button
  bool HandleRightMouseClick (iEvent &Event);							//respond to single click from right mouse button
  bool HandleRightMouseDoubleClick (iEvent &Event);						//respond to double click from right mouse button
  bool HandleMouseMove (iEvent &Event);									//move an active object following mouse movement
  bool HandleEventFromOtherWindows (iEvent &Event);						//see if any of other windows can handle this event
  bool HandleKeyEvent (iEvent &Event);									//handle a key-pressed event
  bool CloseMenu ();													//destroy a 2D menu if one exists
  ChimeSectorEntity* SelectEntity (float x, float y);					//select an entity pointed to by the mouse click
  void SelectVisibleObjects (csVector* iEntityList,						//select only those entities that are visible in the current view
	  csVector *iVisibleObjectList);
  void DrawLabels ();													//draw 2D labels for the entities which are visible


  //CHIME functions
  bool ReadInitialSector();												//create the initial room to drop the user into
  bool InitializeUser();												//initialize the CHIME user


public:

  ChimeSystemDriver (iObjectRegistry* object_reg);						//constructor
  ~ChimeSystemDriver ();												//destructor

  //public CS variables
  csRef<iEngine> csEngine;												//engine
  csRef<iTextureManager> csTxtManager;									//texture manager
  csRef<iLoader> csLoader;												//loader plug-in
  csRef<iPluginManager> csPluginManager;								//plug-in manager
  csRef<iCollideSystem> csCollisionSystem;								//collision system
  bool isOnGround;														//false if user is above the ground

  //public member functions
  
  //-- startup functions --//
  bool Initialize ();													//initialize system driver
  bool InitializeEnvironment ();										//initialize environment
  void Start ();														//start the csEngine

  //-- setter functions --//
  void SetDebugMode (int mode);											//set debugging mode
  void SetOnGround (bool flag);											//set user on ground/not on ground

  //-- getter functions --//
  iObjectRegistry* GetObjectRegistry();									//returns object registry
  ChimeCollider* GetCollider();											//returns collision system
  ChimeSector* GetCurrentSector();										//returns current CHIME sector
  csVector2 GetLastMousePosition();										//returns the 2D coordinates of mouse's last position on screen

  //-- GUI control functions --//
  csMenu* CreateMenu (int x, int y);									//create new menu at given location
  void Redraw ();														//redraw the windows and background
  void Stop3D ();														//stop 3D animation
  void Start3D ();														//start 3D animation
  void Display2DMessage (char* strMessage);								//displays a 2D message on the screen
  void Delete2DMessage ();												//deletes current 2D meesage from the screen
  void WaitForScreenUpdate ();											//forces the system to wait for screen update
  bool HasScreenUpdated ();												//tells whether the screen was updated

  //-- system control functions --//
  void Report(char *source, char *message);								//print error report to standard output

  //-- world control functions --//
  ChimeSector* LoadNewSector (char *strSectorName, 
       char *strSectorSource, csVector3 &origin, 
	   csVector3 &iSectorRotation);										//load given sector, placing it at the given origin and rotating it
  void TransportToSector (char *strSectorName, char *strSectorSource);	//transport the user to default location in given sector
  void UpdateCurrentSector (iSector* room);								//update current sector
  void SetCurrentSector (ChimeSector* sector);							//set current sector
};

#endif // __ChimeSystemDriver_H__