/*******************************************************************
    ChimeSectorActiveObject.cpp
	Author: Mark Galagan @ 2003

	Defines a container for an active object.
	Used to associate meshes with object entities,
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
ChimeSectorActiveObject::ChimeSectorActiveObject (char *iObjectName, char *iObjectSource, 
												  iMeshWrapper *iMesh, iSector *iObjectRoom, 
												  char *iObjectModel, char *iObjectMaterial)
	: ChimeSectorObject (iObjectName, iObjectSource, iMesh, iObjectRoom, 
	iObjectModel, iObjectMaterial, ENTITY_TYPE_ACTIVE_OBJECT)
{
	return;
}


/*******************************************************************
 * SetObjectLocation: sets the location of this object
 *******************************************************************/
void ChimeSectorActiveObject::HandleMouseMove (csVector2 old_pos, csVector2 new_pos, iCamera *camera)
{
	csVector3 newMeshPos;
	csVector3 oldMeshPos = csObjectMesh->GetMovable()->GetTransform().GetOrigin();

	float factor = csVector3::Norm (oldMeshPos - camera->GetTransform ().GetOrigin ());
	factor = 300/factor;
	
	newMeshPos.y = 0;
	newMeshPos.z = (new_pos.y - old_pos.y)/factor;
	newMeshPos.x = (new_pos.x - old_pos.x)/factor;
	newMeshPos = camera->GetTransform ().This2OtherRelative (newMeshPos) + oldMeshPos;

	newMeshPos = driver->GetCollider ()->CollideObject (csObjectMesh, csObjectRoom, oldMeshPos, newMeshPos);
	csObjectMesh->GetMovable()->SetPosition(newMeshPos);
	csObjectMesh->GetMovable()->UpdateMove();

	// recalculate object's label center
	csBox3 mesh_box;
	csObjectMesh->GetWorldBoundingBox (mesh_box);
	csVector3 center (mesh_box.GetCenter ());
	center.y = mesh_box.MaxY ();
	csObjectLabelCenter->Set (center);
}


/*****************************************************************
 * HandleLeftMouseDoubleClick: 
 * Object is activated.
 *****************************************************************/
void ChimeSectorActiveObject::HandleLeftMouseDoubleClick (iEvent &event) 
{
	printf ("Active object is activated.\n");
}



/*****************************************************************
 * HandleRightMouseClick: 
 * Menu is created.
 *****************************************************************/
void ChimeSectorActiveObject::HandleRightMouseClick (iEvent &event) 
{
	csMenu* menu = driver->CreateMenu (event.Mouse.x, event.Mouse.y);
	char mText [100];
	strcpy (mText, "ACTIVE OBJECT: ");
	strcat (mText, strEntityName);
	(void)new csMenuItem (menu, mText, -1);
	menu->SetPos (event.Mouse.x - 3, event.Mouse.y + 3);
}
