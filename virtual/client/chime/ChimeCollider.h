/*******************************************************************
    ChimeCollider.h
	Author: Mark Galagan @ 2002

	Header file for a CHIME collision detector definition
********************************************************************/

#ifndef __ChimeCollider_H__
#define __ChimeCollider_H__

#include <stdarg.h>
#include "iengine/mesh.h"
#include "ivaria/collider.h"
#include "cstool/collider.h"
#include "csgeom/vector3.h"
#include "iutil/objreg.h"
#include "iengine/sector.h"
#include "iengine/engine.h"
#include "csengine/camera.h"

#define MAX_SECTORS 20
#define USER_HEIGHT 1.5
#define USER_RADIUS 0.3

/*****************************************************
 * Class definition for ChimeCollider       *
 *****************************************************/
class ChimeCollider
{
private:

  //Objects representing user's body and legs
  csRef<iMeshWrapper> chUserBody;												//user's body
  csRef<iMeshWrapper> chUserLegs;												//user's legs
  csColliderWrapper *chUserBodyCollider, *chUserLegsCollider;					//colliders associated with user's body and legs

  //Handles for frequently used variables of collision detection
  csRef<iSector> room;															//current room
  csOrthoTransform camera_transform;											//transform of the camera used
  csVector3 new_cam_position;													//position where the camera moves
  csRef<iMeshList> room_meshes;													//meshes in the room

  //Collision methods
  int CollisionDetect (csColliderWrapper *collider,								//check collisions of the camera collider
	  iSector* room, csReversibleTransform *transform, iMeshList *meshes);		//with nearby objects in the given sector
  
public:
  ChimeCollider ();																//constructor
  ~ChimeCollider ();															//destructor

  void CreateUserCollider ();													//create colliders for the camera at user_height
  bool CreateMeshCollider(iMeshWrapper *mesh);									//add a collider wrapper to any given mesh
  bool RemoveMeshCollider (iMeshWrapper *object);								//remove collider wrapper
  void SetRoomFloor (float y);													//set the y coordinate of the room floor
  csVector3 Collide (iCamera *camera, csVector3 pos, 
						csVector3& vel, bool checkVertical, float fps);			//move colliders, while doing collision checking, return new position
  csVector3 CollideObject (iMeshWrapper *mesh, iSector *room, 
						csVector3 const origin, csVector3 new_pos);
};

#endif // __ChimeCollider_H__