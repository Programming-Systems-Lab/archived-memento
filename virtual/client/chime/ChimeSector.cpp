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
#include "csengine/region.h"

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
	csRef<iStatLight> csLight = driver->csEngine->CreateLight (NULL, location, scale,
  								  color, false);
	room->GetLights ()->Add(csLight->QueryLight());

	return true;
}


/************************************************************************
 * Adds a mesh object from the supplied object factory to the given
 * room at a given location
 ************************************************************************/
iMeshWrapper* ChimeSector::AddMeshObject (char *strObjectName, char *strFactoryName,
							 iSector* room, csVector3 const &location)
{
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
	mesh->GetMovable()->SetPosition (room, location);

	// Create a collider for the mesh
	driver->GetCollider ()->CreateObjectCollider (mesh);

	return mesh;
}

/******************************************************************************
 * Build a wall for given array of vertices, given in the csPoly3D object
 * Urepeat and vrepeat refer to u and v scale (repetition) of the texture
 ******************************************************************************/
iPolygon3D* ChimeSector::AddWall (iThingState *walls, csPoly3D const &vertices,
						 iMaterialWrapper *texture, csVector3 const &txtSize)
{
	// create a polygon
	iPolygon3D* p = walls->CreatePolygon ();
	
	// set the texture
	if (texture)
        p->SetMaterial (texture);

	// create polygon vertices
	for (int i=0; i<vertices.GetVertexCount(); i++)
		p->CreateVertex(*(vertices.GetVertex(i)));

	// map the texture
	if (p->GetVertexCount () > 3)
		p->SetTextureSpace (p->GetVertex (1), p->GetVertex (2), txtSize.x, p->GetVertex (0), txtSize.y);

	return p;
}

/******************************************************************************
 * Build a door for given array of vertices, connecting to given room
 * and set to transparency alpha (0 for transparent, 255 for non-transparent)
 ******************************************************************************/
iPolygon3D* ChimeSector::AddInnerDoor (iThingState *walls, csPoly3D const &vertices,
						 iMaterialWrapper *texture, csVector3 const &txtSize,
						 iSector *room, int alpha)
{
	// build the polygon for the door (just use AddWall, its the same thing)
	iPolygon3D *p = AddWall (walls, vertices, texture, txtSize);

	// set this polygon as a portal to the given room
	p->CreatePortal (room);

	// set transparency for this polygon
	p->SetAlpha (alpha);

	return p;
}


/******************************************************************************
 * Build a door for given array of vertices
 ******************************************************************************/
iPolygon3D* ChimeSector::AddOuterDoor (iThingState *walls, csPoly3D const &vertices,
						 iMaterialWrapper *texture, csVector3 const &txtSize,
						 char *strDoorName, char *strDoorTargetName, 
						 char *strDoorTargetSource, char *strDoorTexture)
{
	// build the polygon for the door (just use AddWall, its the same thing)
	iPolygon3D *p = AddWall (walls, vertices, texture, txtSize);

	// build the polygon labels for the door
	iPolygon3D** label = NULL;
	int num_letters = 0;
	label = AddDoorLabel (walls, p, num_letters);

	// add new door entity to the list
	chActiveEntities->Push ( new ChimeSectorDoor (strDoorName, 
		p, label, num_letters, strDoorTargetName, strDoorTargetSource, strDoorTexture));

	return p;
}


/***********************************************************
 * SetupSector is the real constructor.
 * It initializes the whole sector based on a description
 * in the XML file. SetupSector builds the rooms, objects,
 * and doors from that description. It also sets up
 * the dynamic aspects of the sector.
 ***********************************************************/
ChimeSector* ChimeSector::SetupSector(csVector3 &origin, csVector3 const &rotation,
									  char* strSectorXMLFile, char *strISectorName, 
									  char *strISectorSource, ChimeSector *iPreviousSector)
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

	// set sector attributes of the sector structure
	strcpy (sectorStruct->strSectorName, strISectorName);
	strcpy (sectorStruct->strSectorSource, strISectorSource);

	// process structure coordinates to update them, reflecting
	// given origin and rotation angle
    ProcessSectorDefinition (sectorStruct, origin, rotation);

	// determine the full name of the region
	// that needs to be created for this sector
	char regionName[150];
	ChimeSector::GetRegionName (strISectorName, strISectorSource, regionName);
	
	// build the actual sector
	sector->BuildSector (sectorStruct, regionName);

	// set current room and default location
	sector->chCurrentRoom = sector->GetDefaultRoom ();
	sector->csDefaultLocation = sectorStruct->defaultLocation;

	// connect the entrance door to previous sector if it is not null
	if (iPreviousSector)
	{
        // 1. find the entrance (default) door
		ChimeSectorDoor *door = sector->FindEntranceDoor ();
		iSector* entrance = driver->GetCurrentSector ()->GetCurrentRoom ();

		// 2. if door is found, connect it to current room
		// in the previous sector
		if (door && entrance)
		{
			sector->chEntranceRoom = entrance;
			char sName[50], sSource[100];
			iPreviousSector->GetSectorTitle (sName, sSource);
			door->SetDoorTarget (sName, sSource);
			door->ConnectDoorToTarget (true, entrance);
			door->OpenDoor ();
		}
	}

	//Update engine
	driver->csEngine->Prepare ();

	if (!sector->GetDefaultRoom ())
		return NULL;

	return sector;
}

/***********************************************************
 * BuildSector builds the actual sector from the structure
 * provided by the XML parser in variable sector
 ***********************************************************/
bool ChimeSector::BuildSector (chSectorStructPtr sectorStruct, char *strRegionName)
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

	// build doors (we can build doors only now,
	// after all the rooms were created so that we can
	// point these doors to existing rooms)
	chDoorStructPtr door;
	csRef<iMaterialWrapper> doorMaterial;
	iSector *roomConnection, *thisRoom;
	csRef<iMeshWrapper> wall_mesh;
	csRef<iThingState> walls_state;
	for (j = 0; j < sectorStruct->numRooms; j++) {

		// find the pointer to this room where the door is
		thisRoom = driver->csEngine->FindSector (sectorStruct->rooms[j]->strRoomName, region);
		if (!thisRoom)
			continue;

		// get the wall mesh for this room
		wall_mesh = driver->csEngine->CreateSectorWallsMesh (thisRoom, "doors");
		if (!wall_mesh)
			continue;

		// get the wall state (necessary for building)
		walls_state = SCF_QUERY_INTERFACE (wall_mesh->GetMeshObject (), iThingState);
		if (!walls_state)
			continue;

		// build doors
		for (i = 0; i < sectorStruct->rooms[j]->numDoors; i++) {

			// get the door structure definition
			door = sectorStruct->rooms[j]->doors[i];

			// get the material
			doorMaterial = driver->csEngine->GetMaterialList ()->FindByName (door->strDoorTexture);
			if (!doorMaterial)
                continue;

			// find the sector this door connects to by name
			roomConnection = driver->csEngine->FindSector (door->strRoomName, region);
			if (!roomConnection)
				continue;

			// build the door
			if (!AddInnerDoor (walls_state, door->door, doorMaterial, door->txtSize, roomConnection, door->alpha))
				rv = false;
		}

		// shine lights
		thisRoom->ShineLights ();
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

	// declare some variables
	int i = 0;
	csRef<iMaterialWrapper> material;
	chWallStructPtr wall;

	// build walls
	for (i = 0; i < roomStruct->numWalls; i++) {

		// get the wall structure definition
		wall = roomStruct->walls[i];

		// get the material
		material = driver->csEngine->GetMaterialList ()->FindByName (wall->strWallTexture);
		if (!material)
			continue;

		// build the wall
		AddWall (walls_state, wall->wall, material, wall->txtSize);

	}

	// also, finally add collision detection to each room's walls
	driver->GetCollider()->CreateObjectCollider (walls);

	// add lights
	chLightStructPtr light;
	for (i = 0; i < roomStruct->numLights; i++) {

		// get the light
		light = roomStruct->lights[i];

		// add the light
		AddLight (light->location, light->color, light->scale, room);
	}

	// add objects
	chObjectStructPtr object;
	for (i = 0; i < roomStruct->numObjects; i++) {

		// get the object
		object = roomStruct->objects[i];

		// add the light
		iMeshWrapper *objectMesh = AddMeshObject (object->strObjectName, object->strObjectModel, room, object->location);

		// add this object to the list of active objects
		if (objectMesh)
		{
			switch (object->intObjectType)
			{
			case ENTITY_TYPE_OBJECT : chActiveEntities->Push (new ChimeSectorObject (object->strObjectName, 
										  object->strObjectName, objectMesh, &(object->location), 
										  room, object->strObjectModel, NULL));
										break;
			case ENTITY_TYPE_ACTIVE_OBJECT : chActiveEntities->Push (new ChimeSectorActiveObject (object->strObjectName, 
												 object->strObjectName, objectMesh, &(object->location), 
												 room, object->strObjectModel, NULL));
											break;
			case ENTITY_TYPE_USER : chActiveEntities->Push (new ChimeSectorUser (object->strObjectName, 
										object->strObjectName, objectMesh, &(object->location), 
										room, object->strObjectModel, NULL));
										break;
			}
		}
	}

	// add outer doors
	chOuterDoorStructPtr outer_door;
	for (i = 0; i < roomStruct->numOuterDoors; i++) {

		// get the door structure
		outer_door = roomStruct->outer_doors[i];

		// get the material
		material = driver->csEngine->GetMaterialList ()->FindByName (outer_door->door->strDoorTexture);
		if (!material)
			continue;

		// build the polygon for outer door
		AddOuterDoor (walls_state, outer_door->door->door, material, 
			outer_door->door->txtSize, outer_door->strDoorName, outer_door->strTargetSectorName,
			outer_door->strTargetSectorSource, outer_door->door->strDoorTexture);
	}

	return room;
}


/**********************************************************************
 * Return the first entity that intersects the path from start to end
 **********************************************************************/
ChimeSectorEntity* ChimeSector::SelectEntity (const csVector3 &start, const csVector3 &end, csVector3 &isect)
{
	// Prepare variables used to hold objects hit by the beam.
	// HitBeam only returns the mesh that was hit by the beam.
	iMeshWrapper *mesh = NULL;

	// While that is fine for object entities that are represented by
	// meshes, all door entities are a part of the same 'doors' mesh,
	// and so they must be distinguished by their polygons.
	// HitBeam fills hit polygons passed to it as the third parameter
	iPolygon3D **polygons = (iPolygon3D**) malloc ( sizeof(iPolygon3D));
	polygons[0] = (iPolygon3D*) malloc ( sizeof(iPolygon3D));
	polygons[0] = NULL;

	// Intersect the beam to find the mesh
	mesh = chCurrentRoom->HitBeam (start, end, isect, polygons);
	
	// If no mesh was found, return nothing
	if (!mesh)
		return NULL;

	// Prepare pointers for possible active entities
	ChimeSectorEntity *entity = NULL;

	for (int i = 0; i < chActiveEntities->Length (); i++)
	{
		entity = (ChimeSectorEntity*) chActiveEntities->Get (i);
		if (entity->IsEntitySelected (mesh, polygons[0]))
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
iPolygon3D** ChimeSector::AddDoorLabel (iThingState *walls, iPolygon3D *door_polygon, int &num_letters)
{
	// the width and height of a single polygon used for a single letter
	// are pre-calculated and are set here, but can be changed if texture
	// size changes
	float LETTER_WIDTH = 0.15;
	float LETTER_HEIGHT = 0.15;

	iPolygon3D** label;
	num_letters = 0;
	
	// make sure no input is null
	if (door_polygon && walls)
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
			}
			if (wall_normal.z > 0)
			{
				int temp = xmin;
				xmin = xmax;
				xmax = temp;
				xincr = -xincr;
			}

		}

		// create a holder for one polygon
		csPoly3D letter (4);

		// get default empty (black) texture to assign to the polygons
		csRef<iMaterialWrapper> material = driver->csEngine->GetMaterialList ()->FindByName ("letter_a");

		// create each polygon
		for (int i = 0; i < numLabelPolys; i++)
		{
			letter.AddVertex (xmin + xincr*i, ymax, zmin + zincr*i);
			letter.AddVertex (xmin + xincr*i, ymax + LETTER_HEIGHT, zmin + zincr*i);
			letter.AddVertex (xmin + xincr*(i+1), ymax + LETTER_HEIGHT, zmin + zincr*(i+1));
			letter.AddVertex (xmin + xincr*(i+1), ymax, zmin + zincr*(i+1));
			label[i] = AddWall (walls, letter, material, csVector3 (0.3, 0.3, 1));
			letter.MakeEmpty ();
		}

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


/******************************************************
 * Update an object inside this sector. The object is
 * found based on the first two parameters. The other
 * parameters that are not NULL are used to update
 * different properties of the found object.
 ******************************************************/
void ChimeSector::UpdateObject (char *strObjectName, int iObjectType, 
								char *strObjectSource, char *strObjectModel, 
								char *strObjectMaterial, csVector3* vecObjectLocation, 
								char *strObjectRoom)
{

	ChimeSectorObject *object = (ChimeSectorObject*) FindEntity (strObjectName, iObjectType);

	// If object is not found, skip
	if (!object)
		return;

	// Update object source if not NULL
	if (strObjectSource)
		object->SetObjectSource (strObjectSource);

	// Update object model if not NULL
	if (strObjectModel)
		object->SetObjectModel (strObjectModel);

	// Update object material if not NULL
	if (strObjectMaterial)
		object->SetObjectMaterial (strObjectMaterial);

	// Update object location if not NULL
	if (vecObjectLocation)
		object->SetObjectLocation (*vecObjectLocation);

	// Update object room if not NULL
	// (but first find that room based on its name)
	if (strObjectRoom)
	{
		iSector* room = FindRoom (strObjectRoom);
		if (!room)
			return;
		object->SetObjectRoom (room);
	}
}


/******************************************************
 * Return the location in the default room where
 * to place the user
 ******************************************************/
csVector3 ChimeSector::GetDefaultLocation()
{
	return csDefaultLocation;
}


void ChimeSector::ProcessSectorDefinition (chSectorStructPtr sector, csVector3 const &origin, csVector3 const &rot)
{
	//create the rotation matrix
	csYRotMatrix3 rot_matrix (rot.y);

	//find default outer door for this sector
	chOuterDoorStructPtr defaultDoor = NULL;
	for (int i = 0; i < sector->numRooms; i++)
	{
		for (int j = 0; j < sector->rooms[i]->numOuterDoors; j++)
		{
			if (!strcmp (sector->rooms[i]->outer_doors[j]->strDoorName, "default"))
				defaultDoor = sector->rooms[i]->outer_doors[j];
		}
	}

	//adjust origin, based on the overlap of two doors
	csVector3 rot3 = rot_matrix * (*(defaultDoor->door->door.GetVertex (defaultDoor->door->door.GetVertexCount () - 1)));
	csVector3 start (origin - rot3);
	if (start.x > 0) start.x += 0.1; else start.x += -0.1;
	if (start.y > 0) start.y += 0.1; else start.y += -0.1;
	if (start.z > 0) start.z += 0.1; else start.z += -0.1;

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
				printf("Room %d. Wall %d. Vertex %d: %f, %f, %f\n", i, j, k, sector->rooms[i]->walls[j]->wall.GetVertex (k)->x, sector->rooms[i]->walls[j]->wall.GetVertex (k)->y, sector->rooms[i]->walls[j]->wall.GetVertex (k)->z);
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

		for (int j = 0; j < sector->rooms[i]->numOuterDoors; j++)
		{
			for (int k = 0; k < sector->rooms[i]->outer_doors[j]->door->door.GetVertexCount (); k++)
			{
				*sector->rooms[i]->outer_doors[j]->door->door.GetVertex (k) = rot_matrix * (*sector->rooms[i]->outer_doors[j]->door->door.GetVertex (k));
				*sector->rooms[i]->outer_doors[j]->door->door.GetVertex (k) += start;
			}
		}

		for (int j = 0; j < sector->rooms[i]->numLights; j++)
		{
			sector->rooms[i]->lights[j]->location = rot_matrix * sector->rooms[i]->lights[j]->location;
			sector->rooms[i]->lights[j]->location += start;
		}

		for (int j = 0; j < sector->rooms[i]->numObjects; j++)
		{
			sector->rooms[i]->objects[j]->location = rot_matrix * sector->rooms[i]->objects[j]->location;
			sector->rooms[i]->objects[j]->location += start;
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
	sector->defaultLocation = csVector3 (0, 5, 1);
	strcpy (sector->strSectorName, "default");

	// allocate space for three new rooms
	sector->rooms = (chRoomStructPtr*) malloc (3 * sizeof(chRoomStruct));
	sector->rooms[0] = (chRoomStructPtr) malloc (sizeof (chRoomStruct));
	strcpy (sector->rooms[0]->strRoomName, "main");
	sector->rooms[0]->numDoors = 1;
	sector->rooms[0]->numWalls = 14;
	sector->rooms[0]->numLights = 18;
	sector->rooms[0]->numObjects = 1;
	sector->rooms[0]->numOuterDoors = 2;
	sector->rooms[1] = (chRoomStructPtr) malloc (sizeof (chRoomStruct));
	strcpy (sector->rooms[1]->strRoomName, "connector");
	sector->rooms[1]->numDoors = 1;
	sector->rooms[1]->numWalls = 5;
	sector->rooms[1]->numLights = 12;
	sector->rooms[2] = (chRoomStructPtr) malloc (sizeof (chRoomStruct));
	strcpy (sector->rooms[2]->strRoomName, "hallway");
	sector->rooms[2]->numDoors = 1;
	sector->rooms[2]->numWalls = 6;
	sector->rooms[2]->numLights = 20;
	sector->numRooms = 2;

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

	// allocate 24 lights for the main room
	sector->rooms[0]->lights = (chLightStructPtr*) malloc (18 * sizeof (chLightStruct));
	for (int i = 0; i < 18; i++) {
		sector->rooms[0]->lights[i] = (chLightStructPtr) malloc (sizeof (chLightStruct));
		sector->rooms[0]->lights[i]->color = csColor (1, 1, 1);
		sector->rooms[0]->lights[i]->scale = 9.0;
	}

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

	// allocate 1 door for main room
	sector->rooms[0]->doors = (chDoorStructPtr*) malloc (sizeof (chDoorStruct));
	sector->rooms[0]->doors[0] = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	strcpy (sector->rooms[0]->doors[0]->strDoorTexture, "floor_text");
	strcpy (sector->rooms[0]->doors[0]->strRoomName, "connector");
	sector->rooms[0]->doors[0]->alpha = 0;
	sector->rooms[0]->doors[0]->txtSize = csVector3 (1, 1, 1);
	sector->rooms[0]->doors[0]->door = *(new csPoly3D (4));
	sector->rooms[0]->doors[0]->door.AddVertex (-door_width, floor, length-2*door_delta); sector->rooms[0]->doors[0]->door.AddVertex (-door_width, floor+4, length-2*door_delta);
	sector->rooms[0]->doors[0]->door.AddVertex (door_width, floor+4, length-2*door_delta); sector->rooms[0]->doors[0]->door.AddVertex (door_width, floor, length-2*door_delta);

	// allocate one outer door in this room
	sector->rooms[0]->outer_doors = (chOuterDoorStructPtr*) malloc (sector->rooms[0]->numOuterDoors * sizeof (chOuterDoorStruct));
	sector->rooms[0]->outer_doors[0] = (chOuterDoorStructPtr) malloc (sizeof (chOuterDoorStruct));
	strcpy (sector->rooms[0]->outer_doors[0]->strTargetSectorName, "sector2");
	strcpy (sector->rooms[0]->outer_doors[0]->strTargetSectorSource, "www.default.com/s2");
	sector->rooms[0]->outer_doors[0]->door = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	sector->rooms[0]->outer_doors[0]->door->alpha = 0;
	strcpy (sector->rooms[0]->outer_doors[0]->door->strDoorTexture, "door_text");
	strcpy (sector->rooms[0]->outer_doors[0]->door->strRoomName, "main");
	sector->rooms[0]->outer_doors[0]->door->txtSize = csVector3 (2, 3, 1);
	sector->rooms[0]->outer_doors[0]->door->door = *(new csPoly3D (4));
	sector->rooms[0]->outer_doors[0]->door->door.AddVertex (width-0.01, floor, length/2 + 1); sector->rooms[0]->outer_doors[0]->door->door.AddVertex (width-0.01, floor+3, length/2 + 1);
	sector->rooms[0]->outer_doors[0]->door->door.AddVertex (width-0.01, floor+3, length/2 - 1); sector->rooms[0]->outer_doors[0]->door->door.AddVertex (width-0.01, floor, length/2 - 1);
	strcpy (sector->rooms[0]->outer_doors[0]->strDoorName, "door1");

	sector->rooms[0]->outer_doors[1] = (chOuterDoorStructPtr) malloc (sizeof (chOuterDoorStruct));
	strcpy (sector->rooms[0]->outer_doors[1]->strTargetSectorName, "sector5");
	strcpy (sector->rooms[0]->outer_doors[1]->strTargetSectorSource, "www.default.com/s5");
	sector->rooms[0]->outer_doors[1]->door = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	sector->rooms[0]->outer_doors[1]->door->alpha = 0;
	strcpy (sector->rooms[0]->outer_doors[1]->door->strDoorTexture, "door_text");
	strcpy (sector->rooms[0]->outer_doors[1]->door->strRoomName, "main");
	sector->rooms[0]->outer_doors[1]->door->txtSize = csVector3 (2, 3, 1);
	sector->rooms[0]->outer_doors[1]->door->door = *(new csPoly3D (4));
	sector->rooms[0]->outer_doors[1]->door->door.AddVertex (door_width-2, floor, -length+0.01); sector->rooms[0]->outer_doors[1]->door->door.AddVertex (door_width-2, floor+3, -length+0.01);
	sector->rooms[0]->outer_doors[1]->door->door.AddVertex (-door_width+2, floor+3, -length+0.01); sector->rooms[0]->outer_doors[1]->door->door.AddVertex (-door_width+2, floor, -length+0.01);
	strcpy(sector->rooms[0]->outer_doors[1]->door->strRoomName, "default");
	strcpy (sector->rooms[0]->outer_doors[1]->strDoorName, "default");


	// allocate one object for main room
	sector->rooms[0]->numObjects = 1;
	sector->rooms[0]->objects = (chObjectStructPtr*) malloc (sizeof (chObjectStruct));
	sector->rooms[0]->objects[0] = (chObjectStructPtr) malloc (sizeof (chObjectStruct));
	strcpy (sector->rooms[0]->objects[0]->strObjectModel, "user");
	strcpy (sector->rooms[0]->objects[0]->strObjectName, "tool");
	sector->rooms[0]->objects[0]->intObjectType = ENTITY_TYPE_USER;
	sector->rooms[0]->objects[0]->location = csVector3 (-1, 2, -1);
	sector->rooms[0]->objects[0]->rotation = csVector3 (0, 0, 0);
	sector->rooms[0]->objects[0]->scale = csVector3 (1, 1, 1);

	// create second (connector) room
	// allocate 6 walls for the connector
	sector->rooms[1]->walls = (chWallStructPtr*) malloc (6 * sizeof (chWallStruct));
	for (int i = 0; i < 6; i++) {
		sector->rooms[1]->walls[i] = (chWallStructPtr) malloc (sizeof (chWallStruct));
		sector->rooms[1]->walls[i]->txtSize = csVector3 (1, 1, 1);
		strcpy(sector->rooms[1]->walls[i]->strWallTexture, "wall_text");
	}

	// ceiling
	sector->rooms[1]->walls[0]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[0]->wall.AddVertex (-door_width, ceiling-5, 2*length); sector->rooms[1]->walls[0]->wall.AddVertex (-door_width, ceiling-5, length);
	sector->rooms[1]->walls[0]->wall.AddVertex (door_width, ceiling-5, length); sector->rooms[1]->walls[0]->wall.AddVertex (door_width, ceiling-5, 2*length);
	strcpy (sector->rooms[1]->walls[0]->strWallTexture, "ceil_text");

	// floor
	sector->rooms[1]->walls[1]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[1]->wall.AddVertex (-door_width, floor, length); sector->rooms[1]->walls[1]->wall.AddVertex (-door_width, floor, 2*length);
	sector->rooms[1]->walls[1]->wall.AddVertex (door_width, floor, 2*length); sector->rooms[1]->walls[1]->wall.AddVertex (door_width, floor, length);
	strcpy (sector->rooms[0]->walls[1]->strWallTexture, "floor_text");

	// left
	sector->rooms[1]->walls[2]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, floor, length); sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, ceiling-5, length);
	sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, ceiling-5, 2*length); sector->rooms[1]->walls[2]->wall.AddVertex (-door_width, floor, 2*length);

	// front
	sector->rooms[1]->walls[3]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[3]->wall.AddVertex (-door_width, floor, 2*length); sector->rooms[1]->walls[3]->wall.AddVertex (-door_width, ceiling-5, 2*length);
	sector->rooms[1]->walls[3]->wall.AddVertex (door_width, ceiling-5, 2*length); sector->rooms[1]->walls[3]->wall.AddVertex (door_width, floor, 2*length);

	// right
	sector->rooms[1]->walls[4]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[4]->wall.AddVertex (door_width, floor, 2*length); sector->rooms[1]->walls[4]->wall.AddVertex (door_width, ceiling-5, 2*length);
	sector->rooms[1]->walls[4]->wall.AddVertex (door_width, ceiling-5, length); sector->rooms[1]->walls[4]->wall.AddVertex (door_width, floor, length);

	// back
	sector->rooms[1]->walls[5]->wall = *(new csPoly3D (4));
	sector->rooms[1]->walls[5]->wall.AddVertex (door_width, floor, length); sector->rooms[1]->walls[5]->wall.AddVertex (door_width, ceiling-5, length);
	sector->rooms[1]->walls[5]->wall.AddVertex (-door_width, ceiling-5, length); sector->rooms[1]->walls[5]->wall.AddVertex (-door_width, floor, length);

	// allocate 8 lights for the connector room
	sector->rooms[1]->lights = (chLightStructPtr*) malloc (12 * sizeof (chLightStruct));
	for (int i = 0; i < 12; i++) {
		sector->rooms[1]->lights[i] = (chLightStructPtr) malloc (sizeof (chLightStruct));
		sector->rooms[1]->lights[i]->color = csColor (1, 1, 1);
		sector->rooms[1]->lights[i]->scale = 4.0;
	}

	for (int i = 0; i < 2; i++) {
		sector->rooms[1]->lights[0+i*4]->location = csVector3 (-door_width+delta, i*2 + 3, length+delta);
		sector->rooms[1]->lights[1+i*4]->location = csVector3 (-door_width+delta, i*2 + 3, 2*length-delta);
		sector->rooms[1]->lights[2+i*4]->location = csVector3 (door_width-delta, i*2 + 3, length+delta);
		sector->rooms[1]->lights[3+i*4]->location = csVector3 (door_width-delta, i*2 + 3, 2*length-delta);
		sector->rooms[1]->lights[2+i*4]->location = csVector3 (-door_width+delta, i*2 + 3, length+length/2);
		sector->rooms[1]->lights[3+i*4]->location = csVector3 (door_width-delta, i*2 + 3, length+length/2);
	}

	// allocate 1 door for connector room
	sector->rooms[1]->doors = (chDoorStructPtr*) malloc (sizeof (chDoorStruct));
	sector->rooms[1]->doors[0] = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	strcpy (sector->rooms[1]->doors[0]->strDoorTexture, "door_text");
	strcpy (sector->rooms[1]->doors[0]->strRoomName, "main");
	sector->rooms[1]->doors[0]->alpha = 0;
	sector->rooms[1]->doors[0]->txtSize = csVector3 (1, 1, 1);
	sector->rooms[1]->doors[0]->door = *(new csPoly3D (4));
	sector->rooms[1]->doors[0]->door.AddVertex (door_width, floor, length-2*door_delta); sector->rooms[1]->doors[0]->door.AddVertex (door_width, floor+10, length-2*door_delta);
	sector->rooms[1]->doors[0]->door.AddVertex (-door_width, floor+10, length-2*door_delta); sector->rooms[1]->doors[0]->door.AddVertex (-door_width, floor, length-2*door_delta);

	// allocate one object for connector room
	sector->rooms[1]->numObjects = 1;
	sector->rooms[1]->objects = (chObjectStructPtr*) malloc (sizeof (chObjectStruct));
	sector->rooms[1]->objects[0] = (chObjectStructPtr) malloc (sizeof (chObjectStruct));
	strcpy (sector->rooms[1]->objects[0]->strObjectModel, "user");
	strcpy (sector->rooms[1]->objects[0]->strObjectName, "object");
	sector->rooms[1]->objects[0]->intObjectType = ENTITY_TYPE_OBJECT;
	sector->rooms[1]->objects[0]->location = csVector3 (door_width-3, floor+2, length*3/2);
	sector->rooms[1]->objects[0]->rotation = csVector3 (0, 0, 0);
	sector->rooms[1]->objects[0]->scale = csVector3 (1, 1, 1);

	// set the number of outer doors
	sector->rooms[1]->numOuterDoors = 1;
	// allocate one outer door in this room
	sector->rooms[1]->outer_doors = (chOuterDoorStructPtr*) malloc (sizeof (chOuterDoorStruct));
	sector->rooms[1]->outer_doors[0] = (chOuterDoorStructPtr) malloc (sizeof (chOuterDoorStruct));
	strcpy (sector->rooms[1]->outer_doors[0]->strTargetSectorName, "sector1");
	strcpy (sector->rooms[1]->outer_doors[0]->strTargetSectorSource, "www.default.com/s1");
	sector->rooms[1]->outer_doors[0]->door = (chDoorStructPtr) malloc (sizeof (chDoorStruct));
	sector->rooms[1]->outer_doors[0]->door->alpha = 0;
	strcpy (sector->rooms[1]->outer_doors[0]->door->strDoorTexture, "door_text");
	strcpy (sector->rooms[1]->outer_doors[0]->door->strRoomName, "main");
	sector->rooms[1]->outer_doors[0]->door->txtSize = csVector3 (2, 3, 1);
	sector->rooms[1]->outer_doors[0]->door->door = *(new csPoly3D (4));
	sector->rooms[1]->outer_doors[0]->door->door.AddVertex (-door_width+2, floor, length*2-0.01); sector->rooms[1]->outer_doors[0]->door->door.AddVertex (-door_width+2, floor+3, length*2-0.01);
	sector->rooms[1]->outer_doors[0]->door->door.AddVertex (door_width-2, floor+3, length*2-0.01); sector->rooms[1]->outer_doors[0]->door->door.AddVertex (door_width-2, floor, length*2-0.01);
	strcpy (sector->rooms[1]->outer_doors[0]->strDoorName, "door2");

	return sector;
}