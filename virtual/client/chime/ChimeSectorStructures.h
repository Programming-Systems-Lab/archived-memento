/*******************************************************************
    ChimeSectorStructures.h
	Author: Mark Galagan @ 2003

	Header file defines structures for storing room descriptions
	parsed from XML files
********************************************************************/

#include <stdarg.h>


/******************************************************************************
 * Definition for a single door
 ******************************************************************************/
typedef struct {
	csPoly3D door;						//vertices that define the polygon for the door
	char strRoomName[20];				//name of the room this door connects to
	char *strDoorTexture;				//name of the material for the door
	csVector3 *txtSize;					//size of the texture unit
	int	alpha;							//determines the transparency of the door (0 for transparent, 255 for non-transparent)
} chDoorStruct, *chDoorStructPtr;

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
	float intensity;					//intensity of the light
} chLightStruct, *chLightStructPtr;

/******************************************************************************
 * Definition for a single room
 ******************************************************************************/
typedef struct {
	char strRoomName[20];				//name of this room
	chWallStructPtr *walls;				//array of walls (defined in terms of chWallStructPtr)
	int	numWalls;						//number of walls
	chDoorStructPtr *doors;				//array of doors (defined in terms of chDoorStructPtr)
	int	numDoors;						//number of doors
	chLightStructPtr *lights;			//array of lights
	int numLights;						//number of lights
} chRoomStruct, *chRoomStructPtr;

/******************************************************************************
 * Definition for a single sector
 ******************************************************************************/
typedef struct {
	int iSectorID;						//ID number identifying this schema
	csVector3 defaultLocation;			//default location in this sector
	chRoomStructPtr *rooms;				//array of rooms in this sector
	int numRooms;						//number of rooms
} chSectorStruct, *chSectorStructPtr;

