/*******************************************************************
    ChimeSectorObject.cpp
	Author: Mark Galagan @ 2003

	Defines a container for a sector object.
	Object is any object with dynamic bahavior.
	Some extensions include active objects, displays, etc.
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
ChimeSectorObject::ChimeSectorObject (char *iObjectName, char *iObjectSource,
									  iMeshWrapper *iMesh, csVector3 *iObjectLocation, 
									  iSector *iObjectRoom, char *sObjectModel, 
									  char *iObjectMaterial, int iEntityType)
	: ChimeSectorEntity (iObjectName, iEntityType)
{
	csObjectMesh = iMesh;
	csObjectLocation = iObjectLocation;
	csObjectRoom = iObjectRoom;
	strObjectModel = sObjectModel;
	strObjectSource = iObjectSource;
	strObjectMaterial = iObjectMaterial;

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
 * IsEntitySelected: returns true if the passed mesh is the
 * same as the one used to represent this object
 *****************************************************************/
bool ChimeSectorObject::IsEntitySelected (iMeshWrapper *mesh, iPolygon3D *polygon)
{
	if (mesh == csObjectMesh)
		return true;
	return false;
}


/*********************************************************************
 * SetObjectSource: sets the name of the source (Source) of this object
 *********************************************************************/
bool ChimeSectorObject::SetObjectSource (char *iObjectSource)
{
	strObjectSource = iObjectSource;
	return true;
}


/*********************************************************************
 * GetObjectSource: copies the source of this object
 *********************************************************************/
void ChimeSectorObject::GetObjectSource (char *iObjectSource)
{
	strcpy (iObjectSource, strObjectSource);
}


/*******************************************************************
 * SetObjectModel: sets the name of the model used for this object
 *******************************************************************/
bool ChimeSectorObject::SetObjectModel (char *iObjectModel)
{
	iMeshFactoryWrapper* factory = driver->csEngine->GetMeshFactories ()->FindByName (iObjectModel);
	if (factory)
	{
		try
		{
			csObjectMesh->SetFactory (factory);
            strObjectModel = iObjectModel;
			factory->DecRef ();
		}
		catch (...)
		{
			return false;
		}
	}

	return true;
}


/**************************************************************************
 * SetObjectMaterial: sets the material of the model used for this object
 **************************************************************************/
bool ChimeSectorObject::SetObjectMaterial (char *iObjectMaterial)
{
	iMaterialWrapper *material = driver->csEngine->GetMaterialList ()->FindByName (iObjectMaterial);
	if (material)
	{
		try
		{
			csObjectMesh->GetMeshObject ()->SetMaterialWrapper (material);
            strObjectMaterial = iObjectMaterial;
			material->DecRef ();
		}
		catch (...)
		{
			return false;
		}
	}

	return true;
}


/*******************************************************************
 * SetObjectLocation: sets the new location of the object
 *******************************************************************/
bool ChimeSectorObject::SetObjectLocation (csVector3& new_pos)
{
	csObjectMesh->GetMovable ()->SetPosition (new_pos);
	csObjectMesh->GetMovable ()->UpdateMove ();
	return true;
}


/*******************************************************************
 * SetObjectLocation: does not move as the general object cannot
 * be moved by mouse.
 *******************************************************************/
bool ChimeSectorObject::SetObjectLocation (csVector2 newMousePosition, iCamera *camera)
{
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
	strcpy (label, "  Name: ");
	if (strEntityName)
		strcat (label, strEntityName);
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


/*****************************************************************
 * HandleRightMouseClick: 
 * Menu is created.
 *****************************************************************/
void ChimeSectorObject::HandleRightMouseClick (iEvent &event) 
{
	csMenu* menu = driver->CreateMenu (event.Mouse.x, event.Mouse.y);
	char mText [100];
	strcpy (mText, "OBJECT: ");
	strcat (mText, strEntityName);
	(void)new csMenuItem (menu, mText, -1);
	menu->SetPos (event.Mouse.x - 3, event.Mouse.y + 3);
}

/*****************************************************************
 * HandleLeftMouseDoubleClick: 
 * Object is activated.
 *****************************************************************/
void ChimeSectorObject::HandleLeftMouseDoubleClick (iEvent &event) 
{
	printf ("Object is activated.\n");
}