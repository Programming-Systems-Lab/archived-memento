/*******************************************************************
    ChimeSectorUser.cpp
	Author: Mark Galagan @ 2003

	Defines a container for a user.
********************************************************************/

#include "cssysdef.h"
#include "csgeom/transfrm.h"
#include "ChimeSectorEntities.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;


/*****************************************************************
 * Constructor: creates a new container for given object mesh
 *****************************************************************/
ChimeSectorUser::ChimeSectorUser (char *iObjectName, char *iObjectSource,
								  iMeshWrapper *iMesh, csVector3 *iObjectLocation, 
								  iSector *iObjectRoom, char *iObjectModel, 
								  char *iObjectMaterial)
	: ChimeSectorObject (iObjectName, iObjectSource, iMesh, iObjectLocation, iObjectRoom, 
	iObjectModel, iObjectMaterial, ENTITY_TYPE_USER)
{
	return;
}


/*****************************************************************
 * HandleLeftMouseDoubleClick: 
 * Object is activated.
 *****************************************************************/
void ChimeSectorUser::HandleLeftMouseDoubleClick (iEvent &event) 
{
	printf ("User is activated.\n");
}



/*****************************************************************
 * HandleRightMouseClick: 
 * Menu is created.
 *****************************************************************/
void ChimeSectorUser::HandleRightMouseClick (iEvent &event) 
{
	csMenu* menu = driver->CreateMenu (event.Mouse.x, event.Mouse.y);
	char mText [100];
	strcpy (mText, "USER: ");
	strcat (mText, strEntityName);
	(void)new csMenuItem (menu, mText, -1);
	menu->SetPos (event.Mouse.x - 3, event.Mouse.y + 3);
}
