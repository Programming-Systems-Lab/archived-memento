/*******************************************************************
    ChimeNeighbors.h
	Author: Mark Galagan @ 2003

	Header file for a CHIME FIFO queue of ChimeSector neighbors
********************************************************************/

#ifndef __ChimeNeighbors_H__
#define __ChimeNeighbors_H__

#include <stdarg.h>
#include "csutil/csvector.h"
#include "iengine/sector.h"
#include "ChimeSector.h"

#define DEFAULT_NEIGHBORHOOD_SIZE 8

/*****************************************************
 * Class definition for ChimeNeighbors				 *
 *****************************************************/
class ChimeNeighbors
{
private:
  csVector *queue;																// FIFO queue
  int size;																		// determines the size of the queue
  
public:
  ChimeNeighbors ();															// constructor
  ChimeNeighbors (int s_size);													// constructor that initializes queue size
  ~ChimeNeighbors ();															// destructor

  void SetQueueSize (int new_size);												// set the size of the queue
  int  GetQueueSize ();															// return the size of the queue

  bool AddSector (ChimeSector *sector);											// add a ChimeSector pointer to the queue
  ChimeSector* FindSector (char *strSectorName, char *strSectorSource);			// find a sector with these parameters, or return NULL if not found
  ChimeSector* FindSector (iSector *room);										// find a sector that hosts given room
  bool FreeQueue ();															// release all ChimeSector objects
};

#endif // __ChimeNeighbors_H__