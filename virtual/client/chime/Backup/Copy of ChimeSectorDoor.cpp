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
#include "ChimeSectorEntities.h"
#include "ChimeSystemDriver.h"

#define DOOR_LETTER_WIDTH 0.15
#define DOOR_LETTER_HEIGHT 0.15

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;


/*****************************************************************
 * Constructor: creates a new container for given door polygon
 * Its linked room is set to NULL
 *****************************************************************/
ChimeSectorDoor::ChimeSectorDoor (iThingState* mesh, char *iDoorName, iPolygon3D *polygon, csVector3 *iDoorLocation,
								  char *iDoorTargetName, char *iDoorTexture) 
	: ChimeSectorEntity (iDoorName, ENTITY_TYPE_DOOR)
{
	csDoorPolygon = polygon;
	csTargetRoom = NULL;
	csDoorLocation = *iDoorLocation;
	SetDoorTarget (iDoorTargetName);
	strDoorTexture = iDoorTexture;

	// calculate the location of the center
	// of the label for this door
	if (polygon)
	{
		if (polygon->GetVertexCount () < 1)
		{
			csDoorLabelCenter = NULL;
			return;
		}
		float y = 0, z = 0, x = 0;
		csVector3 vertex = polygon->GetVertex (0);
		float width = 0, xmin = vertex.x, xmax = vertex.x, zmin = vertex.z, zmax = vertex.z, ymax = vertex.y;
		int minV = 0, maxV = 0;
		for (int i = 0; i < polygon->GetVertexCount (); i++)
		{
			vertex = polygon->GetVertex (i);
			y += vertex.y;
			z = vertex.z;
			x += vertex.x;
			if (xmin > vertex.x) {xmin = vertex.x; minV = i;}
			if (xmax < vertex.x) {xmax = vertex.x; maxV = i;}
			if (zmin > vertex.z) zmin = vertex.z;
			if (zmax < vertex.z) zmax = vertex.z;
			if (ymax < vertex.y) ymax = vertex.y;
		}
		csDoorLabelCenter = new csVector3 (x/polygon->GetVertexCount(), y/polygon->GetVertexCount(), z);
		width = sqrt (pow (xmax - xmin,2) + pow (zmax - zmin, 2));
		printf("xmin: %f, xmax: %f, zmin: %f, zmax: %f\n", xmin, xmax, zmin, zmax);
		printf("Width: %f\n", width);
		int numLabelPolys = width / DOOR_LETTER_WIDTH;
		if (polygon->GetVertex (minV).z > polygon->GetVertex (maxV))
		{
			int temp = zmin;
			zmin = zmax;
			zmax = temp;
		}
		csPoly3D letter (4);
		float xincr = (xmax - xmin)/numLabelPolys;
		float zincr = (zmax - zmin)/numLabelPolys;
		csDoorLabelPolygons[numLabelPolys] = NULL;
		csRef<iMaterialWrapper> material = driver->csEngine->GetMaterialList ()->FindByName ("letter_a");
		for (int i = 0; i < numLabelPolys; i++)
		{
			letter.AddVertex (xmin + xincr*i, ymax, zmin + zincr*i);
			letter.AddVertex (xmin + xincr*i, ymax + DOOR_LETTER_HEIGHT, zmin + zincr*i);
			letter.AddVertex (xmin + xincr*(i+1), ymax + DOOR_LETTER_HEIGHT, zmin + zincr*(i+1));
			letter.AddVertex (xmin + xincr*(i+1), ymax, zmin + zincr*(i+1));
			printf("Polygon %d\n", i);
			for (int j = 0; j < letter.GetVertexCount(); j++)
				printf("X=%f, Y=%f, Z=%f\n", letter.GetVertex(j)->x, letter.GetVertex(j)->y, letter.GetVertex(j)->z);
			driver->BuildWall (mesh, letter, material, csVector3 (1, 1, 1));
			letter.MakeEmpty ();
		}
	}
	else
		csDoorLabelCenter = NULL;

}


/*****************************************************************
 * IsDoorPolygon: returns true if the passed polygon is the
 * same as the one used to represent this door
 *****************************************************************/
bool ChimeSectorDoor::IsDoorPolygon (iPolygon3D *polygon)
{
	if (polygon == csDoorPolygon)
		return true;
	return false;
}


/*********************************************************************
 * SetDoorTarget: sets the name of the sector this door is linked to
 *********************************************************************/
bool ChimeSectorDoor::SetDoorTarget (char *strTargetName)
{
	strDoorTargetName = strTargetName;
	return true;
}


/*******************************************************************
 * SetDoorTexture: sets the name of the texture used for this door
 *******************************************************************/
bool ChimeSectorDoor::SetDoorTexture (char *strTexture)
{
	strDoorTexture = strTexture;
	return true;
}


/*****************************************************************
 * ConnectDoorToTarget: retrieves the sector to which this door
 * is linked using its name if flag is true and sets the linked
 * room to the sector's default room, or sets the linked
 * room to NULL otherwise
 *****************************************************************/
bool ChimeSectorDoor::ConnectDoorToTarget (bool doConnect)
{
	
	// if connecting...
	if (doConnect)
	{
		// load the sector into the system
		ChimeSector *targetSector = driver->LoadNewSector (strDoorTargetName, strDoorTargetName, csDoorLocation + csVector3 (0.1, 0.1, 0.1));

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
		SetDoorVisible (false);
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
 * ActivateEntity: for this type of entity, door,
 * this function connects to the target sector's default room
 * and opens the door
 *****************************************************************/
bool ChimeSectorDoor::ActivateEntity () 
{
	if (ConnectDoorToTarget (true))
	{
		OpenDoor ();
		return true;
	}
	else
		return false;
}


/*****************************************************************
 * SetupEntityMenu: add options for editing an active door
 *****************************************************************/
void ChimeSectorDoor::SetupEntityMenu (csMenu *csEntityMenu)
{
	(void)new csMenuItem (csEntityMenu, "OPEN DOOR", -1);
	(void)new csMenuItem (csEntityMenu, "LINK DOOR", -1);
}


/*****************************************************************
 * GetEntityLabelCenter: return the center of the door's label
 *****************************************************************/
csVector3* ChimeSectorDoor::GetEntityLabelCenter ()
{
	return csDoorLabelCenter;
}


/*****************************************************************
 * GetEntityLabel: return the label that tells where this door
 * leads to
 *****************************************************************/
char* ChimeSectorDoor::GetEntityLabel ()
{
	char *label = (char*) malloc (100 * sizeof(char));
	strcpy (label, "Door to: ");
	if (strDoorTargetName)
		strcat (label, strDoorTargetName);
	else
		strcat (label, "<NOWHERE>");
	return label;
}


/*****************************************************************
 * IsEntityVisible: return true if any of the vertices
 * of the door's polygon are visible inside the window's
 * rectangle for given camera
 *****************************************************************/
bool ChimeSectorDoor::IsEntityVisible (iCamera* camera, csRect const window_rect)
{
	if (!csDoorPolygon)
		return false;

	csVector2 screen_point;
	csVector3 camera_coordinate;
	for (int i = 0; i < csDoorPolygon->GetVertexCount (); i++)
	{
		camera_coordinate = camera->GetTransform ().Other2This (csDoorPolygon->GetVertex (i));
		if (camera_coordinate.z < 0)
			continue;
		camera->Perspective (camera_coordinate, screen_point);
		if (window_rect.Contains (screen_point.x, screen_point.y))
			return true;
	}

	return false;
}