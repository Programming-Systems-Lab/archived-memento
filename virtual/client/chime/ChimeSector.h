/*******************************************************************
    ChimeSector.h
	Author: Mark Galagan @ 2002

	Header file for a CHIME sector definition
********************************************************************/

#ifndef __ChimeSector_H__
#define __ChimeSector_H__

#include <stdarg.h>
#include "csutil/ref.h"
#include "csgeom/vector3.h"
#include "csutil/csvector.h"
#include "csutil/csstrvec.h"
#include "imesh/thing/thing.h"
#include "ivaria/dynamics.h"
#include "csgeom/poly3d.h"
#include "csutil/cscolor.h"
#include "csengine/sector.h"
#include "ChimeSectorStructures.h"
#include "ChimeSectorEntities.h"


/******************************************
 * Class definition for ChimeSector       *
 ******************************************/
class ChimeSector
{
private:

  //CHIME sector variables
  iSector *chCurrentRoom, *chPreviousRoom;							// pointers to the current room and the previous room from previous sector
  char *strSectorName, *strSectorURL;								// sector name and URL comprise sector title
  csStrVector *strRoomNames;										// names of all the rooms in this sector
  csVector *chActiveEntities;										// holds references to active objects and doors inside this sector
  csVector3 csDefaultLocation;										// default location

  //Constructor
  ChimeSector (char *strISectorName, char *strISectorURL);

  /************************** Sector building functions *************************************/

  //Build this sector from a given sector definition
  bool BuildSector (chSectorStructPtr sectorStruct);

  //Build this sector from a given room definition
  iSector* BuildRoom (chRoomStructPtr roomStruct);

  //Parse given XML file to get the description for this sector
  chSectorStructPtr static ParseSectorDefinition (char *strFileName);

  //Process given sector structure
  void static ProcessSectorDefinition (chSectorStructPtr sector, csVector3 const &origin, csVector3 const &rot);
  
  //Add a light to given room at given location
  bool AddLight (csVector3 const &location, csColor color, float const scale, iSector* room);

  //Add a mesh object from an object factory to a given room at a given location
  iMeshWrapper* AddMeshObject (char *strObjectName, char* strFactoryName, 
							   iSector* room, csVector3 const &location);

  //Add polygons to use as outer door's labels
  iPolygon3D** AddDoorLabel (iThingState *walls, iPolygon3D *door, int &num_letters);
  
  //Build wall for the vertices contained in the Poly3D object
  iPolygon3D* AddWall (iThingState *walls, csPoly3D const &vertices,
						 iMaterialWrapper *txt, csVector3 const &txtSize);

  //Build door for given vertices that connects to a given room
  iPolygon3D* AddInnerDoor (iThingState *walls, csPoly3D const &vertices,
						iMaterialWrapper *txt, csVector3 const &txtSize,
						iSector *room, int alpha);

  //Build an outer door for given vertices (closed)
  iPolygon3D* AddOuterDoor (iThingState *walls, csPoly3D const &vertices,
						iMaterialWrapper *txt, csVector3 const &txtSize,
						char *strDoorName, char *strDoorTargetName, 
						char *strDoorTargetURL, char *strDoorTexture);


public:
  //Desctructor
  ~ChimeSector ();


  /************************** Static sector building functions *************************************/
  //Build the whole sector from XML file description
  static ChimeSector* SetupSector (csVector3 &origin, csVector3 const &rotation, 
	  char *strSectorXMLFile, char *strISectorName, char *strISectorURL, ChimeSector* iPreviousSector);

  /************************** Accessing functions *************************************/
  //Return the room user is currently in
  iSector* GetCurrentRoom ();

  //Set the room user is currently in
  void SetCurrentRoom (iSector *room);

  //Get the default room
  iSector* GetDefaultRoom ();

  //Return the starting location for the default room
  csVector3 GetDefaultLocation ();

  //Select active entity in this sector closest to the beam from start to end
  ChimeSectorEntity* SelectEntity (const csVector3 &start, const csVector3 &end, csVector3 &isect);

  //Return the list of active entities in this sector
  csVector* GetActiveEntities ();

  //Return true if this is the sector with given name and URL
  bool IsThisSector (char *iSectorName, char *iSectorURL);

  //Fill this sector's attributes
  void GetSectorTitle (char *strName, char *strURL);

};

#endif // __ChimeSector_H__