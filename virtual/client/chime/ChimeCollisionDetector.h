/*******************************************************************
    ChimeCollisionDetector.h
	Author: Mark Galagan @ 2002

	Header file for a CHIME collision detector definition
********************************************************************/

#ifndef __ChimeCollisionDetector_H__
#define __ChimeCollisionDetector_H__

#include <stdarg.h>
#include "iengine/mesh.h"
#include "ivaria/collider.h"
#include "cstool/collider.h"
#include "csgeom/vector3.h"
#include "iutil/objreg.h"
#include "iengine/sector.h"
#include "iengine/engine.h"

#define MAX_SECTORS 20
#define USER_HEIGHT 1.5
#define USER_RADIUS 0.3

/*****************************************************
 * Class definition for ChimeCollisionDetector       *
 *****************************************************/
class ChimeCollisionDetector
{
private:

  //Objects representing user's body and legs
  csRef<iMeshWrapper> chUserBody;												//user's body
  csRef<iMeshWrapper> chUserLegs;												//user's legs
  csColliderWrapper *chUserBodyCollider, *chUserLegsCollider;					//colliders associated with user's body and legs

  //Collision methods
  int CollisionDetect (csColliderWrapper *collider,								//check collisions of the camera collider
	  iSector* room, csReversibleTransform *transform, iMeshList *meshes);		//with nearby objects in the given sector
  
public:
  ChimeCollisionDetector ();													//constructor
  ~ChimeCollisionDetector ();													//destructor

  void CreateUserCollider ();													//create colliders for the camera at user_height
  bool CreateObjectCollider(iMeshWrapper *mesh);								//add a collider wrapper to any given object
  void SetRoomFloor (float y);													//set the y coordinate of the room floor
  csVector3 Collide (iSector *room, csOrthoTransform& camera_transform, 
	  csVector3& pos, csVector3& vel, bool checkVertical);						//move colliders, while doing collision checking, return new position
};

#endif // __ChimeCollisionDetector_H__