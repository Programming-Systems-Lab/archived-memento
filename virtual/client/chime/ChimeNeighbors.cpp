/*******************************************************************
    ChimeNeighbors.cpp
	Author: Mark Galagan @ 2003

	Implements a FIFO (First-In-First-Out) queue to hold
	neighboring ChimeSector objects.
********************************************************************/


#include "cssysdef.h"
#include "ChimeNeighbors.h"

/*****************************************************************
 * Constructor: creates a new queue with default size
 *****************************************************************/
ChimeNeighbors::ChimeNeighbors ()
{
	size = DEFAULT_NEIGHBORHOOD_SIZE;
	queue = new csVector (size, size);
}


/*****************************************************************
 * Constructor: creates a new queue with given size
 *****************************************************************/
ChimeNeighbors::ChimeNeighbors (int s_size)
{
	s_size = (s_size > 0 ? s_size : 0);
	size = s_size;
	queue = new csVector (size, size);
}


/*****************************************************************
 * Destructor: frees the queue
 *****************************************************************/
ChimeNeighbors::~ChimeNeighbors ()
{
	FreeQueue ();
}


/*****************************************************************
 * SetSize: allocates a queue of new size and copies contents
 * over to new queue. If new size is smaller, extra entries are
 * discarded (according to FIFO ordering)
 *****************************************************************/
void ChimeNeighbors::SetQueueSize (int new_size)
{
	new_size = (new_size > 0 ? new_size : 0);
	
	// ignore if new size is the same as current size
	if (new_size == size)
		return;

	// if new size is bigger, simply copy
	// existing elements in FIFO order
	if (new_size > size) 
	{
		queue->SetLength (new_size);
		size = new_size;
		return;
	}

	// if new size is smaller, remove objects in FIFO order
	if (new_size < size)
	{
		int length = queue->Length ();
		if (length > new_size)
		{
			for (int i = 0; i < new_size - length; i++)
			{
				try
				{
                    queue->Delete (0);
				}
				catch (...)
				{
					break;
				}
			}
		}
		size = new_size;
		return;
	}
}


/*****************************************************************
 * GetSize: returns the size of the queue
 *****************************************************************/
int ChimeNeighbors::GetQueueSize ()
{
	return size;
}


/*****************************************************************
 * FindSector: returns the sector with given name and URL
 * or NULL is such sector is not in the queue
 *****************************************************************/
ChimeSector* ChimeNeighbors::FindSector (char *strSectorName, char *strSectorURL)
{
	ChimeSector *sector = NULL;
	ChimeSector *temp;
	for (int i = 0; i < queue->Length (); i++)
	{
		temp = (ChimeSector*) queue->Get (i);
		if (temp != NULL && temp->IsThisSector (strSectorName, strSectorURL))
		{
			sector = temp;
			return sector;
		}
	}
	return sector;
}


/*****************************************************************
 * AddSector: adds a sector to the queue of neighbors. 
 * If the queue is full, removes one sector in FIFO order
 *****************************************************************/
bool ChimeNeighbors::AddSector (ChimeSector *sector)
{
	// remove oldest sector if necessary
	if (queue->Length () == size)
	{
		try
		{
            queue->Delete (0);
		}
		catch (...)
		{
			return false;
		}
	}

	// add new sector
	try
	{
		queue->Push (sector);
	}
	catch (...)
	{
		return false;
	}

	return true;
}


/*****************************************************************
 * FreeAll: deletes all neighbors and the queue
 *****************************************************************/
bool ChimeNeighbors::FreeQueue ()
{
	try
	{
		queue->DeleteAll (true);
	}
	catch (...)
	{
		return false;
	}
	return true;
}