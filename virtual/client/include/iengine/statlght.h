/*
    Copyright (C) 1999 by Andrew Zabolotny <bit@eltech.ru>

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.

    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free
    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

#ifndef __CS_IENGINE_STATLGHT_H__
#define __CS_IENGINE_STATLGHT_H__

/**\file
 */
/**
 * \addtogroup engine3d_light
 * @{ */
 
#include "csutil/scf.h"

class csStatLight;
struct iObject;
struct iLight;
struct iLightingInfo;

SCF_VERSION (iStatLight, 0, 0, 3);

/**
 * The iStatLight interface is the SCF interface
 * for the csStatLight class.
 */
struct iStatLight : public iBase
{
  /// Used by the engine to retrieve internal sector object (ugly)
  virtual csStatLight *GetPrivateObject () = 0;

  /// Return the iObject for this light
  virtual iObject *QueryObject () = 0;
  /// Return the iLight for this light
  virtual iLight *QueryLight () = 0;

  /**
   * Add a mesh to this static light. This is usually
   * called during the lighting procedure of a pseudo-dynamic light.
   */
  virtual void AddAffectedLightingInfo (iLightingInfo* li) = 0; 
};

/** @} */

#endif // __CS_IENGINE_STATLGHT_H__

