/*******************************************************************
    ChimeSectorDoor.cpp
	Author: Mark Galagan @ 2003

	Defines a container for an active door.
	Used to associate door polygons with door entities,
	as well as defining utility functions of creating,
	linking and opening doors.
********************************************************************/

#include "cssysdef.h"
#include <math.h>
#include "ivideo/txtmgr.h"
#include "iengine/material.h"
#include "ChimeSectorEntities.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;


/*****************************************************************
 * Constructor: creates a new container for given door polygon
 * Its linked room is set to NULL
 *****************************************************************/
ChimeSectorDoor::ChimeSectorDoor (char *iDoorName, iMeshWrapper *mesh, 
								  iPolygon3D **door_label, 
								  int num_letters, char *iTargetName, 
								  char *iTargetSource, char *iDoorTexture) 
	: ChimeSectorEntity (iDoorName, ENTITY_TYPE_DOOR)
{
	// assign variables
	csDoorMesh = mesh;
	csDoorLabelPolygons = door_label;
	numLabels = num_letters;
	csTargetRoom = NULL;
	strDoorTexture = iDoorTexture;

	// call the function instead of just assigning,
	// to set textures of label polygons
	SetDoorTarget (iTargetName, iTargetSource);

	// Close door initially
	CloseDoor ();
}


/*****************************************************************
 * IsEntitySelected: returns true if the passed polygon is the
 * same as the one used to represent this door
 *****************************************************************/
bool ChimeSectorDoor::IsEntitySelected (iMeshWrapper *mesh)
{
	if (mesh == csDoorMesh)
		return true;
	return false;
}


/*********************************************************************
 * SetDoorTarget: sets the name of the sector this door is linked to
 *********************************************************************/
bool ChimeSectorDoor::SetDoorTarget (char *iTargetName, char *iTargetSource)
{
	strTargetName = iTargetName;
	strTargetSource = iTargetSource;

	// if target name is not NULL,
	// reassign letter textures to label polygons
	// to reflect the change
	if (strTargetName)
	{
		int numLetters = strlen (strTargetName), letter = 'a';
		csRef<iMaterialWrapper> texture = NULL;
		char txtName[9]; strcpy (txtName, "blank");

		for (int i = 0; i < numLabels; i++)
		{
			texture = driver->csEngine->GetMaterialList ()->FindByName ("blank");

			if (i < numLetters)
			{
				letter = strTargetName[i];
				if (letter < 91 && letter > 64)
					letter += 32;
				if ( (letter >= 'a' && letter <= 'z') || (letter >= '0' && letter <= '9') )
				{
					sprintf (txtName, "letter_%c", (char)letter);
					texture = driver->csEngine->GetMaterialList ()->FindByName (txtName);
				}
			}

			if (texture && csDoorLabelPolygons[i])
				csDoorLabelPolygons[i]->SetMaterial (texture);
		}
	}
	return true;
}


/*******************************************************************
 * SetTargetRoom: directly sets the target room
 *******************************************************************/
bool ChimeSectorDoor::SetTargetRoom (iSector *room)
{
	csTargetRoom = room;
	return true;
}


/*******************************************************************
 * SetDoorTexture: sets the name of the texture used for this door
 *******************************************************************/
bool ChimeSectorDoor::SetDoorTexture (char *iTexture)
{
	strcpy (strDoorTexture, iTexture);
	csRef<iMaterialWrapper> texture = driver->csEngine->
		GetMaterialList ()->FindByName (strDoorTexture);
	iPolygon3D* door = FindDoorPolygon ();
	door->SetMaterial (texture);
	return true;
}


/*****************************************************************
 * ConnectDoorToTarget: retrieves the sector to which this door
 * is linked using its name if flag is true and sets the linked
 * room to the sector's default room, or sets the linked
 * room to NULL otherwise
 *****************************************************************/
bool ChimeSectorDoor::ConnectDoorToTarget (bool doConnect, iSector* target)
{
	
	// if connecting...
	iPolygon3D* csDoorPolygon = FindDoorPolygon ();
	if (doConnect && csDoorPolygon)
	{

		if (target)
		{
			csTargetRoom = target;
			return true;
		}

		if (!strTargetName)
			return false;

		if (csDoorPolygon->GetVertexCount () < 3)
			return false;

        // get the door polygon's normal
		csPlane3 wall_plane (csDoorPolygon->GetVertex (0), csDoorPolygon->GetVertex (1), csDoorPolygon->GetVertex (2));
		wall_plane.Normalize ();
		csVector3 wall_normal (wall_plane.Normal ());
		wall_normal *= -1;
		printf("Wall normal: %f, %f, %f\n", wall_normal.x, wall_normal.y, wall_normal.z);

		// find the angles of rotation between door normal and (0, 0, 1) vector
		// these will be the angles by which new sector will be rotated
		csVector3 rot_angle (0);
		csVector3 norm (0);

		// find angle of rotation around y-axis
		norm.Set (wall_normal); norm.y = 0;
		if (norm.Norm () > 0)
            rot_angle.y = norm.z / norm.Norm ();
		else
			rot_angle.y = 0;
		printf("Rot angle: %f\n", rot_angle.y);

		// find actual angle
		rot_angle.y = -acos (rot_angle.y);
		printf("Rot angle: %f\n", rot_angle.y);
		if (wall_normal.x < 0) rot_angle.y *= -1;

		// load the sector into the system
		csVector3 origin (csDoorPolygon->GetVertex (0));
		ChimeSector *targetSector = driver->LoadNewSector (strTargetName, 
			strTargetSource, origin, rot_angle);

		// if the sector is loaded or found successfully,
		// set the linked room to be its default room
		if (targetSector)
		{
            csTargetRoom = targetSector->GetDefaultRoom ();
            return true;
		}

		// load failed, return false
		else
			return false;
	}

	// if disconnecting, set the linked room to NULL
	else
	{
        csTargetRoom = NULL;
        return true;
	}
	return false;
}


/*********************************************************************
 * OpenDoor: connects the door to the linked room and opens the door
 *********************************************************************/
bool ChimeSectorDoor::OpenDoor ()
{
	// if linked room is not NULL...
	iPolygon3D* csDoorPolygon = FindDoorPolygon ();
	if (csTargetRoom && csDoorPolygon)
	{
        // create the portal
		csDoorPolygon->CreatePortal (csTargetRoom);
		// make this door invisible
		SetDoorVisible (csDoorPolygon, false);
		// remove collider
		driver->GetCollider ()->RemoveMeshCollider (csDoorMesh);

		return true;
	}

	// otherwise, report failure
	return false;
}


/*****************************************************************
 * CloseDoor: disconnects the door and makes it non-transparent
 *****************************************************************/
bool ChimeSectorDoor::CloseDoor ()
{
	// create a collider
	driver->GetCollider ()->CreateMeshCollider (csDoorMesh);

	// make door visible
	iPolygon3D* csDoorPolygon = FindDoorPolygon ();
	if (csDoorPolygon)
	{
        csDoorPolygon->CreateNullPortal ();
        SetDoorVisible (csDoorPolygon, false);
		return true;
	}

	return false;
}


/*****************************************************************
 * SetDoorVisible: sets door visibility
 * If flag is set, makes the door visible 100%,
 * otherwise makes the door transparent (0% visibility)
 *****************************************************************/
bool ChimeSectorDoor::SetDoorVisible (iPolygon3D* door, bool flag)
{
	if (door)
	{
        if (flag)
            door->SetAlpha (100);
        else
            door->SetAlpha (0);
		return true;
	}

	return false;
}


/*****************************************************************
 * HandleLeftMouseDoubleClick: 
 * Door is opened by 1) loading the sector, 2) creating portal
 * to new sector
 *****************************************************************/
void ChimeSectorDoor::HandleLeftMouseDoubleClick (iEvent &event) 
{
	if (ConnectDoorToTarget (true))
	{
		OpenDoor ();
	}
}


/*****************************************************************
 * FindDoorPolygon: 
 * Find the polygon used for this door.
 *****************************************************************/
iPolygon3D* ChimeSectorDoor::FindDoorPolygon ()
{
	csRef<iThingState> state = SCF_QUERY_INTERFACE (csDoorMesh->GetMeshObject (), iThingState);
	if (state->GetPolygonCount () > 0)
		return state->GetPolygon (0);
	return NULL;
}


/*****************************************************************
 * HandleRightMouseClick: 
 * Menu is created.
 *****************************************************************/
void ChimeSectorDoor::HandleRightMouseClick (iEvent &event) 
{
    csMenu* menu = driver->CreateMenu (event.Mouse.x, event.Mouse.y);
	char mText [100];
	strcpy (mText, "Door: ");
	strcat (mText, strEntityName);
	(void)new csMenuItem (menu, mText, -1);
	strcpy (mText, "Linked To: ");
	strcat (mText, strTargetName);
	(void)new csMenuItem (menu, mText, -1);
	(void)new csMenuItem (menu, "", -1, CSMIS_SEPARATOR);
	(void)new csMenuItem (menu, "SET DOOR NAME", DOOR_MENU_SET_NAME);
	(void)new csMenuItem (menu, "SET TARGET", DOOR_MENU_SET_TARGET);
	(void)new csMenuItem (menu, "SET DOOR TEXTURE", DOOR_MENU_SET_TEXTURE);
	menu->SetPos (event.Mouse.x - 3, event.Mouse.y + 3);
}


/*****************************************************************
 * HandleMenuEvent: handle a menu event.
 *****************************************************************/
bool ChimeSectorDoor::HandleMenuEvent (int iMenuCode)
{
	switch (iMenuCode)
	{
	case DOOR_MENU_SET_NAME : 
		ModularWindow *w = new ModularWindow (driver->GetApplication (), "Door Window");
        w->SetSize (100, 40);
        w->Center ();
		break;
	}

	return true;
}