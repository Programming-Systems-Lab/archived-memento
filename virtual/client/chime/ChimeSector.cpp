/*******************************************************************
    ChimeSector.cpp
	Author: Mark Galagan @ 2002

	Container class that defines a single location entity
	for CHIME called ChimeSector. It contains all the
	rooms of a single, sector, objects, doors, etc. inside
	the sector, as well as methods used to manipulate
	the sector as a single entity.
********************************************************************/

#include "cssysdef.h"
#include "cssys/sysfunc.h"
#include "iutil/vfs.h"
#include "csutil/cscolor.h"
#include "cstool/csview.h"
#include "cstool/initapp.h"
#include "iutil/eventq.h"
#include "iutil/event.h"
#include "iutil/objreg.h"
#include "iutil/csinput.h"
#include "iutil/virtclk.h"
#include "iengine/sector.h"
#include "iengine/engine.h"
#include "iengine/camera.h"
#include "iengine/light.h"
#include "iengine/statlght.h"
#include "iengine/texture.h"
#include "iengine/mesh.h"
#include "iengine/movable.h"
#include "iengine/material.h"
#include "imesh/thing/polygon.h"
#include "imesh/thing/thing.h"
#include "imesh/thing/portal.h"
#include "imesh/thing/lightmap.h"
#include "igeom/polymesh.h"
#include "csgeom/pmtools.h"
#include "imesh/sprite3d.h"
#include "imesh/object.h"
#include "imesh/ball.h"
#include "ivideo/graph3d.h"
#include "ivideo/graph2d.h"
#include "ivideo/txtmgr.h"
#include "ivideo/texture.h"
#include "ivideo/material.h"
#include "ivideo/fontserv.h"
#include "igraphic/imageio.h"
#include "imap/parser.h"
#include "ivaria/reporter.h"
#include "ivaria/stdrep.h"
#include "ivaria/dynamics.h"
#include "csutil/cmdhelp.h"
#include "igeom/polymesh.h"

#include "ChimeSystemDriver.h"
#include "ChimeSector.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

/****************************************************
 * Constructor simply initializes all the variables
 ****************************************************/
ChimeSector::ChimeSector(char *strISectorName, char *strISectorSource)
{
	strSectorName = strISectorName;
	strSectorSource = strISectorSource;
	chEntranceRoom = chCurrentRoom = NULL;
	chRooms = new csVector ();
	chActiveEntities = new csVector ();
	csDefaultLocation = csVector3 (0);
	chActiveScreen = NULL;
}

/*****************************************************
 * Destructor frees up all the rooms
 *****************************************************/
ChimeSector::~ChimeSector()
{

}

/***********************************************************************
 * Adds a static light in a given room, at a given location
 * and scaled appropriately
 ***********************************************************************/
bool ChimeSector::AddLight (csVector3 const &location, csColor color,
							float const scale, iSector* room)
{
	//csRef<iDynLight> dLight = driver->csEngine->CreateDynLight (location, scale, color);
	csRef<iStatLight> csLight = driver->csEngine->CreateLight (NULL, location, scale,
  								  color, true);
	room->GetLights ()->Add (csLight->QueryLight ());

	return true;
}


/************************************************************************
 * Adds a mesh object from the supplied object factory to the given
 * room at a given location
 ************************************************************************/
iMeshWrapper* ChimeSector::AddMeshObject (char *strObjectName, char *strFactoryName,
							 iSector* room, csVector3 const &location)
{
    // make sure object with same name does not exist
	if (room->GetMeshes ()->FindByName (strObjectName))
		return NULL;
	
	//Find the mesh factory
	iMeshFactoryWrapper* factory = driver->csEngine->GetMeshFactories ()->FindByName (strFactoryName);
	if (!factory)
	{
		driver->Report ("Chime.Application.ChimeSector",
						"Error finding mesh object factory!");
		return NULL;
	}
	
	// Create the mesh.
	csRef<iMeshWrapper> mesh (driver->csEngine->CreateMeshWrapper (factory, strObjectName, room));
	mesh->DeferUpdateLighting (CS_NLIGHT_STATIC|CS_NLIGHT_DYNAMIC, 10);
	mesh->GetMovable ()->SetPosition (room, location);
	mesh->GetMovable ()->UpdateMove ();

	// Create a collider for the mesh
	driver->GetCollider ()->CreateMeshCollider (mesh);

	return mesh;
}


/*************************************************************************
 * Build a door entity at given origin
 *************************************************************************/
bool ChimeSector::AddDoorEntity (csVector3 *vOrigin, char* strRoomName, 
								 char *strDoorName, char *strTargetName, 
								 char *strTargetSource, char *strDoorTexture, 
								 csVector3* vTxtSize)
{
    // find room where the door is added
    iSector* room = FindRoom (strRoomName);
    if (!room) return false;

    // prepare all needed variables
    iPolygon3D* door = NULL;
    iPolygon3D** labels = NULL;
    int num_letters = 0;
    csVector3 v (0, 0, 0);

    // find door origin
	if (vOrigin == NULL) return false;

    // name the door
    static int doorNum = 0;
    doorNum++;
    char door_name[10];
    sprintf(door_name, "door%d", doorNum);

    // create door mesh
    csRef<iMeshWrapper> mesh = driver->csEngine->CreateThingMesh (room, door_name);
    mesh->SetZBufMode (CS_ZBUF_USE);

    // build the door
    door = BuildDoor (room, mesh, *vOrigin, strDoorTexture, vTxtSize);
    if (!door) return false;

    // build door labels
    labels = AddPolygonLabel (room, door, num_letters);
    if (!labels) return false;

	// create new active door
    chActiveEntities->Push (new ChimeSectorDoor (strDoorName, mesh,
        labels, num_letters, strTargetName, strTargetSource, strDoorTexture)
		);

    // shine lights on the door
    driver->ShineLights (room, mesh);

	// shine lights on the walls, but first close
	// all portals so as not to affect other rooms,
	// and then reopen them
	// @@@ HACK
    mesh = room->GetMeshes ()->FindByName ("walls");
	csRef<iThingState> wall_state = SCF_QUERY_INTERFACE (mesh->GetMeshObject (), iThingState);
	int num_portals = wall_state->GetPortalCount ();
	iSector **portals = (iSector**) malloc (num_portals * sizeof (iSector));
	
	// set all portals to NULL, but remember their destination
	for (int i = 0; i < num_portals; i++)
	{
		portals[i] = wall_state->GetPortal (i)->GetSector ();
		wall_state->GetPortal (i)->SetSector (NULL);
	}

	// shine lights
    driver->ShineLights (room, mesh);

	// reset portals to their destinations
	for (int i = 0; i < num_portals; i++)
	{
		wall_state->GetPortal (i)->SetSector (portals[i]);
	}

	// shine lights on label mesh
    mesh = room->GetMeshes ()->FindByName ("labels");
    driver->ShineLights (room, mesh);

	return true;
}


/*************************************************************************
 * Build an entrance door entity from given polygon definition
 *************************************************************************/
bool ChimeSector::AddDoorEntity (csPoly3D &door, char *strDoorTexture, csVector3 *vTxtSize, 
					iSector* thisRoom, iSector* roomConnection)
{
    if (!thisRoom)
		return false;
	
	// make door mesh
    csRef<iMeshWrapper> mesh = driver->csEngine->CreateThingMesh (thisRoom, "entrance");
    mesh->SetZBufMode (CS_ZBUF_USE);
	int num_letters = 0;

    // create door polygon
    iPolygon3D* door_poly = BuildDoor (thisRoom, mesh, door, strDoorTexture, vTxtSize);

    // create door label polygons
    iPolygon3D** door_label = AddPolygonLabel (thisRoom, door_poly, num_letters);
    ChimeSectorDoor *d = NULL;

    // if there is an entrance room,
    // find corresponding ChimeSector and build the door
    if (roomConnection && driver->FindSectorByRoom (roomConnection))
	{
        char s_name[50], s_source[100];
        driver->FindSectorByRoom (roomConnection)->GetSectorTitle (s_name, s_source);
        d = new ChimeSectorDoor ("entrance", mesh, door_label, num_letters, 
			s_name, s_source, strDoorTexture);
        d->SetTargetRoom (roomConnection);
        d->OpenDoor ();
	}

    // if there is no entrance room, build a door that
    // leads to nowhere
    else
	{
        d = new ChimeSectorDoor ("entrance", mesh, door_label, 
			num_letters, "none", "none", strDoorTexture);
	}

    // add door to the list of active entities
    chActiveEntities->Push (d);
	return true;
}


/******************************************************************************
 * Same as BuildWall, except this functions first finds appropriate
 * material and texture size.
 ******************************************************************************/
iPolygon3D* ChimeSector::BuildWall (iThingState *walls, csPoly3D const &vertices,
						 char *strTextureName, csVector3 *textureSize,
						 char* strPolygonName)
{
	// get the texture
	csRef<iMaterialWrapper> material = NULL;
	if (strTextureName)
		material = driver->csEngine->GetMaterialList ()->FindByName (strTextureName);
	else
		material = driver->csEngine->GetMaterialList ()->FindByName ("blank");
	if (!material)
		return NULL;

	// get texture size
	if (textureSize)
		return BuildWall (walls, vertices, material, textureSize, strPolygonName);
	else
		return BuildWall (walls, vertices, material, new csVector3 (1, 1, 1), strPolygonName);

	return NULL;
}


/******************************************************************************
 * Build a wall for given array of vertices, given in the csPoly3D object
 * Urepeat and vrepeat refer to u and v scale (repetition) of the texture
 * Urepeat = textureSize.x & vrepeat = textureSize.z
 ******************************************************************************/
iPolygon3D* ChimeSector::BuildWall (iThingState *walls, csPoly3D const &vertices,
						 iMaterialWrapper *material, csVector3 *textureSize,
						 char* strPolygonName)
{
	// make sure all parameters are not NULL
	if (!walls || !material || !textureSize)
		return NULL;

	// create a polygon
	iPolygon3D* p = walls->CreatePolygon (strPolygonName);

	// create polygon vertices
	for (int i=0; i<vertices.GetVertexCount(); i++)
		p->CreateVertex(*(vertices.GetVertex(i)));

	// set the material
	p->SetMaterial (material);

	// map the texture
	if (p->GetVertexCount () > 3)
		p->SetTextureSpace (p->GetVertex (1), p->GetVertex (2), textureSize->x, p->GetVertex (0), textureSize->y);
	else
		return NULL;

	return p;
}

/******************************************************************************
 * Build a door for given array of vertices, connecting to given room
 * and set to transparency alpha (0 for transparent, 255 for non-transparent)
 ******************************************************************************/
iPolygon3D* ChimeSector::BuildEntrance (iThingState *walls, csPoly3D const &vertices,
						 char *strTextureName, csVector3 *txtSize,
						 iSector *room, int alpha)
{
	// build the polygon for the door (just use BuildWall, its the same thing)
	iPolygon3D *p = BuildWall (walls, vertices, strTextureName, txtSize);
	if (!p)
		return NULL;

	// set this polygon as a portal to the given room
	if (room)
        p->CreatePortal (room);
	else
		p->CreateNullPortal ();

	// set transparency for this polygon
	p->SetAlpha (alpha);

	return p;
}


/******************************************************
 * Simple function that validates a polygon
 * as a wall that can be used for building
 * a door. Criteria are:
 * 1) 4 corners
 * 2) Large enough height and width
 * 3) The wall is vertical
 ******************************************************/
bool IsWallValidForDoor (const csVector3 *origin, iPolygon3D* wall)
{
	// calculate number of corners
	int corners = wall->GetVertexCount ();

	// calculate wall height
	float height = wall->GetVertex (1).y - wall->GetVertex (0).y;

	// calculate wall width
	float width = sqrt (pow (wall->GetVertex (0).x - wall->GetVertex (3).x, 2) +
		pow (wall->GetVertex (0).z - wall->GetVertex (3).z, 2));

	// see if the wall is vertical
	float max_diff = 1;
	bool isVertical = (
		ABS (wall->GetVertex (0).x - wall->GetVertex (1).x) < max_diff
		&&
		ABS (wall->GetVertex (0).z - wall->GetVertex (1).z) < max_diff
		&&
		ABS (wall->GetVertex (2).x - wall->GetVertex (3).x) < max_diff
		&&
		ABS (wall->GetVertex (2).x - wall->GetVertex (3).x) < max_diff
		);

	// make sure all criteria are satisfied
	if (corners == 4 && height >= DOOR_HEIGHT
		&& width >= DOOR_WIDTH && isVertical)
		return true;

	return false;
}


/***********************************************************
 * Build door takes the location of an object,
 * finds the closest wall to that location,
 * cuts a whole in that wall the size of a door
 * and builds the door.
 ***********************************************************/
iPolygon3D* ChimeSector::BuildDoor (iSector* room, iMeshWrapper* door_mesh, 
									csVector3 const &origin, char *strDoorTexture, 
									csVector3 *textSize)
{
	// find wall mesh wrapper
	csRef<iMeshWrapper> wall_mesh = room->GetMeshes ()->FindByName ("walls");
	if (!wall_mesh) return NULL;

	// find thing state
	csRef<iThingState> wall_state (SCF_QUERY_INTERFACE (wall_mesh->GetMeshObject (),
		iThingState));
	if (!wall_state) return NULL;

	// find closest polygon & point of intersection
	csVector3 door_origin (0, 0, 0);
	iPolygon3D* wall = FindClosestPolygon (wall_state, origin, &door_origin, IsWallValidForDoor);
	if (!wall) return NULL;

	// find the right lower corner of the door
	csVector3 door_bottom (wall->GetVertexW (3) - door_origin);
	door_bottom.y = 0;
	door_bottom *= DOOR_WIDTH / door_bottom.Norm ();
	csVector3 door_right (door_origin + door_bottom);

	// set door polygon
	csPoly3D door_poly (4);
	door_poly.AddVertex (door_origin);
	door_poly.AddVertex (door_origin + csVector3 (0, DOOR_HEIGHT, 0));
	door_poly.AddVertex (door_right + csVector3 (0, DOOR_HEIGHT, 0));
	door_poly.AddVertex (door_right);

	// build the door
	return BuildDoor (room, door_poly, wall, door_mesh, strDoorTexture, textSize);
}


/************************************************************************************
 * Build the actual door. First, cut a hole in the wall, then build
 * the polygon for the door
 ************************************************************************************/
iPolygon3D* ChimeSector::BuildDoor (iSector* room, const csPoly3D &door, 
									iPolygon3D* wall, iMeshWrapper* door_mesh, 
									char *strDoorTexture, csVector3 *textSize)
{
	// cut the hole in the wall
	csRef<iMeshWrapper> wall_mesh = room->GetMeshes ()->FindByName ("walls");

	// remove walls collider
	driver->GetCollider ()->RemoveMeshCollider (wall_mesh);

	// get iThingState for wall mesh
	csRef<iThingState> state = SCF_QUERY_INTERFACE (wall_mesh->GetMeshObject (), iThingState);

	// make a hole in the wall where the door will be
	MakeHoleInWall (door, state, wall);

	// add walls collider
	driver->GetCollider ()->CreateMeshCollider (wall_mesh);

	// update lights in the room to force a relight
	iLight *light = NULL;
	for (int i = 0; i < room->GetLights ()->GetCount (); i++)
	{
		light = room->GetLights ()->Get (i);
		light->SetColor (light->GetColor ());
	}

	// find door mesh
	if (!door_mesh) return NULL;
	state = SCF_QUERY_INTERFACE (door_mesh->GetMeshObject (), iThingState);
	if (!state) return NULL;

	// build door
	return BuildWall (state, door, strDoorTexture, textSize);
}


/***********************************************************
 * Build door for given door polygon and door texture
 ***********************************************************/
iPolygon3D* ChimeSector::BuildDoor (iSector* room, iMeshWrapper* door_mesh, 
									csPoly3D const &door, char *strDoorTexture, 
									csVector3 *textSize)
{
	// find wall mesh wrapper
	csRef<iMeshWrapper> wall_mesh = room->GetMeshes ()->FindByName ("walls");
	if (!wall_mesh) return NULL;

	// find thing state
	csRef<iThingState> wall_state (SCF_QUERY_INTERFACE (wall_mesh->GetMeshObject (),
		iThingState));
	if (!wall_state) return NULL;

	// find the wall where the door is
	iPolygon3D* wall = NULL;
	for (int i = 0; i < wall_state->GetPolygonCount (); i++)
	{
		if (wall_state->GetPolygon (i)->PointOnPolygon (*(door.GetVertex (1))))
			wall = wall_state->GetPolygon (i);
	}
	if (!wall) return NULL;

	// build the door
	return BuildDoor (room, door, wall, door_mesh, strDoorTexture, textSize);
}


/*********************************************************************************
 * Make a hole in the given wall where the hole's bottom left corner,
 * bottom right corner and height are given. The original wall is removed
 *********************************************************************************/
void ChimeSector::MakeHoleInWall (csPoly3D const &hole, iThingState *walls, 
								  iPolygon3D *wall)
{
	// get original variables
	csPoly3D vertices (4);
	csVector3 temp (0, 0, 0);
	csVector3 txtSize (1, 1, 1);
	csRef<iMaterialWrapper> material = wall->GetMaterial ();
	iPolygon3D* np = NULL;

	// make original wall the left side of the hole
	vertices.AddVertex (wall->GetVertexW (0));
	vertices.AddVertex (wall->GetVertexW (1));
	temp.Set (*(hole.GetVertex (0))); temp.y = wall->GetVertexW (2).y;
	vertices.AddVertex (temp);
	temp.y = wall->GetVertexW (3).y;
	vertices.AddVertex (temp);
	np = BuildWall (walls, vertices, material, &txtSize);
	wall->CopyTextureType (np);

	// make right side of the hole
	vertices.MakeEmpty ();
	temp.Set (*(hole.GetVertex (3))); temp.y = wall->GetVertexW (3).y;
	vertices.AddVertex (temp);
	temp.y = wall->GetVertexW (2).y;
	vertices.AddVertex (temp);
	vertices.AddVertex (wall->GetVertexW (2));
	vertices.AddVertex (wall->GetVertexW (3));
	np = BuildWall (walls, vertices, material, &txtSize);
	wall->CopyTextureType (np);

	// make bottom side of the hole
	vertices.MakeEmpty ();
	temp.Set (*(hole.GetVertex (0))); temp.y = wall->GetVertexW (0).y;
	vertices.AddVertex (temp);
	temp.y = (*(hole.GetVertex (0))).y;
	vertices.AddVertex (temp);
	temp.Set (*(hole.GetVertex (3)));
	vertices.AddVertex (temp);
	temp.y = wall->GetVertexW (3).y;
	vertices.AddVertex (temp);
	np = BuildWall (walls, vertices, material, &txtSize);
	wall->CopyTextureType (np);

	// make top side of the hole
	vertices.MakeEmpty ();
	temp.Set (*(hole.GetVertex (1)));
	vertices.AddVertex (temp);
	temp.y = wall->GetVertexW (1).y;
	vertices.AddVertex (temp);
	temp.Set (*(hole.GetVertex (3))); temp.y = wall->GetVertexW (2).y;
	vertices.AddVertex (temp);
	temp.Set (*(hole.GetVertex (2)));
	vertices.AddVertex (temp);
	np = BuildWall (walls, vertices, material, &txtSize);
	wall->CopyTextureType (np);

	// remove original wall
	walls->RemovePolygon (walls->FindPolygonIndex (wall));
}


/***************************************************************************************
 * SetupSector is the real constructor.
 * It initializes the whole sector based on a description
 * in the XML file. SetupSector builds the rooms, objects,
 * and doors from that description. It also sets up
 * the dynamic aspects of the sector.
 ***************************************************************************************/
ChimeSector* ChimeSector::SetupSector(csVector3 &origin, csVector3 const &rotation,
									  char* strSectorXMLFile, char *strISectorName, 
									  char *strISectorSource, iSector *iEntranceRoom)
{

	// Create new ChimeSector
	ChimeSector *sector  = new ChimeSector (strISectorName, strISectorSource);

	// get the structure that defines this sector for this definition file
	chSectorStructPtr sectorStruct = ParseSectorDefinition (strSectorXMLFile);
	if (!sectorStruct) {
		char *errorMsg = "Cannot parse file definition ";
		strcat (errorMsg, strSectorXMLFile);
		strcat (errorMsg, "\n");
		driver->Report ("crystalspace.application.ChimeSector", errorMsg);
		return NULL;
	}

	// process structure coordinates to update them, reflecting
	// given origin and rotation angle
    ProcessSectorDefinition (sectorStruct, origin, rotation);

	// determine the full name of the region
	// that needs to be created for this sector
	char regionName[150];
	ChimeSector::GetRegionName (strISectorName, strISectorSource, regionName);
	
	// build the actual sector
	sector->BuildSector (sectorStruct, regionName, iEntranceRoom);

	// set current room and default location
	//sector->chCurrentRoom = sector->GetDefaultRoom ();
	sector->csDefaultLocation = sectorStruct->defaultLocation;

	if (!sector->GetDefaultRoom ())
		return NULL;

	return sector;
}

/***********************************************************
 * BuildSector builds the actual sector from the structure
 * provided by the XML parser in variable sector
 ***********************************************************/
bool ChimeSector::BuildSector (chSectorStructPtr sectorStruct, char *strRegionName, 
							   iSector* iEntranceRoom)
{
	// return value
	bool rv = true;
	int j = 0, i = 0;

	// create a region for this sector with this name
	driver->csEngine->SelectRegion (strRegionName);
	iRegion *region = driver->csEngine->GetRegions ()->FindByName (strRegionName);

	// build all the rooms in this new region
	// (they will automatically be assigned to this new region)
	for (i = 0; i < sectorStruct->numRooms; i++)
		if (!BuildRoom (sectorStruct->rooms[i]))
			rv = false;

	chCurrentRoom = GetDefaultRoom ();
	csDefaultLocation = sectorStruct->defaultLocation;

	// build doors (we can build doors only now,
	// after all the rooms were created so that we can
	// point these doors to existing rooms)
	chDoorStructPtr door;
	csRef<iMaterialWrapper> doorMaterial;
	iSector *roomConnection, *thisRoom;
	csRef<iMeshWrapper> door_mesh;
	csRef<iThingState> doors_state;
	for (j = 0; j < sectorStruct->numRooms; j++)
	{
		// find the pointer to this room where the door is
		thisRoom = driver->csEngine->FindSector (sectorStruct->rooms[j]->strRoomName, region);
		if (!thisRoom)
			continue;

		// get the door mesh for this room
		door_mesh = thisRoom->GetMeshes ()->FindByName ("walls");
		if (!door_mesh)
			continue;

		// get the door state (necessary for building)
		doors_state = SCF_QUERY_INTERFACE (door_mesh->GetMeshObject (), iThingState);
		if (!doors_state)
			continue;

		// build doors
		for (i = 0; i < sectorStruct->rooms[j]->numDoors; i++) {

			// get the door structure definition
			door = sectorStruct->rooms[j]->doors[i];

			// see if this is the entrance door
			if (strcmp (door->strRoomName, "entrance") &&
				strcmp (door->strRoomName, "default"))
			{
                // find the connecting room
				roomConnection = driver->csEngine->FindSector (door->strRoomName, region);
				// build the door

                if (!BuildEntrance (doors_state, door->door, door->strDoorTexture, 
                    door->txtSize, roomConnection, door->alpha))
                    rv = false;
			}
			// if it is the entrance door, build an active door
			else
			{
				rv = AddDoorEntity (door->door, door->strDoorTexture, door->txtSize,
					thisRoom, iEntranceRoom);
			}
		}

		// shine lights in the room
		driver->ShineLights (thisRoom);
	}

	return rv;
}

/***********************************************************
 * BuildSector builds the actual sector from the structure
 * provided by the XML parser in variable sector
 ***********************************************************/
iSector* ChimeSector::BuildRoom (chRoomStructPtr roomStruct)
{
	// create a iSector room
	csRef<iSector> room = driver->csEngine->CreateSector(roomStruct->strRoomName);
	chRooms->Push (room);
	
	// initialize a mesh for the walls
	csRef<iMeshWrapper> walls (driver->csEngine->CreateSectorWallsMesh (room, "walls"));
	walls->SetZBufMode (CS_ZBUF_USE);
	csRef<iThingState> walls_state (SCF_QUERY_INTERFACE (walls->GetMeshObject (),
		iThingState));

	// add a mesh for AI2TV screens, and leave it empty for now
	// it will be used later for building AI2TV screens
	csRef<iMeshWrapper> other_mesh = driver->csEngine->CreateSectorWallsMesh (room, "screens");
	other_mesh->SetZBufMode (CS_ZBUF_USE);

	// add a mesh for door labels, and leave it empty for now
	// it will be used later for building door labels
	other_mesh = driver->csEngine->CreateSectorWallsMesh (room, "labels");
	other_mesh->SetZBufMode (CS_ZBUF_USE);

	// declare some variables
	int i = 0;
	chWallStructPtr wall;

	// build walls
	for (i = 0; i < roomStruct->numWalls; i++) {

		// get the wall structure definition
		wall = roomStruct->walls[i];
		// build the wall
		BuildWall (walls_state, wall->wall, wall->strWallTexture, &(wall->txtSize));

	}

	// also, finally add collision detection to each room's walls
	driver->GetCollider()->CreateMeshCollider (walls);

	// add lights
	chLightStructPtr light;
	for (i = 0; i < roomStruct->numLights; i++) {

		// get the light
		light = roomStruct->lights[i];

		// add the light
		AddLight (light->location, light->color, light->intensity, room);
	}

	return room;
}


/**********************************************************************
 * Build a screen polygon on the closest wall
 **********************************************************************/
void ChimeSector::BuildScreen (csOrthoTransform const &transform)
{

	// find wall mesh wrapper
	csRef<iMeshWrapper> screen_mesh = chCurrentRoom->GetMeshes ()->FindByName ("screens");
	if (!screen_mesh)
		return;

	// find thing state
	csRef<iThingState> screen_state (SCF_QUERY_INTERFACE (screen_mesh->GetMeshObject (),
		iThingState));
	if (!screen_state)
		return;

	// delete all polygons (any previous screens)
	screen_state->RemovePolygons ();

	// move transform 1 step forward
	csVector3 up_pos (transform.This2OtherRelative (5 * CS_VEC_FORWARD) + transform.GetOrigin ());

	// move transform 2 steps left
	csVector3 left_pos (transform.This2OtherRelative (csVector3 (-1.5, 0, 0)) + up_pos);

	// move transform 2 steps right
	csVector3 right_pos (transform.This2OtherRelative (csVector3 (1.5, 0, 0)) + up_pos);

	// move 0.1 step forward (for background screens)
	csVector3 up_step (transform.This2OtherRelative (csVector3 (0, 0, 0.1)));

	// texture size
	csVector3 txtSize (3, 2, 1);

	// create csPoly3D as a list of vertices for the front screen
	csPoly3D screen (4);
	screen.AddVertex (left_pos);
	screen.AddVertex (left_pos + csVector3 (0, 2, 0));
	screen.AddVertex (right_pos + csVector3 (0, 2, 0));
	screen.AddVertex (right_pos);

	// create the screen
	BuildWall (screen_state, screen, "blank", &txtSize, "screen_front");

	// now create the back screen
	left_pos += up_step;
	right_pos += up_step;
	screen.MakeEmpty ();
	screen.AddVertex (right_pos);
	screen.AddVertex (right_pos + csVector3 (0, 2, 0));
	screen.AddVertex (left_pos + csVector3 (0, 2, 0));
	screen.AddVertex (left_pos);
	BuildWall (screen_state, screen, "ai2tv_ready", &txtSize, "screen_back");

	// shine lights
	driver->ShineLights (chCurrentRoom, screen_mesh);
}


/**********************************************************************
 * Display image as texture on the screen
 **********************************************************************/
bool ChimeSector::DisplayImageOnScreen (iMaterialWrapper* image)
{
	// find wall mesh wrapper
	csRef<iMeshWrapper> screen_mesh = driver->csEngine->FindMeshObject ("screens");
	if (!screen_mesh)
		return false;

	// find thing state
	csRef<iThingState> screen_state (SCF_QUERY_INTERFACE (screen_mesh->GetMeshObject (),
		iThingState));
	if (!screen_state)
		return false;

	// make sure screens exist
	if (screen_state->GetPolygonCount () < 2)
		return false;

    // assign frames as material to front screen
	csRef<iMaterialWrapper> temp = driver->csEngine->FindMaterial ("door_text");
	screen_state->GetPolygon (0)->SetMaterial (image);
	screen_state->GetPolygon (1)->SetMaterial (image);

	return true;
}


/**********************************************************************
 * Return the first entity that intersects the path from start to end
 **********************************************************************/
ChimeSectorEntity* ChimeSector::SelectEntity (const csVector3 &start, const csVector3 &end, csVector3 &isect)
{
	// Prepare variables used to hold objects hit by the beam.
	// HitBeam only returns the mesh that was hit by the beam.
	iMeshWrapper *mesh = NULL;

	// Intersect the beam to find the mesh
	mesh = chCurrentRoom->HitBeam (start, end, isect, NULL);
	
	// If no mesh was found, return nothing
	if (!mesh)
		return NULL;

	// Prepare pointers for possible active entities
	ChimeSectorEntity *entity = NULL;

	// Compare all entities
	for (int i = 0; i < chActiveEntities->Length (); i++)
	{
		entity = (ChimeSectorEntity*) chActiveEntities->Get (i);
		if (entity->IsEntitySelected (mesh))
			return entity;
	}

	return NULL;
}


/**************************************************************************************
 * Build a set of small polygons at the top of an outer door to display
 * the target of the door to serve as the door label. Return the array of
 * polygon pointers. Polygons initially have some default texture (all black)
 * which will be replaced with the door target later.
 ***************************************************************************************/
iPolygon3D** ChimeSector::AddPolygonLabel (iSector* room, iPolygon3D *door_polygon, 
										   int &num_letters)
{

	csRef<iMeshWrapper> wall_mesh = room->GetMeshes ()->FindByName ("labels");
	if (!wall_mesh) return false;
    csRef<iThingState> walls (SCF_QUERY_INTERFACE (wall_mesh->GetMeshObject (), iThingState));
    if (!walls) return false;

	// the width and height of a single polygon used for a single letter
	// are pre-calculated and are set here, but can be changed if texture
	// size changes
	float LETTER_WIDTH = 0.15;
	float LETTER_HEIGHT = 0.15;

	iPolygon3D** label;
	num_letters = 0;
	
	// make sure no input is null
	if (door_polygon)
	{
		// we cannot define a top for polgons
		// with no vertices
		if (door_polygon->GetVertexCount () <= 1)
		{
			return NULL;
		}

		// initialize variables for calculating some
		// dimensions of the door polygon, e.g. width and top coordinate
		csVector3 vertex = door_polygon->GetVertex (0);
		float width = 0, xmin = vertex.x, xmax = vertex.x, zmin = vertex.z, zmax = vertex.z, ymax = vertex.y;

		// calculate the variables initialized above
		for (int i = 0; i < door_polygon->GetVertexCount (); i++)
		{
			vertex = door_polygon->GetVertex (i);
			if (xmin > vertex.x) xmin = vertex.x;
			if (xmax < vertex.x) xmax = vertex.x;
			if (zmin > vertex.z) zmin = vertex.z;
			if (zmax < vertex.z) zmax = vertex.z;
			if (ymax < vertex.y) ymax = vertex.y;
		}

		// calculate width available for the label as the
		// distance between two points with maximum and minimum
		// x and z values (ignore y)
		width = sqrt (pow (xmax - xmin,2) + pow (zmax - zmin, 2));

		// number of polygons that will fit in this width
		// depends opnly on the width of each polygon
		int numLabelPolys = (int) (width / LETTER_WIDTH);

		// calculate coordinate increment for polygons
		float xincr = (xmax - xmin)/numLabelPolys;
		float zincr = (zmax - zmin)/numLabelPolys;

		// create an array of polygon pointers of the size
		// that was just calculated
		label = (iPolygon3D**) malloc (numLabelPolys * sizeof(iPolygon3D));

		// the label polygons must start at the top left corner
		// of the door for left-to-right reading, assumed to be the
		// point at the top of the door (maxy) with minimum x and z
		// coordinates.
		// but if the door is oriented such that its top leftmost corner does
		// not have the minimum z (like a door on a right wall), switch
		// zmin and zmax so that first letter is placed appropriately
		if (door_polygon->GetVertexCount () > 2)
		{
			csPlane3 wall_plane (door_polygon->GetVertex (0), door_polygon->GetVertex (1), door_polygon->GetVertex (2));
			wall_plane.Normalize ();
			csVector3 wall_normal (wall_plane.Normal ());
			if (wall_normal.x < 0)
			{
				int temp = zmin;
				zmin = zmax;
				zmax = temp;
				zincr = -zincr;
				xmin -= 0.02;
				xmax -= 0.02;
			}
			if (wall_normal.x > 0)
			{
				xmin += 0.02;
				xmax += 0.02;
			}
			if (wall_normal.z > 0)
			{
				int temp = xmin;
				xmin = xmax;
				xmax = temp;
				xincr = -xincr;
				zmin += 0.02;
				zmax += 0.02;
			}
			if (wall_normal.z < 0)
			{
				zmin -= 0.02;
				zmax -= 0.02;
			}

		}

		// create a holder for one polygon
		csPoly3D letter (4);

		// texture size
		csVector3 txtSize (0.3, 0.3, 1);

		// create each polygon
		for (int i = 0; i < numLabelPolys; i++)
		{
			letter.AddVertex (xmin + xincr*i, ymax, zmin + zincr*i);
			letter.AddVertex (xmin + xincr*i, ymax + LETTER_HEIGHT, zmin + zincr*i);
			letter.AddVertex (xmin + xincr*(i+1), ymax + LETTER_HEIGHT, zmin + zincr*(i+1));
			letter.AddVertex (xmin + xincr*(i+1), ymax, zmin + zincr*(i+1));
			label[i] = BuildWall (walls, letter, "blank", &txtSize);
			letter.MakeEmpty ();
		}

		// set up a light by the label
		//AddLight (csVector3 (xmin, ymax, zmin), csColor (1, 1, 1), 5.0, room);
		//AddLight (csVector3 (xmax, ymax, zmax), csColor (1, 1, 1), 5.0, room);

		// return
		num_letters = numLabelPolys;
		return label;
	}

	return NULL;
}


/**************************************************************************************
 * Return all the active entities in this sector
 ***************************************************************************************/
csVector* ChimeSector::GetActiveEntities ()
{
	return chActiveEntities;
}


/******************************************************
 * Return the current room where the user is
 ******************************************************/
iSector* ChimeSector::GetCurrentRoom()
{
	return chCurrentRoom;
}

/******************************************************
 * Set the current room where the user is
 ******************************************************/
void ChimeSector::SetCurrentRoom(iSector *room)
{
	chCurrentRoom = room;
}


/******************************************************
 * Get the default room
 ******************************************************/
iSector* ChimeSector::GetDefaultRoom()
{
	iSector *room = FindRoom ("default");
	if (!room)
		room = FindRoom ("main");
	return room;
}


/******************************************************
 * Return true if this is the sector with given name
 * and Source, false otherwise
 ******************************************************/
bool ChimeSector::IsThisSector(char *iSectorName, char *iSectorSource)
{
	return (!strcmp(iSectorName, strSectorName) && !strcmp(iSectorSource, strSectorSource));
}


/******************************************************
 * Copy sector title
 ******************************************************/
void ChimeSector::GetSectorTitle(char *strName, char *strSource)
{
	strcpy (strName, strSectorName);
	strcpy (strSource, strSectorSource);
}


/*******************************************************
 * Fill in region name of this sector
 *******************************************************/
void ChimeSector::GetRegionName (char *strRegionName)
{
	ChimeSector::GetRegionName (strSectorName, strSectorSource, strRegionName);
}


/********************************************************
 * Creates the name of the region for this sector
 * based on sector parameters
 ********************************************************/
void ChimeSector::GetRegionName (char* strSectorName, char* strSectorSource, char* strRegionName)
{
    strcpy (strRegionName, strSectorSource);
	strcat (strRegionName, strSectorName);
}


/******************************************************
 * Find the default door that leads to entrance sector
 ******************************************************/
ChimeSectorEntity* ChimeSector::FindEntity (char *strEntityName, int iEntityType)
{
	ChimeSectorEntity *entity = NULL;
	for (int i = 0; i < chActiveEntities->Length (); i++)
	{
		entity = (ChimeSectorEntity*)chActiveEntities->Get (i);
		if (entity->GetEntityType () == iEntityType && 
			!strcmp (strEntityName, entity->GetEntityName ()))
			return entity;
	}
	return NULL;
}


/******************************************************
 * Find all entities of the same type
 ******************************************************/
csVector* ChimeSector::FindAllEntities (int iEntityType)
{
	csVector *all = new csVector (8);
	ChimeSectorEntity *entity = NULL;
	for (int i = 0; i < chActiveEntities->Length (); i++)
	{
		entity = (ChimeSectorEntity*)chActiveEntities->Get (i);
		if (entity->GetEntityType () == iEntityType)
			all->Push (entity);
	}
	return all;
}


/******************************************************
 * Find the default door that leads to entrance sector
 ******************************************************/
ChimeSectorDoor* ChimeSector::FindEntranceDoor ()
{
	return (ChimeSectorDoor*) FindEntity ("default", ENTITY_TYPE_DOOR);
}


/******************************************************
 * Find the room in this sector under given name
 ******************************************************/
iSector* ChimeSector::FindRoom (char *strRoomName)
{
	// determine the full name of the region
	// that needs to be created for this sector
	char regionName[200];
	GetRegionName (regionName);

	// find the region
	iRegion *region = driver->csEngine->GetRegions ()->FindByName (regionName);

	// find the room
	return driver->csEngine->FindSector (strRoomName, region);
}


/******************************************************
 * See if given room belongs to this sector
 ******************************************************/
bool ChimeSector::IsRoomInThisSector (iSector* room)
{
	for (int i = 0; i < chRooms->Length (); i++)
	{
		if ((iSector*)chRooms->Get (i) == room)
			return true;
	}

	return false;
}


/***************************************************************
 * Find a polygon closest to given origin
 * using raycasting. Validate each found polygon
 * against passed function. Save point of intersection
 * to intersect pointer.
 ***************************************************************/
iPolygon3D* ChimeSector::FindClosestPolygon (iThingState *poly_state,
								csVector3 const &origin, csVector3 *intersect,
								bool (*func) (const csVector3*, iPolygon3D*))
{
	iPolygon3D* poly_hit = NULL;
	
	// vector that is added to the origin as the
	// end of the ray
	csVector3 origin_add (0, 0, 0);

	// isect is the point of intersection
	// of the ray with the polygon
	csVector3 isect (0, 0, 0);

	float x, z, length;
	// loop that defines the length of the ray
	for (length = 0.5; length <= 10; length += 0.5)
	{
		//------------- handle special cases -----------------------//
		//------------- (for comments on actions, see below) --------//

		// step back
		origin_add.Set (0, 0, -length);
		poly_hit = poly_state->IntersectSegment (origin, 
            origin + origin_add, isect);
		if (poly_hit)
		{
			if ((func && func (&origin, poly_hit)) || func == NULL)
			{
                intersect->Set (isect);
                return poly_hit;
			}
		}

		// step forward
		origin_add.Set (0, 0, length);
		poly_hit = poly_state->IntersectSegment (origin, 
            origin + origin_add, isect);
		if (poly_hit)
		{
			if ((func && func (&origin, poly_hit)) || func == NULL)
			{
                intersect->Set (isect);
                return poly_hit;
			}
		}

		// step left
		origin_add.Set (-length, 0, 0);
		poly_hit = poly_state->IntersectSegment (origin, 
            origin + origin_add, isect);
		if (poly_hit)
		{
			if ((func && func (&origin, poly_hit)) || func == NULL)
			{
                intersect->Set (isect);
                return poly_hit;
			}
		}

		// step right
		origin_add.Set (length, 0, 0);
		poly_hit = poly_state->IntersectSegment (origin, 
            origin + origin_add, isect);
		if (poly_hit)
		{
			if ((func && func (&origin, poly_hit)) || func == NULL)
			{
                intersect->Set (isect);
                return poly_hit;
			}
		}

		
		// range x from -length to length
		for (z = -length; z <= length; z += 0.5)
		{
			// range z from -length to length
			for (x = -length; x <= length; x += 0.5)
			{
				// get the add vector
				origin_add.Set (x, 0, z);

				// find intersecting polygon
				poly_hit = poly_state->IntersectSegment (origin, 
					origin + origin_add, isect);

				// if a wall is found that has four corners
				if (poly_hit)
				{
					// if the wall is not valid, continue
					if (func && !func (&origin, poly_hit))
                        continue;

					// otherwise, return found wall
					intersect->Set (isect);
					return poly_hit;
				}
			}
		}
	}

	return NULL;
}

/********************************************************
 * Add a new entity to this sector
 ********************************************************/
bool ChimeSector::AddEntity (char *strEntityName, int iEntityType,
							 csStrVector const &param_name, csVector const &param_value)
{
	// make sure there is the same number of parameters
	if (param_name.Length () != param_value.Length ())
	{
		driver->Report ("crystalspace.application.chime", 
			"Could not add entity! Improper number of parameters.");
		return false;
	}

	// add entity
	switch (iEntityType)
	{
	case ENTITY_TYPE_DOOR : 

		// make sure there are enough parameters
		if (param_name.Length () == 6)
		{
            // make sure all parameters are there
            if (param_name.FindKey ("origin") < 0 ||
                param_name.FindKey ("room") < 0 ||
                param_name.FindKey ("target_name") < 0 ||
                param_name.FindKey ("target_source") < 0 ||
                param_name.FindKey ("door_texture") < 0 ||
                param_name.FindKey ("texture_size") < 0
				)
                return false;

            // add the door entity
			return AddDoorEntity (
                (csVector3*) param_value.Get (param_name.FindKey ("origin")),
                (char*) param_value.Get (param_name.FindKey ("room")),
				strEntityName, 
                (char*) param_value.Get (param_name.FindKey ("target_name")),
                (char*) param_value.Get (param_name.FindKey ("target_source")),
                (char*) param_value.Get (param_name.FindKey ("door_texture")),
                (csVector3*) param_value.Get (param_name.FindKey ("texture_size"))
				);
		}
		break;

	case ENTITY_TYPE_OBJECT :
	case ENTITY_TYPE_ACTIVE_OBJECT : 
	case ENTITY_TYPE_USER :

		// make sure there are enough parameters
		if (param_name.Length () == 5)
		{
		// make sure all parameters are there
		if (param_name.FindKey ("origin") < 0 ||
			param_name.FindKey ("room") < 0 ||
			param_name.FindKey ("object_source") < 0 ||
			param_name.FindKey ("object_model") < 0 ||
			param_name.FindKey ("object_material") < 0
			)
            return false;

		// find room where the door is added
		iSector* room = FindRoom ((char*) param_value.Get (param_name.FindKey ("room")));
		if (!room) return false;

		// find object origin
		csVector3 *origin = (csVector3*)param_value.Get (param_name.FindKey ("origin"));
		if (!origin) return false;

		// add mesh for object
		iMeshWrapper *mesh = AddMeshObject (strEntityName, 
			(char*) param_value.Get (param_name.FindKey ("object_model")),
			room, *origin);
		if (!mesh) return false;

		// create new object
		chActiveEntities->Push (new ChimeSectorObject (strEntityName, 
			(char*) param_value.Get (param_name.FindKey ("object_source")),
			mesh, room, (char*) param_value.Get (param_name.FindKey ("object_model")),
			(char*) param_value.Get (param_name.FindKey ("object_material")), iEntityType)
			);
		}
		break;
	}
	return true;
}


/******************************************************
 * Return the location in the default room where
 * to place the user
 ******************************************************/
csVector3 ChimeSector::GetDefaultLocation()
{
	return csDefaultLocation;
}


/******************************************************
 * Return region where this sector resides
 ******************************************************/
iRegion* ChimeSector::GetRegion ()
{
    // find region name
	char region_name[150];
	GetRegionName (region_name);

	// find region
	return driver->csEngine->GetRegions ()->FindByName (region_name);
}


void ChimeSector::ProcessSectorDefinition (chSectorStructPtr sector, csVector3 const &origin, csVector3 const &rot)
{
	//create the rotation matrix
	csYRotMatrix3 rot_matrix (rot.y);

	//find entrance door to this sector
	chDoorStructPtr defaultDoor = NULL;
	for (int i = 0; i < sector->numRooms; i++)
	{
		for (int j = 0; j < sector->rooms[i]->numDoors; j++)
		{
			if (!strcmp (sector->rooms[i]->doors[j]->strRoomName, "entrance"))
				defaultDoor = sector->rooms[i]->doors[j];
		}
	}

	if (!defaultDoor)
		return;

	//adjust origin, based on the overlap of two doors
	csVector3 rot3 = rot_matrix * (*(defaultDoor->door.GetVertex (defaultDoor->door.GetVertexCount () - 1)));
	csVector3 start (origin - rot3);

	//------- rotate all vectors using given rotation matrix -------//
	sector->defaultLocation = rot_matrix * sector->defaultLocation;
	sector->defaultLocation += start;

	//for each room...
	for (int i = 0; i < sector->numRooms; i++)
	{
		for (int j = 0; j < sector->rooms[i]->numWalls; j++)
		{
			for (int k = 0; k < sector->rooms[i]->walls[j]->wall.GetVertexCount (); k++)
			{
				*sector->rooms[i]->walls[j]->wall.GetVertex (k) = rot_matrix * (*sector->rooms[i]->walls[j]->wall.GetVertex (k));
                *sector->rooms[i]->walls[j]->wall.GetVertex (k) += start;
			}
		}

		for (int j = 0; j < sector->rooms[i]->numDoors; j++)
		{
			for (int k = 0; k < sector->rooms[i]->doors[j]->door.GetVertexCount (); k++)
			{
				*sector->rooms[i]->doors[j]->door.GetVertex (k) = rot_matrix * (*sector->rooms[i]->doors[j]->door.GetVertex (k));
				*sector->rooms[i]->doors[j]->door.GetVertex (k) += start;
			}
		}

		for (int j = 0; j < sector->rooms[i]->numLights; j++)
		{
			sector->rooms[i]->lights[j]->location = rot_matrix * sector->rooms[i]->lights[j]->location;
			sector->rooms[i]->lights[j]->location += start;
		}

	}

}





/******************************************************
 * Parse the given XML file to get the room definition
 ******************************************************/
chSectorStructPtr ChimeSector::ParseSectorDefinition (char *strFileName)
{
	//-------- For now this is just a stub, later will be filled with an actual parser ------//
	
	// allocate space for a new sector
	chSectorStructPtr sector = (chSectorStructPtr) malloc (sizeof (chSectorStruct));
	sector->iSectorID = 0;
	sector->defaultLocation = csVector3 (0, 5, 1);

	// allocate space for three new rooms
	sector->rooms = (chRoomStructPtr*) malloc (2 * sizeof(chRoomStruct));
	sector->rooms[0] = (chRoomStructPtr) malloc (sizeof (chRoomStruct));
	strcpy (sector->rooms[0]->strRoomName, "main");
	sector->rooms[0]->numDoors = 2;
	sector->rooms[0]->numWalls = 14;
	sector->rooms[0]->numLights = 18;
	sector->rooms[1] = (chRoomStructPtr) malloc (sizeof (chRoomStruct));
	strcpy (sector->rooms[1]->strRoomName, "corridor");
	sector->rooms[1]->numDoors = 1;
	sector->rooms[1]->numWalls = 5;
	sector->rooms[1]->numLights = 12;
	sector->numRooms = 2;

	//---------------------------------------------------------------------------------//
	//----------------------- main room -----------------------------------------------//
	//---------------------------------------------------------------------------------//
	
	// allocate walls for the main room
	sector->rooms[0]->walls = (chWallStructPtr*) malloc (sector->rooms[0]->numWalls * sizeof (chWallStruct));
	for (int i = 0; i < sector->rooms[0]->numWalls; i++) {
		sector->rooms[0]->walls[i] = (chWallStructPtr) malloc (sizeof (chWallStruct));
		sector->rooms[0]->walls[i]->txtSize = csVector3 (1, 1, 1);
		strcpy(sector->rooms[0]->walls[i]->strWallTexture, "wall_text");
	}

	double floor = 0.0;
	double ceiling = 10.0;
	double length = 10;
	double width = 10;
	int door_width = 3;
	double door_delta = 0.0;
	int out_door_width = 1;

	// ceiling
	sector->rooms[0]->walls[0]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[0]->wall.AddVertex (-width, ceiling, length); sector->rooms[0]->walls[0]->wall.AddVertex (-width, ceiling, -length);
	sector->rooms[0]->walls[0]->wall.AddVertex (width, ceiling, -length); sector->rooms[0]->walls[0]->wall.AddVertex (width, ceiling, length);
	strcpy (sector->rooms[0]->walls[0]->strWallTexture, "ceil_text");

	// floor
	sector->rooms[0]->walls[1]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[1]->wall.AddVertex (-width, floor, -length); sector->rooms[0]->walls[1]->wall.AddVertex (-width, floor, length);
	sector->rooms[0]->walls[1]->wall.AddVertex (width, floor, length); sector->rooms[0]->walls[1]->wall.AddVertex (width, floor, -length);
	strcpy (sector->rooms[0]->walls[1]->strWallTexture, "floor_text");

	// left
	sector->rooms[0]->walls[2]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[2]->wall.AddVertex (-width, floor, -length); sector->rooms[0]->walls[2]->wall.AddVertex (-width, ceiling, -length);
	sector->rooms[0]->walls[2]->wall.AddVertex (-width, ceiling, length); sector->rooms[0]->walls[2]->wall.AddVertex (-width, floor, length);

	// front
	sector->rooms[0]->walls[3]->wall = *(new csPoly3D (8));
	sector->rooms[0]->walls[3]->wall.AddVertex (-width, floor, length); sector->rooms[0]->walls[3]->wall.AddVertex (-width, ceiling, length);
	sector->rooms[0]->walls[3]->wall.AddVertex (-door_width, ceiling, length); sector->rooms[0]->walls[3]->wall.AddVertex (-door_width, floor, length);

	// right
	sector->rooms[0]->walls[4]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[4]->wall.AddVertex (width, floor, length); sector->rooms[0]->walls[4]->wall.AddVertex (width, ceiling, length);
	sector->rooms[0]->walls[4]->wall.AddVertex (width, ceiling, -length); sector->rooms[0]->walls[4]->wall.AddVertex (width, floor, -length);

	// back
	sector->rooms[0]->walls[5]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[5]->wall.AddVertex (width, floor, -length); sector->rooms[0]->walls[5]->wall.AddVertex (width, ceiling, -length);
	sector->rooms[0]->walls[5]->wall.AddVertex (-width, ceiling, -length); sector->rooms[0]->walls[5]->wall.AddVertex (-width, floor, -length);

	// ramp
	sector->rooms[0]->walls[6]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[6]->wall.AddVertex (0, floor, -8); sector->rooms[0]->walls[6]->wall.AddVertex (-7, ceiling/2, -8);
	sector->rooms[0]->walls[6]->wall.AddVertex (-7, ceiling/2, -5); sector->rooms[0]->walls[6]->wall.AddVertex (0, floor, -5);

	// ramp left
	sector->rooms[0]->walls[7]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[7]->wall.AddVertex (-10, floor, -8); sector->rooms[0]->walls[7]->wall.AddVertex (-10, ceiling/2, -8);
	sector->rooms[0]->walls[7]->wall.AddVertex (-7, ceiling/2, -8); sector->rooms[0]->walls[7]->wall.AddVertex (0, floor, -8);

	// ramp right
	sector->rooms[0]->walls[8]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[8]->wall.AddVertex (0, floor, -5); sector->rooms[0]->walls[8]->wall.AddVertex (-7, ceiling/2, -5);
	sector->rooms[0]->walls[8]->wall.AddVertex (-10, ceiling/2, -5); sector->rooms[0]->walls[8]->wall.AddVertex (-10, floor, -5);

	// shelf top
	sector->rooms[0]->walls[9]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[9]->wall.AddVertex (-10, ceiling/2, -8); sector->rooms[0]->walls[9]->wall.AddVertex (-10, ceiling/2, 6);
	sector->rooms[0]->walls[9]->wall.AddVertex (-7, ceiling/2, 6); sector->rooms[0]->walls[9]->wall.AddVertex (-7, ceiling/2, -8);

	// shelf front
	sector->rooms[0]->walls[10]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[10]->wall.AddVertex (-7, floor, 6); sector->rooms[0]->walls[10]->wall.AddVertex (-7, ceiling/2, 6);
	sector->rooms[0]->walls[10]->wall.AddVertex (-10, ceiling/2, 6); sector->rooms[0]->walls[10]->wall.AddVertex (-10, floor, 6);

	// shelf right
	sector->rooms[0]->walls[11]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[11]->wall.AddVertex (-7, floor, -5); sector->rooms[0]->walls[11]->wall.AddVertex (-7, ceiling/2, -5);
	sector->rooms[0]->walls[11]->wall.AddVertex (-7, ceiling/2, 6); sector->rooms[0]->walls[11]->wall.AddVertex (-7, floor, 6);

	// front upper
	sector->rooms[0]->walls[12]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[12]->wall.AddVertex (-door_width, floor+4, length); sector->rooms[0]->walls[12]->wall.AddVertex (-door_width, ceiling, length);
	sector->rooms[0]->walls[12]->wall.AddVertex (door_width, ceiling, length); sector->rooms[0]->walls[12]->wall.AddVertex (door_width, floor+4, length);

	// front right
	sector->rooms[0]->walls[13]->wall = *(new csPoly3D (4));
	sector->rooms[0]->walls[13]->wall.AddVertex (door_width, floor, length); sector->rooms[0]->walls[13]->wall.AddVertex (door_width, ceiling, length);
	sector->rooms[0]->walls[13]->wall.AddVertex (width, ceiling, length); sector->rooms[0]->walls[13]->wall.AddVertex (width, floor, length);

	// allocate 18 lights for the main room
	sector->rooms[0]->lights = (chLightStructPtr*) malloc (18 * sizeof (chLightStruct));
	for (int i = 0; i < 18; i++) {
		sector->rooms[0]->lights[i] = (chLightStructPtr) malloc (sizeof (chLightStruct));
		sector->rooms[0]->lights[i]->color = csColor (1, 1, 1);
		sector->rooms[0]->lights[i]->intensity = 9.0;
	}

	// place the lights
	double delta = 1.0;
	for (int i = 0; i < 2; i++) {
		sector->rooms[0]->lights[0+i*9]->location = csVector3 (-width+delta, i*ceiling/2 + 2, -length+delta);
		sector->rooms[0]->lights[1+i*9]->location = csVector3 (-width+delta, i*ceiling/2 + 2, 0);
		sector->rooms[0]->lights[2+i*9]->location = csVector3 (-width+delta, i*ceiling/2 + 2, length-delta);
		sector->rooms[0]->lights[3+i*9]->location = csVector3 (0, i*ceiling/2 + 2, -length+delta);
		sector->rooms[0]->lights[4+i*9]->location = csVector3 (0, i*ceiling/2 + 2, 0);
		sector->rooms[0]->lights[5+i*9]->location = csVector3 (0, i*ceiling/2 + 2, length-delta);
		sector->rooms[0]->lights[6+i*9]->location = csVector3 (width-delta, i*ceiling/2 + 2, -length+delta);
		sector->rooms[0]->lights[7+i*9]->location = csVector3 (width-delta, i*ceiling/2 + 2, 0);
		sector->rooms[0]->lights[8+i*9]->location = csVector3 (width-delta, i*ceiling/2 + 2, length-delta);
	}

	// allocate 2 doors in main room
	// one that leads to corridor
	// another that is the entrance door
	sector->rooms[0]->doors = (chDoorStructPtr*) malloc (2 * sizeof (chDoorStruct));
	sector->rooms[0]->doors[0] = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	sector->rooms[0]->doors[1] = (chDoorStructPtr) malloc (sizeof (chDoorStruct));

	// door to corridor
	sector->rooms[0]->doors[0]->strDoorTexture = NULL;
	strcpy (sector->rooms[0]->doors[0]->strRoomName, "corridor");
	sector->rooms[0]->doors[0]->alpha = 0;
	sector->rooms[0]->doors[0]->txtSize = NULL;
	sector->rooms[0]->doors[0]->door = *(new csPoly3D (4));
	sector->rooms[0]->doors[0]->door.AddVertex (-door_width, floor, length-2*door_delta); sector->rooms[0]->doors[0]->door.AddVertex (-door_width, floor+4, length-2*door_delta);
	sector->rooms[0]->doors[0]->door.AddVertex (door_width, floor+4, length-2*door_delta); sector->rooms[0]->doors[0]->door.AddVertex (door_width, floor, length-2*door_delta);

	// entrance door
	sector->rooms[0]->doors[1]->strDoorTexture = NULL;
	strcpy (sector->rooms[0]->doors[1]->strRoomName, "entrance");
	sector->rooms[0]->doors[1]->strDoorTexture = (char*) malloc (20 * sizeof (char));
	strcpy (sector->rooms[0]->doors[1]->strDoorTexture, "door_text");
	sector->rooms[0]->doors[1]->alpha = 0;
	sector->rooms[0]->doors[1]->txtSize = new csVector3 (2, 3, 1);
	sector->rooms[0]->doors[1]->door = *(new csPoly3D (4));
	sector->rooms[0]->doors[1]->door.AddVertex (out_door_width, floor, -length+2*door_delta); sector->rooms[0]->doors[1]->door.AddVertex (out_door_width, floor+3, -length+2*door_delta);
	sector->rooms[0]->doors[1]->door.AddVertex (-out_door_width, floor+3, -length+2*door_delta); sector->rooms[0]->doors[1]->door.AddVertex (-out_door_width, floor, -length+2*door_delta);

	//---------------------------------------------------------------------------------//
	//----------------------- corridor room -------------------------------------------//
	//---------------------------------------------------------------------------------//

	// create corridor room
	// allocate 6 walls for the connector
	sector->rooms[1]->walls = (chWallStructPtr*) malloc (6 * sizeof (chWallStruct));
	for (int i = 0; i < 6; i++) {
		sector->rooms[1]->walls[i] = (chWallStructPtr) malloc (sizeof (chWallStruct));
		sector->rooms[1]->walls[i]->txtSize = csVector3 (1, 1, 1);
		strcpy(sector->rooms[1]->walls[i]->strWallTexture, "wall_text");
	}

	// ceiling
	sector->rooms[1]->walls[0]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[0]->wall.AddVertex (-door_width, floor+4, 2*length); sector->rooms[1]->walls[0]->wall.AddVertex (-door_width, floor+4, length);
	sector->rooms[1]->walls[0]->wall.AddVertex (door_width, floor+4, length); sector->rooms[1]->walls[0]->wall.AddVertex (door_width, floor+4, 2*length);
	strcpy (sector->rooms[1]->walls[0]->strWallTexture, "ceil_text");

	// floor
	sector->rooms[1]->walls[1]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[1]->wall.AddVertex (-door_width, floor, length); sector->rooms[1]->walls[1]->wall.AddVertex (-door_width, floor, 2*length);
	sector->rooms[1]->walls[1]->wall.AddVertex (door_width, floor, 2*length); sector->rooms[1]->walls[1]->wall.AddVertex (door_width, floor, length);
	strcpy (sector->rooms[0]->walls[1]->strWallTexture, "floor_text");

	// left
	sector->rooms[1]->walls[2]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, floor, length); sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, floor+4, length);
	sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, floor+4, 2*length); sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, floor, 2*length);

	// front
	sector->rooms[1]->walls[3]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[3]->wall.AddVertex (-door_width, floor, 2*length); sector->rooms[1]->walls[3]->wall.AddVertex (-door_width, floor+4, 2*length);
	sector->rooms[1]->walls[3]->wall.AddVertex (door_width, floor+4, 2*length); sector->rooms[1]->walls[3]->wall.AddVertex (door_width, floor, 2*length);

	// right
	sector->rooms[1]->walls[4]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[4]->wall.AddVertex (door_width, floor, 2*length); sector->rooms[1]->walls[4]->wall.AddVertex (door_width, floor+4, 2*length);
	sector->rooms[1]->walls[4]->wall.AddVertex (door_width, floor+4, length); sector->rooms[1]->walls[4]->wall.AddVertex (door_width, floor, length);

	// back
	sector->rooms[1]->walls[5]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[5]->wall.AddVertex (door_width, floor, length); sector->rooms[1]->walls[5]->wall.AddVertex (door_width, floor+4, length);
	sector->rooms[1]->walls[5]->wall.AddVertex (-door_width, floor+4, length); sector->rooms[1]->walls[5]->wall.AddVertex (-door_width, floor, length);

	// allocate 8 lights for the connector room
	sector->rooms[1]->lights = (chLightStructPtr*) malloc (12 * sizeof (chLightStruct));
	for (int i = 0; i < 12; i++) {
		sector->rooms[1]->lights[i] = (chLightStructPtr) malloc (sizeof (chLightStruct));
		sector->rooms[1]->lights[i]->color = csColor (1, 1, 1);
		sector->rooms[1]->lights[i]->intensity = 4.0;
	}

	// place lights
	for (int i = 0; i < 2; i++) {
		sector->rooms[1]->lights[0+i*4]->location = csVector3 (-door_width+delta, i*2 + 3, length+delta);
		sector->rooms[1]->lights[1+i*4]->location = csVector3 (-door_width+delta, i*2 + 3, 2*length-delta);
		sector->rooms[1]->lights[2+i*4]->location = csVector3 (door_width-delta, i*2 + 3, length+delta);
		sector->rooms[1]->lights[3+i*4]->location = csVector3 (door_width-delta, i*2 + 3, 2*length-delta);
		sector->rooms[1]->lights[2+i*4]->location = csVector3 (-door_width+delta, i*2 + 3, length+length/2);
		sector->rooms[1]->lights[3+i*4]->location = csVector3 (door_width-delta, i*2 + 3, length+length/2);
	}

	// allocate 1 door in corridor room
	// that leads back to main room
	sector->rooms[1]->doors = (chDoorStructPtr*) malloc (sizeof (chDoorStruct));
	sector->rooms[1]->doors[0] = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	sector->rooms[1]->doors[0]->strDoorTexture = NULL;
	strcpy (sector->rooms[1]->doors[0]->strRoomName, "main");
	sector->rooms[1]->doors[0]->alpha = 0;
	sector->rooms[1]->doors[0]->txtSize = NULL;
	sector->rooms[1]->doors[0]->door = *(new csPoly3D (4));
	sector->rooms[1]->doors[0]->door.AddVertex (door_width, floor, length-2*door_delta); sector->rooms[1]->doors[0]->door.AddVertex (door_width, floor+10, length-2*door_delta);
	sector->rooms[1]->doors[0]->door.AddVertex (-door_width, floor+10, length-2*door_delta); sector->rooms[1]->doors[0]->door.AddVertex (-door_width, floor, length-2*door_delta);

	return sector;
}


void ChimeSector::RecreatePolygonsFromMesh (iMeshWrapper* mesh)
{
	csRef<iThingState> state = SCF_QUERY_INTERFACE (mesh->GetMeshObject (), iThingState);
	if (!state) return;
	iSector* room = mesh->GetMovable ()->GetSectors ()->Get (0);
	csRef<iMeshWrapper> new_mesh (driver->csEngine->CreateSectorWallsMesh (room, "temp_mesh"));
	csRef<iThingState> new_state = SCF_QUERY_INTERFACE (new_mesh->GetMeshObject (), iThingState);
	if (!new_state) return;
	csRef<iMaterialWrapper> material = NULL;
	csVector3 txtSize (1, 1, 1);

	int i = 0, j = 0;
	csPoly3D poly (10);
	iPolygon3D* p = NULL, *new_p = NULL;
	for (i = 0; i < state->GetPolygonCount (); i++)
	{
		p = state->GetPolygon (i);
		poly.MakeEmpty ();
		for (j = 0; j < p->GetVertexCount (); j++)
			poly.AddVertex (p->GetVertex (j));
		material = p->GetMaterial ();
		new_p = BuildWall (new_state, poly, material, &txtSize);
		if (p->GetPortal ()) new_p->CreatePortal (p->GetPortal ()->GetSector ());
		p->CopyTextureType (new_p);
	}

	char mName[50]; strcpy (mName, mesh->QueryObject ()->GetName ());
	driver->csEngine->RemoveObject (mesh);
	new_mesh->QueryObject ()->SetName (mName);
	new_mesh->SetZBufMode (CS_ZBUF_USE);
	driver->GetCollider ()->CreateMeshCollider (new_mesh);
}