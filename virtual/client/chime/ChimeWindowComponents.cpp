#include "cssysdef.h"
#include "cssys/sysdriv.h"
#include "csws/csws.h"
#include "csver.h"
#include "ivideo/fontserv.h"
#include "ChimeWindowToolkit.h"
#include "ChimeSystemDriver.h"

// The global pointer to ChimeSystemDriver
extern ChimeSystemDriver *driver;

ChimeButton::ChimeButton (csComponent *iParent, int iCommandCode, 
		int iButtonStyle, csButtonFrameStyle iFrameStyle)
		: csButton (iParent, iCommandCode, iButtonStyle, iFrameStyle)
{
	printf("Chime button created\n");
	currentSchemaID = driver->GetSchemaID ();
}

void ChimeButton::Draw ()
{
	csButton::Draw ();

	if (driver->GetSchemaID () == currentSchemaID)
		return;

	currentSchemaID = driver->GetSchemaID ();
	printf("Current style: %d\n", currentSchemaID );
}