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
#include "ChimeSectorEntities.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;


/*****************************************************************
 * Constructor: creates a new container for given door polygon
 * Its linked room is set to NULL
 *****************************************************************/
ChimeSectorDoor::ChimeSectorDoor (char *iDoorName, iPolygon3D *polygon, 
								  iPolygon3D **door_label, int num_letters,
								  char *iTargetName, char *iTargetURL, 
								  char *iDoorTexture) 
	: ChimeSectorEntity (iDoorName, ENTITY_TYPE_DOOR)
{
	// assign variables
	csDoorPolygon = polygon;
	csDoorLabelPolygons = door_label;
	numLabels = num_letters;
	csTargetRoom = NULL;
	strDoorTexture = iDoorTexture;

	// call the function instead of just assigning,
	// to set textures of label polygons
	SetDoorTarget (iTargetName, iTargetURL);
}


/*****************************************************************
 * IsEntitySelected: returns true if the passed polygon is the
 * same as the one used to represent this door
 *****************************************************************/
bool ChimeSectorDoor::IsEntitySelected (iMeshWrapper *mesh, iPolygon3D *polygon)
{
	if (polygon == csDoorPolygon)
		return true;
	return false;
}


/*********************************************************************
 * SetDoorTarget: sets the name of the sector this door is linked to
 *********************************************************************/
bool ChimeSectorDoor::SetDoorTarget (char *iTargetName, char *iTargetURL)
{
	strTargetName = iTargetName;
	strTargetURL = iTargetURL;

	// if target name is not NULL,
	// reassign letter textures to label polygons
	// to reflect the change
	if (strTargetName)
	{
		int numLetters = strlen (strTargetName), letter = 'a';
		csRef<iMaterialWrapper> texture = NULL;
		char *txtName = "letter_a";
		for (int i = 0; i < numLabels; i++)
		{
			texture = driver->csEngine->GetMaterialList ()->FindByName ("letter_blank");
			if (i < numLetters)
			{
				letter = strTargetName[i];
				if (letter < 91 && letter > 64)
					letter += 32;
				if ( (letter >= 'a' && letter <= 'z') || (letter >= '0' && letter <= '9') )
				{
					txtName[7] = (char)letter;
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
 * SetDoorTexture: sets the name of the texture used for this door
 *******************************************************************/
bool ChimeSectorDoor::SetDoorTexture (char *iTexture)
{
	strDoorTexture = iTexture;
	return true;
}


/*****************************************************************
 * ConnectDoorToTarget: retrieves the sector to which this door
 * is linked using its name if flag is true and sets the linked
 * room to the sector's default room, or sets the linked
 * room to NULL otherwise
 *****************************************************************/
bool ChimeSectorDoor::ConnectDoorToTarget (bool doConnect, iSector* target = NULL)
{
	
	// if connecting...
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

		// find actual angle
		rot_angle.y = acos (rot_angle.y);
		rot_angle.y = -rot_angle.y;

		// load the sector into the system
		ChimeSector *targetSector = driver->LoadNewSector (strTargetName, strTargetURL,
			csDoorPolygon->GetVertex (0), rot_angle);

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
	if (csTargetRoom && csDoorPolygon)
	{
        // create the portal
		csDoorPolygon->CreatePortal (csTargetRoom);
		// make this door invisible
		csDoorPolygon->SetAlpha (100);
		//SetDoorVisible (false);

		//csDoorPolygon->GetTexture ()->GetMaterialHandle ()->Prepare ();
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
	if (csDoorPolygon)
	{
        csDoorPolygon->CreatePortal (NULL);
        SetDoorVisible (true);
		return true;
	}

	return false;
}


/*****************************************************************
 * SetDoorVisible: sets door visibility
 * If flag is set, makes the door visible 100%,
 * otherwise makes the door transparent (0% visibility)
 *****************************************************************/
bool ChimeSectorDoor::SetDoorVisible (bool flag)
{
	if (csDoorPolygon)
	{
        if (flag)
            csDoorPolygon->SetAlpha (255);
        else
            csDoorPolygon->SetAlpha (0);
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
		driver->Redraw ();
	}
}



/*****************************************************************
 * HandleRightMouseClick: 
 * Menu is created.
 *****************************************************************/
void ChimeSectorDoor::HandleRightMouseClick (iEvent &event) 
{
	csMenu* menu = driver->CreateMenu (event.Mouse.x, event.Mouse.y);
	char mText [100];
	strcpy (mText, "DOOR: ");
	strcat (mText, strEntityName);
	(void)new csMenuItem (menu, mText, -1);
	menu->SetPos (event.Mouse.x - 3, event.Mouse.y + 3);
}