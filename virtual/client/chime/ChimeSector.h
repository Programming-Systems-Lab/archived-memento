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
#include "csengine/region.h"
#include "ChimeSectorStructures.h"
#include "ChimeSectorEntities.h"

#define DOOR_WIDTH 2
#define DOOR_HEIGHT 3

/******************************************
 * Class definition for ChimeSector       *
 ******************************************/
class ChimeSector
{
private:

  //CHIME sector variables
  csVector *chRooms;												// list of all rooms in this sector
  iSector *chCurrentRoom, *chEntranceRoom;							// pointers to the current room and the previous room from previous sector
  char *strSectorName, *strSectorSource;							// sector name and Source comprise sector title
  csVector *chActiveEntities;										// holds references to active objects and doors inside this sector
  csVector3 csDefaultLocation;										// default location
  iPolygon3D *chActiveScreen;										// active AI2TV screen

  //Constructor
  ChimeSector (char *strISectorName, char *strISectorSource);


  /************************** Sector building functions *************************************/

  //Build this sector from a given sector definition
  bool BuildSector (chSectorStructPtr sectorStruct, char *strRegionName, iSector *iEntranceRoom);

  //Build a room from a given room definition
  iSector* BuildRoom (chRoomStructPtr roomStruct);

  //Build wall for the vertices contained in the Poly3D object
  iPolygon3D* BuildWall (iThingState *walls, csPoly3D const &vertices,
						 char *strTextureName = NULL, csVector3 *textureSize = NULL,
						 char* strPolygonName = NULL);

  //Build wall for the vertices contained in the Poly3D object
  //with given material and given texture size (they cannot be NULL)
  iPolygon3D* BuildWall (iThingState *walls, csPoly3D const &vertices,
						 iMaterialWrapper* material, csVector3 *textureSize,
						 char* strPolygonName = NULL);

  //Build entrance into given room
  iPolygon3D* BuildEntrance (iThingState *walls, csPoly3D const &vertices,
						char *strTextureName, csVector3 *textureSize,
						iSector *room, int alpha);

  //Make a hole in the wall of given size
  void MakeHoleInWall (csPoly3D const &hole, iThingState *walls, iPolygon3D *wall);

  //Build a door in a polygon closest to given point
  iPolygon3D* BuildDoor (iSector* room, iMeshWrapper* door_mesh, 
	  const csVector3 &origin, char *strDoorTexture, csVector3 *textSize);

  //Build a door for given vertices and given door texture
  iPolygon3D* BuildDoor (iSector* room, iMeshWrapper* door_mesh, 
	  const csPoly3D &door, char *strDoorTexture, csVector3 *textSize);

  //Create the actual door polygon
  iPolygon3D* BuildDoor (iSector* room, const csPoly3D &door, iPolygon3D* wall, 
	  iMeshWrapper* door_mesh, char *strDoorTexture, csVector3 *textSize);

  //Add door entity
  bool AddDoorEntity (csVector3 *vOrigin, char* strRoomName, char *strDoorName, 
	  char *strTargetName, char *strTargetSource, char *strDoorTexture, 
	  csVector3* vTxtSize);

  //Add door entity
  bool AddDoorEntity (csPoly3D &door, char *strDoorTexture, csVector3 *vTxtSize,
	  iSector* thisRoom, iSector* roomConnection);

  //Add a light to given room at given location
  bool AddLight (csVector3 const &location, csColor color, float const scale, iSector* room);

  //Add a mesh object from an object factory to a given room at a given location
  iMeshWrapper* AddMeshObject (char *strObjectName, char* strFactoryName, 
							   iSector* room, csVector3 const &location);

  //Add polygons to use as a label for a door
  iPolygon3D** AddPolygonLabel (iSector* room, iPolygon3D *door, int &num_letters);

  //Find material under given name
  csRef<iMaterialWrapper> FindMaterial (char *strMaterialName);
  
  //Finds a polygon closest to given origin
  iPolygon3D* FindClosestPolygon (iThingState *poly_state,
	  csVector3 const &origin, csVector3 *intersect,
	  bool (*func) (const csVector3*, iPolygon3D*));

  void RecreatePolygonsFromMesh (iMeshWrapper* mesh);


  /************************** XML processing functions *************************************/

  //Parse given XML file to get the description for this sector
  chSectorStructPtr static ParseSectorDefinition (char *strFileName);

  //Process given sector structure
  void static ProcessSectorDefinition (chSectorStructPtr sector, csVector3 const &origin, csVector3 const &rot);


  /************************** Helpful functions *************************************/
  
  //Create the name of the region using sector parameters
  static void GetRegionName (char* strSectorName, char* strSectorSource, char* strRegionName);


public:
  //Desctructor
  ~ChimeSector ();


  /************************** Static sector building functions *************************************/
  //Build the whole sector from XML file description
  static ChimeSector* SetupSector (csVector3 &origin, csVector3 const &rotation, 
	  char *strSectorXMLFile, char *strISectorName, char *strISectorSource, iSector* iEntranceRoom);


  /************************** Accessing functions *************************************/
  //Return the room user is currently in
  iSector* GetCurrentRoom ();

  //Set the room user is currently in
  void SetCurrentRoom (iSector *room);

  //Get the default room
  iSector* GetDefaultRoom ();

  //Fill in region name for this sector
  void GetRegionName (char *strRegionName);

  //Return the starting location for the default room
  csVector3 GetDefaultLocation ();

  //Returns region in which this sector resides
  iRegion* GetRegion ();

  //Select active entity in this sector closest to the beam from start to end
  ChimeSectorEntity* SelectEntity (const csVector3 &start, const csVector3 &end, csVector3 &isect);

  //Return the list of active entities in this sector
  csVector* GetActiveEntities ();

  //Find a particular entity
  ChimeSectorEntity* FindEntity (char *strEntityName, int iEntityType);

  //Find all entities of given type
  csVector* FindAllEntities (int iEntityType);

  //Return true if this is the sector with given name and Source
  bool IsThisSector (char *iSectorName, char *iSectorSource);

  //Find room based on room name
  iSector* FindRoom (char *strRoomName);

  //Return true if given room belongs to this sector
  bool IsRoomInThisSector (iSector *room);

  //Fill this sector's attributes
  void GetSectorTitle (char *strName, char *strSource);

  //Find the door that leads to the entrance sector
  ChimeSectorDoor* FindEntranceDoor ();


  /************************** AI2TV building functions *************************************/

  //Build a AI2TV screen at given coordinates
  void BuildScreen (csOrthoTransform const &transform);

  //Display an image on existing screen
  bool DisplayImageOnScreen (iMaterialWrapper* image);


  /************************** Entity update functions *************************************/

  //Update given entity
  bool UpdateEntity (char *strEntityName, int iEntityType, 
	  csStrVector param_name, csVector param_value);

  //Add a new entity
  bool AddEntity (char *strEntityName, int iEntityType,
	  const csStrVector &param_name, const csVector &param_value);

};

#endif // __ChimeSector_H__