/*******************************************************************
    ChimeApp.h
	Author: Mark Galagan @ 2002

	Header file for a CHIME application definition
********************************************************************/

#ifndef __ChimeApp_H__
#define __ChimeApp_H__

#include <stdarg.h>
#include "csws/csws.h"

/******************************************
 * Class definition for ChimeApp          *
 ******************************************/
class ChimeApp : public csApp
{
private:

public:
  ChimeApp (iObjectRegistry *object_reg, csSkin &skin);				//constructor
  ~ChimeApp ();														//destructor
  bool StartApplication ();											//start application
  bool Initialize (iObjectRegistry *object_reg);					//initialize environment
};

#endif // __ChimeApp_H__