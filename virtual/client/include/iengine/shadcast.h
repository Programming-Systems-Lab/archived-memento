/*
    Crystal Space 3D engine
    Copyright (C) 2001 by Jorrit Tyberghein

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

#ifndef __CS_IENGINE_SHADCAST_H__
#define __CS_IENGINE_SHADCAST_H__

/**\file
 */
/**
 * \addtogroup engine3d_light
 * @{ */
 
#include "csutil/scf.h"

struct iShadowCaster;
struct iShadowReceiver;
struct iShadowBlock;
struct iShadowBlockList;
struct iMovable;
struct iFrustumView;
class csBox3;

SCF_VERSION (iShadowCaster, 0, 0, 3);

/**
 * An object that can cast shadows. An object implementing this interface
 * also implements iVisibilityObject so that it can be registered with
 * a visibility culler.
 */
struct iShadowCaster : public iBase
{
  /**
   * Append a list of shadow frustums which extend from
   * this shadow caster. The origin is the position of the light.
   */
  virtual void AppendShadows (iMovable* movable, iShadowBlockList* shadows, const csVector3& origin) = 0;
};

SCF_VERSION (iShadowReceiver, 0, 0, 2);

/**
 * An object that is interested in getting shadow information.
 */
struct iShadowReceiver : public iBase
{
  /// Cast shadows on this receiver.
  virtual void CastShadows (iMovable* movable, iFrustumView* fview) = 0;
};

/** @} */

#endif // __CS_IENGINE_SHADCAST_H__

