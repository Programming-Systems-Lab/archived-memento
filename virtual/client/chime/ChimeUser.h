/*******************************************************************
    ChimeUser.h
	Author: Mark Galagan @ 2002

	Header file for a CHIME user definition
********************************************************************/

#ifndef __ChimeUser_H__
#define __ChimeUser_H__

#include <stdarg.h>
#include "csutil/ref.h"
#include "csgeom/vector3.h"
#include "csutil/csvector.h"
#include "iengine/mesh.h"
#include "iengine/sector.h"
#include "cstool/collider.h"

#define USER_BOX_RADIUS 0.25

struct iMeshWrapper;
struct iCamera;

/******************************************
 * Class definition for ChimeUser       *
 ******************************************/
class ChimeUser
{
private:

  //CHIME user variables
  csRef<iMeshWrapper>	chUserMesh;												//mesh used to represent the user
  csRef<iCamera>		chUserCamera;											//camera associated with this user

public:
  ChimeUser ();																	//constructor
  ~ChimeUser ();																//destructor

  bool SetupUser (iMeshWrapper *mesh, iCamera *camera);							//setup this user
  bool PlaceUser (csVector3 const &location, iSector* room);					//place user in a room
  bool MoveUser (csVector3 const direction, float fps, bool checkVertical);		//move user
  bool RotateUser (csVector3 const direction, float angle);						//rotate user
};

#endif // __ChimeUser_H__