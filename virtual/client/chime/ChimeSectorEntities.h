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
#include "iutil/event.h"
#include "csws/csws.h"
#include "iengine/camera.h"
#include "iengine/sector.h"

#define ENTITY_TYPE_DOOR 1
#define ENTITY_TYPE_OBJECT 2
#define ENTITY_TYPE_ACTIVE_OBJECT 3
#define ENTITY_TYPE_USER 4

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
	 * Handle single left-button mouse click 
	 *******************************************************/
	virtual void HandleLeftMouseClick (iEvent &event)
	{
		return;
	}

	/*******************************************************
	 * Handle double left-button mouse click 
	 *******************************************************/
	virtual void HandleLeftMouseDoubleClick (iEvent &event)
	{
		return;
	}

	/*******************************************************
	 * Handle single right-button mouse click 
	 *******************************************************/
	virtual void HandleRightMouseClick (iEvent &event)
	{
		return;
	}

	/*******************************************************
	 * Handle double right-button mouse click 
	 *******************************************************/
	virtual void HandleRightMouseDoubleClick (iEvent &event)
	{
		return;
	}

	/*******************************************************
	 * Handle mouse movement
	 *******************************************************/
	virtual void HandleMouseMove (csVector2 old_pos, csVector2 new_pos, iCamera *camera)
	{
		return;
	}

	/*******************************************************
	 * Handle menu event
	 *******************************************************/
	virtual bool HandleMenuEvent (int iMenuCode)
	{
		return false;
	}

	/*******************************************************
	 * Update entity
	 *******************************************************/
	virtual void UpdateEntity (const csStrVector &param_name, 
		const csVector &param_value)
	{
		return;
	}

	/*******************************************************
	 * Returns true if the entity has the property
	 * of having a label
	 *******************************************************/
	virtual bool HasLabel () { return false; }

	/*******************************************************
	 * Returns true if either the selected mesh
	 * or polygon belong to this entity
	 *******************************************************/
	virtual bool IsEntitySelected (iMeshWrapper *mesh)
	{
		return false;
	}

};


/******************************************************************************
 * Active door container
 ******************************************************************************/
#define DOOR_MENU_SET_NAME CHIME_DOOR_MENU+1
#define DOOR_MENU_SET_TARGET CHIME_DOOR_MENU+2
#define DOOR_MENU_SET_TEXTURE CHIME_DOOR_MENU+3
class ChimeSectorDoor : public ChimeSectorEntity
{
protected:

	// door attributes
	iMeshWrapper *csDoorMesh;
	iPolygon3D *csDoorPolygon;
	iPolygon3D **csDoorLabelPolygons;
	iSector *csTargetRoom;
	char *strTargetName, *strTargetSource;
	char *strDoorTexture;
	int numLabels;

public:

	ChimeSectorDoor (char *iDoorName, iMeshWrapper *mesh, 
		iPolygon3D **door_label, int num_letters, 
		char *iTargetName, char *iTargetSource, char *iDoorTexture);			// create a door entity for given door polygon
	bool SetDoorTarget (char *iTargetName, char *iTargetSource);				// set the target sector of this door
	bool SetTargetRoom (iSector* room);											// directly set the target room
	bool SetDoorTexture (char *iTexture);										// set the texture used for this door
	bool SetDoorVisible (iPolygon3D* door, bool flag);							// set the door to be visible if flag is true, transparent otherwise
	bool ConnectDoorToTarget (bool doConnect, iSector *target=NULL);			// connect or disconnect this door to its target, depending on flag
	bool OpenDoor ();															// open this door
	bool CloseDoor ();															// close this door
	iMeshWrapper* GetDoorMesh () { return csDoorMesh; }							// return door mesh
	iPolygon3D* FindDoorPolygon ();												// return the polygon used for this door

	void HandleLeftMouseDoubleClick (iEvent &event);
	void HandleRightMouseClick (iEvent &event);
	bool HandleMenuEvent (int iMenuCode);
	bool IsEntitySelected (iMeshWrapper *mesh);
	//void UpdateEntity (csStrVector param_name, csVector param_value);
};


/******************************************************************************
 * Sector object container
 ******************************************************************************/
class ChimeSectorObject : public ChimeSectorEntity
{
protected:

	// object attributes
	iMeshWrapper *csObjectMesh;
	csVector3 *csObjectLabelCenter;
	iSector *csObjectRoom;
	char *strObjectSource;
	char *strObjectModel;
	char *strObjectMaterial;

public:

	ChimeSectorObject (char *iObjectName, char *iObjectSource = NULL,
		iMeshWrapper *iMesh = NULL, iSector *iObjectRoom = NULL, 
		char *sObjectModel = NULL, char *iObjectMaterial = NULL, 
		int iEntityType = ENTITY_TYPE_OBJECT);									// create an object for given mesh
	bool SetObjectSource (char *iObjectSource);									// set the source Source of this object
	bool SetObjectModel (char *iObjectModel);									// set the model used for this object
	bool SetObjectLocation (csVector2 newMousePosition, iCamera *camera);		// move the object to new location indicated by mouse position using collision detection
	bool SetObjectLocation (csVector3& new_pos);								// move object to given location
	bool SetObjectRoom (iSector *iObjectRoom);									// set the room where this object is located
	bool SetObjectMaterial (char *iObjectMaterial);								// set the material on the object mesh
	csVector3* GetObjectLabelCenter ();											// return the 3D coordinate of the object's label center
	char* GetObjectLabel ();													// return the text for this object's label
	void GetObjectSource (char* iObjectSource);								// copy object source
	bool IsObjectVisible (iCamera* camera, csRect const window_rect);			// return true if this object's mesh is visible in this camera for this window rectangle
	bool HasLabel () { return true; }

	void HandleLeftMouseDoubleClick (iEvent &event);
	void HandleRightMouseClick (iEvent &event);
	bool IsEntitySelected (iMeshWrapper *mesh);
	//void UpdateEntity (csStrVector param_name, csVector param_value);
};


/******************************************************************************
 * Sector user container
 ******************************************************************************/
class ChimeSectorUser : public ChimeSectorObject
{

public:

	ChimeSectorUser (char *iObjectName, char *iObjectSource, 
		iMeshWrapper *iMesh, iSector *iObjectRoom, 
		char *iObjectModel, char *iObjectMaterial=NULL);

	void HandleLeftMouseDoubleClick (iEvent &event);
	void HandleRightMouseClick (iEvent &event);
	//void UpdateEntity (csStrVector param_name, csVector param_value);
};


/******************************************************************************
 * Active sector object container
 ******************************************************************************/
class ChimeSectorActiveObject : public ChimeSectorObject
{

public:

	ChimeSectorActiveObject (char *iObjectName, char *iObjectSource, 
		iMeshWrapper *iMesh, iSector *iObjectRoom, 
		char *iObjectModel, char *iObjectMaterial=NULL);

	void HandleLeftMouseDoubleClick (iEvent &event);
	void HandleRightMouseClick (iEvent &event);
	void HandleMouseMove (csVector2 old_pos, csVector2 new_pos, iCamera *camera);
	//void UpdateEntity (csStrVector param_name, csVector param_value);
};

#endif // __ChimeSectorEntities_H__