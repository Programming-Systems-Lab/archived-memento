/*******************************************************************
    ChimeSectorStructures.h
	Author: Mark Galagan @ 2003

	Header file defines structures for storing room descriptions
	parsed from XML files
********************************************************************/

#include <stdarg.h>

/******************************************************************************
 * Definition for a single object
 ******************************************************************************/
typedef struct {
	char strObjectName[50];				//name of this object
	char strObjectModel[50];			//name of the 3D model used for this object
	char strRoomName[50];				//name of the room this object resides in
	int intObjectType;					//type of this object
	csVector3 location;					//location
	csVector3 scale;					//scale of the model
	csVector3 rotation;					//rotation of the model
} chObjectStruct, *chObjectStructPtr;

/******************************************************************************
 * Definition for a single active object
 ******************************************************************************/
typedef struct {
	chObjectStructPtr object;			//object definition
	char strTargetSectorName[50];		//name of the Chime sector this object leads to
	char strTargetSectorSource[100];	//Source of the Chime sector this object leads to
} chActiveObjectStruct, *chActiveObjectStructPtr;

/******************************************************************************
 * Definition for a single door
 ******************************************************************************/
typedef struct {
	csPoly3D door;						//vertices that define the polygon for the door
	char strRoomName[50];				//name of the room this door connects to
	char strDoorTexture[50];			//name of the material for the door
	csVector3 txtSize;					//size of the texture unit
	int	alpha;							//determines the transparency of the door (0 for transparent, 255 for non-transparent)
} chDoorStruct, *chDoorStructPtr;

/******************************************************************************
 * Definition for a single outer door (door leading to a different sector)
 ******************************************************************************/
typedef struct {
	char strDoorName[50];				//name of this door
	char strTargetSectorName[50];		//name of the sector this door leads to
	char strTargetSectorSource[100];		//Source of the sector this door leads to
	chDoorStructPtr door;				//door definition
} chOuterDoorStruct, *chOuterDoorStructPtr;

/******************************************************************************
 * Definition for a single wall
 ******************************************************************************/
typedef struct {
	csPoly3D wall;						//vertices that define the polygon for the wall
	char strWallTexture[50];			//name of the material for the wall
	csVector3 txtSize;					//size of the texture unit
} chWallStruct, *chWallStructPtr;

/******************************************************************************
 * Definition for a single light
 ******************************************************************************/
typedef struct {
	csVector3 location;					//location of the light
	csColor color;						//color of the light
	float scale;						//scale of the light
} chLightStruct, *chLightStructPtr;

/******************************************************************************
 * Definition for a single room
 ******************************************************************************/
typedef struct {
	char strRoomName[50];				//name of this room
	chWallStructPtr *walls;				//array of walls (defined in terms of chWallStructPtr)
	int	numWalls;						//number of walls
	chDoorStructPtr *doors;				//array of doors (defined in terms of chDoorStructPtr)
	int	numDoors;						//number of doors
	chOuterDoorStructPtr *outer_doors;	//array of outer doors
	int numOuterDoors;					//number of outer doors
	chLightStructPtr *lights;			//array of lights
	int numLights;						//number of lights
	chObjectStructPtr *objects;			//array of objects
	int numObjects;						//number of objects
} chRoomStruct, *chRoomStructPtr;

/******************************************************************************
 * Definition for a single sector
 ******************************************************************************/
typedef struct {
	char strSectorName[50];				//name of this sector
	char strSectorSource[100];				//Source of this sector
	csVector3 defaultLocation;			//default location in this sector
	chRoomStructPtr *rooms;				//array of rooms in this sector
	int numRooms;						//number of rooms
} chSectorStruct, *chSectorStructPtr;