/*******************************************************************
    ChimeSectorEntities.h
	Author: Mark Galagan @ 2003

	Header file defines entities that reside inside
	a ChimeSector, e.g. active doors, objects, etc.
********************************************************************/

#ifndef __ChimeSectorEntities_H__
#define __ChimeSectorEntities_H__

#include <stdarg.h>
#include "imesh/thing/polygon.h"
#include "csutil/csvector.h"
#include "csws/csws.h"
#include "iengine/camera.h"
#include "iengine/sector.h"

#define ENTITY_TYPE_DOOR 1
#define ENTITY_TYPE_OBJECT 2

/******************************************************************************
 * General container for any active object
 ******************************************************************************/
class ChimeSectorEntity
{
protected:

	// object attributes
	char *strEntityName;
	int intEntityType;

public:

	/*******************************************************
	 * Generic constructor assigns common attributes
	 *******************************************************/
	ChimeSectorEntity (char *iEntityName, int iEntityType)
	{
		strEntityName = iEntityName;
		intEntityType = iEntityType;
	}

	/*******************************************************
	 * Returns entity name
	 *******************************************************/
	char* GetEntityName ()
	{
		return strEntityName;
	}

	/*******************************************************
	 * Returns entity type
	 *******************************************************/
	int GetEntityType ()
	{
		return intEntityType;
	}

	/*******************************************************
	 * Activates entity, whatever activity means for a
	 * particular entity
	 *******************************************************/
	virtual bool ActivateEntity () = 0;

	virtual void SetupEntityMenu (csMenu *csEntityMenu)
	{
		char outputString[100];
		strcpy (outputString, "Name: ");
		if (strEntityName)
            strcat (outputString, strEntityName);
		else
			strcat (outputString, "<NONE>");
		(void)new csMenuItem (csEntityMenu, outputString, -1);
	}

	virtual bool HasLabel () { return true; }
};


/******************************************************************************
 * Active door container
 ******************************************************************************/
class ChimeSectorDoor : public ChimeSectorEntity
{
private:

	// door attributes
	iPolygon3D *csDoorPolygon;
	iPolygon3D **csDoorLabelPolygons;
	iSector *csTargetRoom;
	char *strTargetName, *strTargetURL;
	char *strDoorTexture;
	int numLabels;

public:

	ChimeSectorDoor (char *iDoorName, iPolygon3D *polygon, 
		iPolygon3D **door_label, int num_letters, 
		char *iTargetName, char *iTargetURL, char *iDoorTexture);				// create a door entity for given door polygon
	bool IsDoorPolygon (iPolygon3D *polygon);									// returns true if this is the same polygon as the one used for this door
	bool SetDoorTarget (char *iTargetName, char *iTargetURL);					// set the target sector of this door
	bool SetDoorTexture (char *iTexture);										// set the texture used for this door
	bool SetDoorVisible (bool flag);											// set the door to be visible if flag is true, transparent otherwise
	bool ConnectDoorToTarget (bool doConnect, iSector *target);					// connect or disconnect this door to its target, depending on flag
	bool OpenDoor ();															// open this door
	bool CloseDoor ();															// close this door
	bool ActivateEntity ();														// activate this door
	void SetupEntityMenu (csMenu *csEntityMenu);								// setup the csEntityMenu for editing this door
	bool HasLabel () { return false; }
};


/******************************************************************************
 * Active object container
 ******************************************************************************/
class ChimeSectorObject : public ChimeSectorEntity
{
private:

	// object attributes
	iMeshWrapper *csObjectMesh;
	csVector3 *csObjectLocation;
	csVector3 *csObjectLabelCenter;
	iSector *csObjectRoom;
	char *strObjectSource;
	char *strObjectModel;

public:

	ChimeSectorObject (char *iObjectName, int iObjectType, 
		iMeshWrapper *iMesh, csVector3 *iObjectLocation, 
		iSector *iObjectRoom, char *iObjectModel);								// create an object for given mesh
	bool IsObjectMesh (iMeshWrapper *iMesh);									// returns true if this is the same mesh as the one used for this object
	bool SetObjectSource (char *iObjectSource);									// set the source URL of this object
	bool SetObjectModel (char *iObjectModel);									// set the model used for this object
	bool SetObjectLocation (csVector2 newMousePosition, iCamera *camera);		// move the object to new location indicated by mouse position using collision detection
	bool SetObjectRoom (iSector *iObjectRoom);									// set the room where this object is located
	bool ActivateEntity ();														// activate this object
	void SetupEntityMenu (csMenu *csEntityMenu);								// setup the csEntityMenu for editing this object
	csVector3* GetObjectLabelCenter ();											// return the 3D coordinate of the object's label center
	char* GetObjectLabel ();													// return the text for this object's label
	bool IsObjectVisible (iCamera* camera, csRect const window_rect);			// return true if this object's mesh is visible in this camera for this window rectangle
};

#endif // __ChimeSectorEntities_H__