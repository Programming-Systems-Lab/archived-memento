/*******************************************************************
    ChimeSectorObject.cpp
	Author: Mark Galagan @ 2003

	Defines a container for an active object.
	Used to associate meshes with door entities,
	as well as storing attributes associated with
	active objects and defining utility functions
	that operate on said attributes.
********************************************************************/

#include "cssysdef.h"
#include "csgeom/transfrm.h"
#include "ChimeSectorEntities.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;


/*****************************************************************
 * Constructor: creates a new container for given object mesh
 *****************************************************************/
ChimeSectorObject::ChimeSectorObject (char *iObjectName, int iObjectType = ENTITY_TYPE_OBJECT,
									  iMeshWrapper *iMesh = NULL, csVector3 *iObjectLocation = NULL, 
									  iSector *iObjectRoom = NULL, char *iObjectModel = NULL) 
	: ChimeSectorEntity (iObjectName, iObjectType)
{
	csObjectMesh = iMesh;
	csObjectLocation = iObjectLocation;
	csObjectRoom = iObjectRoom;
	strObjectModel = iObjectModel;
	strObjectSource = NULL;

	// calculate the center of the object's label
	if (iObjectLocation && iMesh)
	{
		csBox3 mesh_box;
		iMesh->GetWorldBoundingBox (mesh_box);
		csVector3 center (mesh_box.GetCenter ());
		center.y = mesh_box.MaxY ();
		csObjectLabelCenter = new csVector3 (center);
	}
	else
		csObjectLabelCenter = NULL;
}


/*****************************************************************
 * IsObjectMesh: returns true if the passed mesh is the
 * same as the one used to represent this object
 *****************************************************************/
bool ChimeSectorObject::IsObjectMesh (iMeshWrapper *iMesh)
{
	if (iMesh == csObjectMesh)
		return true;
	return false;
}


/*********************************************************************
 * SetObjectSource: sets the name of the source (URL) of this object
 *********************************************************************/
bool ChimeSectorObject::SetObjectSource (char *iObjectSource)
{
	strObjectSource = iObjectSource;
	return true;
}


/*******************************************************************
 * SetObjectModel: sets the name of the model used for this object
 *******************************************************************/
bool ChimeSectorObject::SetObjectModel (char *iObjectModel)
{
	strObjectModel = iObjectModel;
	return true;
}


/*******************************************************************
 * SetObjectLocation: sets the location of this object
 *******************************************************************/
bool ChimeSectorObject::SetObjectLocation (csVector2 newMousePosition, iCamera *camera)
{
	csVector3 newMeshPos;
	csVector3 oldMeshPos = csObjectMesh->GetMovable()->GetTransform().GetOrigin();
	csVector2 csLastMousePosition = driver->GetLastMousePosition ();

	float factor = csVector3::Norm (oldMeshPos - camera->GetTransform ().GetOrigin ());
	factor = 300/factor;
	
	newMeshPos.y = 0;
	newMeshPos.z = (newMousePosition.y - csLastMousePosition.y)/factor;
	newMeshPos.x = (newMousePosition.x - csLastMousePosition.x)/factor;
	newMeshPos = camera->GetTransform ().This2OtherRelative (newMeshPos) + oldMeshPos;

	newMeshPos = driver->GetCollider ()->CollideObject (csObjectMesh, csObjectRoom, oldMeshPos, newMeshPos);
	csObjectMesh->GetMovable()->SetPosition(newMeshPos);
	csObjectMesh->GetMovable()->UpdateMove();

	csObjectLocation = &newMeshPos;

	// recalculate object's label center
	csBox3 mesh_box;
	csObjectMesh->GetWorldBoundingBox (mesh_box);
	csVector3 center (mesh_box.GetCenter ());
	center.y = mesh_box.MaxY ();
	csObjectLabelCenter->Set (center);

	return true;
}


/*******************************************************************
 * SetObjectRoom: sets the room where this object resides
 *******************************************************************/
bool ChimeSectorObject::SetObjectRoom (iSector *iObjectRoom)
{
	csObjectRoom = iObjectRoom;
	return true;
}


/*****************************************************************
 * ActivateEntity: for this type of entity, object,
 * this function displays the contents of the source
 * represented by this object
 *****************************************************************/
bool ChimeSectorObject::ActivateEntity () 
{
	printf ("Activating object %s... \n", strEntityName);
	return true;
}

/*****************************************************************
 * SetupEntityMenu: add options for editing an active object
 *****************************************************************/
void ChimeSectorObject::SetupEntityMenu (csMenu *csEntityMenu)
{
	(void)new csMenuItem (csEntityMenu, "CHANGE MODEL", -1);
}


/*****************************************************************
 * GetEntityLabelCenter: return the center of the object's label
 *****************************************************************/
csVector3* ChimeSectorObject::GetObjectLabelCenter ()
{
	return csObjectLabelCenter;
}


/*****************************************************************
 * GetEntityLabel: return the label that tells what
 * the source of this object is
 *****************************************************************/
char* ChimeSectorObject::GetObjectLabel ()
{
	char *label = (char*) malloc (100 * sizeof (char));
	strcpy (label, "Source: ");
	if (strObjectSource)
		strcat (label, strObjectSource);
	else
		strcat (label, "<NONE>");
	return label;
}


/*****************************************************************
 * IsEntityVisible: return true if the bounding box of this
 * object's mesh is visible inside the given window rectangle
 * for given camera
 *****************************************************************/
bool ChimeSectorObject::IsObjectVisible (iCamera* camera, csRect const window_rect)
{
	if (!csObjectMesh)
		return false;

	csBox3 mesh_box;
	csBox2 screen_box;
	csObjectMesh->GetWorldBoundingBox (mesh_box);
	if (csObjectMesh->GetScreenBoundingBox (camera, screen_box, mesh_box) < 0)
		return false;
	if (window_rect.Contains (screen_box.MinX (), screen_box.MinY ()))
		return true;
	if (window_rect.Contains (screen_box.MaxX (), screen_box.MaxY ()))
		return true;

	return false;
}