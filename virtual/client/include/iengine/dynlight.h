/*
    Copyright (C) 1999 by Andrew Zabolotny <bit@eltech.ru>
    Copyright (C) 2000 by Jorrit Tyberghein

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

#ifndef __CS_IENGINE_DYNLIGHT_H__
#define __CS_IENGINE_DYNLIGHT_H__

#include "csutil/scf.h"

/**\file
 */
/**
 * \addtogroup engine3d_light
 * @{ */
 
class csDynLight;
struct iObject;
struct iLight;
struct iLightingInfo;

SCF_VERSION (iDynLight, 0, 2, 0);

/**
 * The iDynLight interface represents a dynamic light.
 */
struct iDynLight : public iBase
{
  /// Get the private pointer to csDynLight (ugly).
  virtual csDynLight* GetPrivateObject () = 0;

  /// Get the iObject for this light.
  virtual iObject *QueryObject () = 0;
  /// Get the iLight for this light.
  virtual iLight *QueryLight () = 0;

  /**
   * Add a mesh to this dynamic light. This is usually
   * called during Setup() by meshes that are hit by the
   * dynamic light.
   */
  virtual void AddAffectedLightingInfo (iLightingInfo* li) = 0; 

  /**
   * Remove a mesh from this dynamic light.
   */
  virtual void RemoveAffectedLightingInfo (iLightingInfo* li) = 0; 

  /// Setup the light (i.e. do the lighting calculations).
  virtual void Setup () = 0;

  /// Get the next dynamic light in the list.
  virtual iDynLight* GetNext () = 0;
};

/** @} */

#endif // __CS_IENGINE_DYNLIGHT_H__

