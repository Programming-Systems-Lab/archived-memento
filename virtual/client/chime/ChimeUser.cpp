/*******************************************************************
    ChimeUser.cpp
	Author: Mark Galagan @ 2002

	Object wrapper for the user of CHIME.
	It represents the first-person object (mesh and camera)
	for the main user of CHIME.
	It holds simple functions for user control, like
	loading the meshes, moving and rotating the user
	and the associated camera, etc.
********************************************************************/

#include "cssysdef.h"
#include "iutil/objreg.h"
#include "iutil/plugin.h"
#include "iengine/camera.h"
#include "iengine/movable.h"
#include "imesh/sprite3d.h"
#include "imesh/object.h"
#include "imesh/thing/polygon.h"
#include "iengine/light.h"
#include "iengine/engine.h"
#include "igeom/polymesh.h"
#include "iengine/material.h"
#include "ivaria/collider.h"
#include "cstool/collider.h"

#include "ChimeUser.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

/*****************************************************************
 * Constructor: does nothing, since using smart pointers
 *****************************************************************/
ChimeUser::ChimeUser ()
{

}

/*****************************************************************
 * Destructor: does nothing, since using smart pointers
 *****************************************************************/
ChimeUser::~ChimeUser ()
{

}

/*****************************************************************
 * Sets up the user. Creates the user mesh with given name 
 * from given mesh factory and stores the associated camera.
 *****************************************************************/
bool ChimeUser::SetupUser (iMeshWrapper *mesh, iCamera *camera)
{
	// Assign variables
	chUserMesh = mesh;
	if (chUserMesh) {
		csRef<iSprite3DState> state = SCF_QUERY_INTERFACE (chUserMesh->GetMeshObject (), iSprite3DState);
		state->SetAction ("default");
		chUserMesh->SetZBufMode (CS_ZBUF_USE);
		chUserMesh->DeferUpdateLighting (CS_NLIGHT_STATIC | CS_NLIGHT_DYNAMIC, 10);
	}
	chUserCamera = camera;

	return true;
}


/*****************************************************************
 * Places the user in a room at the given location
 * Create colliders for user's body and legs, setting
 * the user's eyes to be at user_height
 *****************************************************************/
bool ChimeUser::PlaceUser (csVector3 const &location, iSector* room)
{
	// Place mesh representing the user
	if (chUserMesh) {
		chUserMesh->GetMovable()->SetSector(room);
		chUserMesh->GetMovable()->SetPosition(room, location);
	}

	// Create colliders, now that user is placed
	driver ->GetCollider ()->CreateUserCollider ();

	return true;
}

/*****************************************************************
 * Moves the user in the specified direction
 *****************************************************************/
bool ChimeUser::MoveUser (csVector3 const direction, float fps, bool checkVertical)
{
	//chUserCamera->Move (direction, true);
	//return true;
	
	// Find the new position where the user would end up
	csVector3 vel (direction);
	csVector3 pos = driver->GetCollider ()->Collide (
		chUserCamera, chUserCamera->GetTransform().GetOrigin(), vel, checkVertical, fps);

	// Move the camera
	chUserCamera->GetTransform ().SetOrigin (pos);

	// Update current sector if necessary
	if (chUserCamera->GetSector () != driver->GetCurrentSector ()->GetCurrentRoom ())
	{
		driver->GetCurrentSector ()->SetCurrentRoom (chUserCamera->GetSector ());
		if (chUserMesh)
			chUserMesh->GetMovable ()->SetSector (chUserCamera->GetSector ());
	}

	// Move mesh representing the user
	if (chUserMesh) {
		chUserMesh->GetMovable ()->GetTransform ().SetOrigin (pos);
		chUserMesh->GetMovable ()->UpdateMove ();
	}
	
	return true;
}

/*****************************************************************
 * Rotates the user by the specified angle in the
 * specified direction
 *****************************************************************/
bool ChimeUser::RotateUser (csVector3 const direction, float angle)
{

	// if rotating left or right, use csOrthoTransform.RotateOther
	// to rotate, so as not to screw up the camera's transformation
	// matrix, otherwise, use RotateThis
	if (direction == CS_VEC_ROT_RIGHT || direction == CS_VEC_ROT_LEFT) 
	{
		chUserCamera->GetTransform ().RotateOther (direction, angle);
		if (chUserMesh)
			chUserMesh->GetMovable ()->GetTransform ().RotateOther (direction, angle);
	}
	else {
		chUserCamera->GetTransform ().RotateThis (direction, angle);
		if (chUserMesh)
			chUserMesh->GetMovable ()->GetTransform ().RotateThis (direction, angle);
	}

	// If we are using a user mesh, we need to call this
	// to finalize the move
	if (chUserMesh)
		chUserMesh->GetMovable ()->UpdateMove ();
	
	return true;
}