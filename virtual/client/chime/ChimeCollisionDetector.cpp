#include "cssysdef.h"
#include "qint.h"
#include "qsqrt.h"
#include "imesh/thing/polygon.h"
#include "imesh/terrfunc.h"
#include "imesh/object.h"
#include "imesh/thing/polygon.h"
#include "imesh/thing/thing.h"
#include "csgeom/transfrm.h"
#include "iengine/movable.h"
#include "iutil/plugin.h"
#include "iutil/vfs.h"
#include "igeom/polymesh.h"
#include "imap/loader.h"

#include "ChimeSystemDriver.h"
#include "ChimeCollisionDetector.h"

extern ChimeSystemDriver *driver;


/****************************************************************
 * Constructor reads collider objects (body and legs) dimensions
 * from the configuration file.
 ****************************************************************/
ChimeCollisionDetector::ChimeCollisionDetector()
{
	//Initialize all objects to null
	chUserBody = NULL;
	chUserLegs = NULL;
	chUserBodyCollider = NULL;
	chUserLegsCollider = NULL;
}


/*****************************************************************
 * Create a collider box for any given object
 *****************************************************************/
bool ChimeCollisionDetector::CreateObjectCollider(iMeshWrapper *mesh)
{
	csRef<iPolygonMesh> polygon = SCF_QUERY_INTERFACE (mesh->GetMeshObject (), iPolygonMesh);
	csColliderWrapper *cw = new csColliderWrapper (mesh->QueryObject (), 
		driver->csCollisionSystem, polygon);
	cw->SetName (mesh->QueryObject ()->GetName());
	cw->DecRef ();
	if (!cw)
		return false;
	return true;
}


/*******************************************************************
 * Create a collider for the user camera.
 * Collider consists of a mesh (with no texture) that represents
 * an invisible bounding box for camera, since the camera itself
 * has no physical object associated with it.
 * There is also the collider wrapper, that  is associated with
 * the bounding box, and is used for actual collision detection.
 * For a more detailed description of the actual bounding box,
 * see Collision Detection part in chime.cfg configuration file.
 *******************************************************************/

void ChimeCollisionDetector::CreateUserCollider ()
{
	// Create a polygon for the bounding box
	// It is defined later by vertices
	iPolygon3D *p;
	csRef<iPolygonMesh> mesh;
	csRef<iPluginManager> plugin_mgr = driver->csPluginManager;
	csRef<iMeshObjectType> ThingType (CS_QUERY_PLUGIN_CLASS (plugin_mgr, 
		"crystalspace.mesh.object.thing", iMeshObjectType));
	if (!ThingType)
		ThingType = CS_LOAD_PLUGIN (plugin_mgr,
		"crystalspace.mesh.object.thing", iMeshObjectType);

	// Create the mesh wrapper for the bounding box body
	csRef<iMeshFactoryWrapper> thingMeshFact = driver->csEngine->CreateMeshFactory("crystalspace.mesh.object.thing", "thingMeshFact");
	csRef<iMeshObject> mesh_obj = thingMeshFact->GetMeshObjectFactory()->NewInstance();
	try {
		chUserBody = thingMeshFact->CreateMeshWrapper();
		chUserBody->SetMeshObject (mesh_obj);
	}
	catch (...) {
		driver->Report("crystalspace.application.ChimeCollisionDetector", 
						"Could not create the user collider\n");
		return;
	}

	csRef<iThingState> thing_state (SCF_QUERY_INTERFACE (mesh_obj, iThingState));

	// Define the 3D polygon of the bounding box with vertices
	// It is nothing but a simple box defined by 8 vertices,

	// First, find the dimensions, based on constants defined in
	// the header file. Body_y is the vertical coordinate of the
	// basis of the body box. (0, 0, 0) is assumed to be the
	// basis for all calculations (camera is initially placed
	// at (0, 0, 0)
	float body_radius = USER_RADIUS;
	float body_y = -USER_RADIUS;
	float body_height = 2 * USER_RADIUS;

	// Now create the vertices
	thing_state->CreateVertex (csVector3 (-body_radius, body_y,    -body_radius));
	thing_state->CreateVertex (csVector3 (-body_radius, body_y,    body_radius));
	thing_state->CreateVertex (csVector3 (-body_radius, body_y+body_height, body_radius));
	thing_state->CreateVertex (csVector3 (-body_radius, body_y+body_height, -body_radius));
	thing_state->CreateVertex (csVector3 (body_radius,  body_y,    -body_radius));
	thing_state->CreateVertex (csVector3 (body_radius,  body_y,    body_radius));
	thing_state->CreateVertex (csVector3 (body_radius,  body_y+body_height, body_radius));
	thing_state->CreateVertex (csVector3 (body_radius,  body_y+body_height, -body_radius));

	// Build the 3D polygon with vertices
	// Left
	p = thing_state->CreatePolygon ();
	p->CreateVertex (0); p->CreateVertex (1);
	p->CreateVertex (2); p->CreateVertex (3);

	// Right
	p = thing_state->CreatePolygon ();
	p->CreateVertex (4); p->CreateVertex (5);
	p->CreateVertex (6); p->CreateVertex (7);

	// Bottom
	p = thing_state->CreatePolygon ();
	p->CreateVertex (3); p->CreateVertex (2);
	p->CreateVertex (7); p->CreateVertex (6);

	// Top
	p = thing_state->CreatePolygon ();
	p->CreateVertex (1); p->CreateVertex (0);
	p->CreateVertex (5); p->CreateVertex (4);

	// Front
	p = thing_state->CreatePolygon ();
	p->CreateVertex (1); p->CreateVertex (4);
	p->CreateVertex (7); p->CreateVertex (2);

	// Back
	p = thing_state->CreatePolygon ();
	p->CreateVertex (5); p->CreateVertex (0);
	p->CreateVertex (0); p->CreateVertex (6);

	// Create the collider wrapper for the bounding box
	mesh = SCF_QUERY_INTERFACE (mesh_obj, iPolygonMesh);
	chUserBodyCollider = new csColliderWrapper (chUserBody->QueryObject(), driver->csCollisionSystem, mesh);
	chUserBodyCollider->SetName ("player body");

	//------- Repeat the procedure for user legs --------------------//

	// Create the mesh wrapper for the bounding box legs
	mesh_obj  = thingMeshFact->GetMeshObjectFactory()->NewInstance();
	try {
		chUserLegs = thingMeshFact->CreateMeshWrapper ();
		chUserLegs->SetMeshObject (mesh_obj);
	}
	catch (...) {
		driver->Report("crystalspace.application.ChimeCollisionDetector", 
						"Could not create the user collider\n");
		return;
	}

	thing_state = SCF_QUERY_INTERFACE (mesh_obj, iThingState);

	// Define the 3D polygon of the bounding box with vertices

	// Use body dimensions to define the legs bounding box.
	// Legs_y is the vertical coordinate of the basis of
	// the bounding box, placed such that legs heigh
	// plus body box radius amount to USER_HEIGHT, since that
	// is where camera (or viewer's eyes should be).
	float legs_radius = body_radius - 0.1; 
	float legs_y =  -USER_HEIGHT;
	float legs_height = -body_radius;

	// Create the vertices
	thing_state->CreateVertex (csVector3 (-legs_radius, legs_y,     -legs_radius));
	thing_state->CreateVertex (csVector3 (-legs_radius, legs_y,     legs_radius));
	thing_state->CreateVertex (csVector3 (-legs_radius, legs_height, legs_radius));
	thing_state->CreateVertex (csVector3 (-legs_radius, legs_height, -legs_radius));
	thing_state->CreateVertex (csVector3 (legs_radius,  legs_y,     -legs_radius));
	thing_state->CreateVertex (csVector3 (legs_radius,  legs_y,     legs_radius));
	thing_state->CreateVertex (csVector3 (legs_radius,  legs_height, legs_radius));
	thing_state->CreateVertex (csVector3 (legs_radius,  legs_height, -legs_radius));

	// Build the 3D polygon with vertices
	// Left
	p = thing_state->CreatePolygon ();
	p->CreateVertex (0); p->CreateVertex (1);
	p->CreateVertex (2); p->CreateVertex (3);

	// Right
	p = thing_state->CreatePolygon ();
	p->CreateVertex (4); p->CreateVertex (5);
	p->CreateVertex (6); p->CreateVertex (7);

	// Bottom
	p = thing_state->CreatePolygon ();
	p->CreateVertex (0); p->CreateVertex (1);
	p->CreateVertex (5); p->CreateVertex (4);

	// Top
	p = thing_state->CreatePolygon ();
	p->CreateVertex (3); p->CreateVertex (2);
	p->CreateVertex (6); p->CreateVertex (7);

	// Front
	p = thing_state->CreatePolygon ();
	p->CreateVertex (1); p->CreateVertex (5);
	p->CreateVertex (6); p->CreateVertex (2);

	// Back
	p = thing_state->CreatePolygon ();
	p->CreateVertex (0); p->CreateVertex (4);
	p->CreateVertex (7); p->CreateVertex (3);

	// Create the collider wrapper for the bounding box
	mesh = SCF_QUERY_INTERFACE (mesh_obj, iPolygonMesh);
	chUserLegsCollider = new csColliderWrapper (chUserLegs->QueryObject(), driver->csCollisionSystem, mesh);
	chUserLegsCollider->SetName ("user_legs_collider");
}


/*************************************************************************
 * Detect collision between the collider and any other objects
 * in the given room, using the transform matrix given for the
 * bounding box.
 *************************************************************************/
int ChimeCollisionDetector::CollisionDetect(csColliderWrapper *collider, iSector* room, 
							   csReversibleTransform *transform, iMeshList *meshes)
{
	// Allocate some variables
	int hit = 0;
	int j;

	// For all meshes...
	for (int i = 0; i < meshes->GetCount(); i++)
	{
		// Get the mesh for the object
		csRef<iMeshWrapper> mesh = meshes->Get (i);
		csColliderWrapper *wrapper = csColliderWrapper::GetColliderWrapper (mesh->QueryObject ());

		if (wrapper)
		{
			// Reset the collision pairs, as we are calculating
			// new collision objects
			driver->csCollisionSystem->ResetCollisionPairs ();

			// Collide camera bounding box with object, and if
			// collision pair exists, increment our hit count
			if (collider->Collide (mesh->QueryObject(), transform, &mesh->GetMovable ()->GetTransform ()))
				hit++;
		}
	}

	// Return the number of collisions detected
	return hit;
}

/*************************************************************************
 * Collide the new position where the user wants to go with objects
 * in the room. Correct user's vertical position if necessary.
 *************************************************************************/
csVector3 ChimeCollisionDetector::Collide(iSector *room, csOrthoTransform& camera_transform, csVector3& pos, csVector3& vel, bool checkVertical)
{
	// new_pos is the location where the camera would end up
	// if it moved in the direction given
	csVector3 vel_translate (camera_transform.This2OtherRelative(vel));
	vel_translate.y = 0;
	csVector3 new_pos = pos + vel_translate;
	csMatrix3 m;
	csOrthoTransform test (m, new_pos);
	iMeshList *meshes = driver->getCurrentSector ()->GetCurrentRoom ()->GetMeshes ();

	// calculate how many collisions new_pos would result in if
	// body bounding box moved there
	int hits_body = CollisionDetect (chUserBodyCollider, room, &test, meshes);

	// if body box collides with two walls, then definitely cannot move there
	if (hits_body == 2)
		return pos;

	// if body box collides with one wall, attempt sliding along that wall
	if (hits_body == 1) {
		
		// get the triangle long which the collision occured and create a plane
		// that contains this triangle, and get the normal into that wall
		csCollisionPair *pair = driver->csCollisionSystem->GetCollisionPairs ();
		csPlane3 wall (pair->a2, pair->b2, pair->c2);
		csVector3 wall_normal (wall.Normal ());
		// invert normal to point into the wall, not from it
		wall_normal = -wall_normal;

		// project the direction vector on to the wall normal
		csVector3 dir = new_pos - pos; dir.y = 0;
		csVector3 projection = ((wall_normal * dir) / wall_normal.SquaredNorm()) * wall_normal;

		// the difference between the direction and the normal
		// will give direction of sliding along the wall
		vel_translate =  (dir - projection) * 3 / 4;

		// update the transform and direction
		test.SetOrigin (pos + vel_translate);

		// this is used for those cases when the user is in the corner
		// (sliding will bring the user into the other wall)
		if (CollisionDetect (chUserBodyCollider, room, &test, meshes))
			return pos;
	}

	// same as above, only for the legs bounding box
	int hits_legs = CollisionDetect (chUserLegsCollider, room, &test, meshes);

	// if there is no collision, then we are in the air, and must fall
	if (!hits_legs && checkVertical) {

		// tell the system that the user is not on the ground
		driver->isOnGround = false;
		
		// keep trying incremental small falls by 0.1
		// up to 3 times (that is the maximum fall per frame
		// to simulate smooth gravity effects)
		for (int i=0; i<8; i++) {

			// test new position after a small fall
			test.SetOrigin (test.GetOrigin() + csVector3 (0, -0.05, 0));

			// if landed, tell the system, and go up just a bit, so that
			// the collider doesn't think we are constantly climbing up
			if (CollisionDetect (chUserLegsCollider, room, &test, meshes)) {
				driver->isOnGround = true;
				test.SetOrigin (test.GetOrigin() + csVector3 (0, +0.05, 0));
				break;
			}
		}
	}

	// if there is a collision, then we need to go up by some amount
	if (hits_legs) {
		// keep going up in small, incremental steps until no collision
		// is detected
		do {
			test.SetOrigin (test.GetOrigin() + csVector3 (0, 0.01, 0));
		}
		while (CollisionDetect (chUserLegsCollider, room, &test, meshes));
	}

	// move body and legs bounding boxes
	chUserBody->GetMovable()->MovePosition(test.GetOrigin());
	chUserLegs->GetMovable()->MovePosition(test.GetOrigin());

	// return new position
	return test.GetOrigin();
}