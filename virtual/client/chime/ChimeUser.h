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

  //Server user definition parameters
  char *strUserName;
  char* strUserPassword;
  char* strUserSource;
  char* strUserID;
  char* strGroupID;

public:
  ChimeUser ();																	//constructor
  ChimeUser::ChimeUser (const char *iUserName, const char *iUserPassword,
       const char *iUserSource, const char *iUserID, const char *iGroupID);		//another constructor
  ~ChimeUser ();																//destructor

  // User control functions
  bool SetupUser (iMeshWrapper *mesh, iCamera *camera);							//setup this user
  bool PlaceUser (csVector3 const &location, iSector* room);					//place user in a room
  bool MoveUser (csVector3 const direction, float fps, bool checkVertical);		//move user
  bool RotateUser (csVector3 const direction, float angle);						//rotate user

  // Getter functions
  const char* GetUserName ();
  const char* GetUserPassword ();
  const char* GetUserSource ();
  const char* GetUserID ();
  const char* GetGroupID ();

  // Setter functions
  void SetUserName (const char *iUserName);
  void SetUserPassword (const char *iUserPassword);
  void SetUserSource (const char *iUserSource);
  void SetUserID (const char *iUserID);
  void SetGroupID (const char *iGroupID);

  //Validate user
  bool IsUserValid ();

  // Debugging function
  void PrintUserParameters ();													//prints user's parameters
  void PrintUserLocation ();													//prints user's 3D coordinates and sector name
};

#endif // __ChimeUser_H__