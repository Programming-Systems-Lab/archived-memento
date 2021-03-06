/*
    Copyright (C) 2000-2001 by Jorrit Tyberghein
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

#ifndef __CS_IENGINE_LIGHT_H__
#define __CS_IENGINE_LIGHT_H__

/**\file
 */
/**
 * \addtogroup engine3d_light
 * @{ */
 
#include "csutil/scf.h"
#include "iengine/fview.h"

class csLight;
class csColor;
class csFlags;
struct iLight;
struct iSector;
struct iObject;
struct iCrossHalo;
struct iNovaHalo;
struct iFlareHalo;

/** \name Light flags
 * @{ */
/**
 * If CS_LIGHT_THINGSHADOWS is set for a light then things will also
 * cast shadows. This flag is set by default for static lights and unset
 * for dynamic lights.
 */
#define CS_LIGHT_THINGSHADOWS	0x00000001

/** 
 * If this flag is set, the halo for this light is active and is in the
 * engine's queue of active halos. When halo become inactive, this flag
 * is reset.
 */
#define CS_LIGHT_ACTIVEHALO	0x80000000
/** @} */

/// Light level that is used when there is no light on the texture.
#define CS_DEFAULT_LIGHT_LEVEL 20
/// Light level that corresponds to a normally lit texture.
#define CS_NORMAL_LIGHT_LEVEL 128

/** \name Attenuation modes
 * Attenuation controls how the brightness of a light fades with distance.
 * There are four attenuation formulas:
 * <ul>
 *   <li> no attenuation = light * 1
 *   <li> linear attenuation = light * (radius - distance) / radius
 *   <li> inverse attenuation = light * (radius / distance)
 *   <li> realistic attenuation = light * (radius^2 / distance^2)
 * </ul>
 * @{ */
/// no attenuation: light * 1
#define CS_ATTN_NONE      0
/// linear attenuation: light * (radius - distance) / radius
#define CS_ATTN_LINEAR    1
/// inverse attenuation: light * (radius / distance)
#define CS_ATTN_INVERSE   2
/// realistic attenuation: light * (radius^2 / distance^2)
#define CS_ATTN_REALISTIC 3
/** @} */

SCF_VERSION (iLightCallback, 0, 2, 0);

/**
 * Set a callback which is called when this light color is changed.
 * The given context will be either an instance of iRenderView, iFrustumView,
 * or else NULL.
 */
struct iLightCallback : public iBase
{
  /**
   * Light color will be changed. It is safe to delete this callback
   * in this function.
   */
  virtual void OnColorChange (iLight* light, const csColor& newcolor) = 0;

  /**
   * Light position will be changed. It is safe to delete this callback
   * in this function.
   */
  virtual void OnPositionChange (iLight* light, const csVector3& newpos) = 0;

  /**
   * Sector will be changed. It is safe to delete this callback
   * in this function.
   */
  virtual void OnSectorChange (iLight* light, iSector* newsector) = 0;

  /**
   * Radius will be changed.
   * It is safe to delete this callback in this function.
   */
  virtual void OnRadiusChange (iLight* light, float newradius) = 0;

  /**
   * Light will be destroyed.
   * It is safe to delete this callback in this function.
   */
  virtual void OnDestroy (iLight* light) = 0;
};


SCF_VERSION (iLight, 0, 0, 9);

/**
 * The iLight interface is the SCF interface for the csLight class.
 * <p>
 * First some terminology about all the several types of lights
 * that Crystal Space supports:
 * <ul>
 * <li>Static light. This is a normal static light that cannot move
 *     and cannot change intensity/color. All lighting information from
 *     all static lights is collected in one static lightmap.
 * <li>Pseudo-dynamic light. This is a static light that still cannot
 *     move but the intensity/color can change. The shadow information
 *     from every pseudo-dynamic light is kept in a seperate shadow-map.
 *     Shadowing is very accurate with pseudo-dynamic lights since they
 *     use the same algorithm as static lights.
 * <li>Dynamic light. This is a light that can move and change
 *     intensity/color. These lights are the most flexible. All lighting
 *     information from all dynamic lights is collected in one dynamic
 *     lightmap (seperate from the pseudo-dynamic shadow-maps).
 *     Shadows for dynamic lights will be less accurate because things
 *     will not cast accurate shadows (due to computation speed limitations).
 * </ul>
 * Note that static and pseudo-dynamic lights are represented by the
 * same csStatLight class.
 */
struct iLight : public iBase
{
  /// Get private pointer to light object. UGLY
  virtual csLight* GetPrivateObject () = 0;

  /// Get the id of this light. This is a 16-byte MD5.
  virtual const char* GetLightID () = 0;

  /// Get the iObject for this light.
  virtual iObject *QueryObject() = 0;

  /// Get the position of this light.
  virtual const csVector3& GetCenter () = 0;
  /// Set the position of this light.
  virtual void SetCenter (const csVector3& pos) = 0;

  /// Get the sector for this light.
  virtual iSector *GetSector () = 0;
  /// Set the sector for this light.
  virtual void SetSector (iSector* sector) = 0;

  /// Get the radius
  virtual float GetRadius () = 0;
  /// Get the squared radius.
  virtual float GetSquaredRadius () = 0;
  /// Get the inverse radius.
  virtual float GetInverseRadius () = 0;
  /// Set the radius
  virtual void SetRadius (float r) = 0;

  /// Get the color of this light.
  virtual const csColor& GetColor () = 0;
  /// Set the color of this light.
  virtual void SetColor (const csColor& col) = 0;
  
  /// Return true if this light is pseudo-dynamic.
  virtual bool IsDynamic () const = 0;

  /// Return current attenuation mode.
  virtual int GetAttenuation () = 0;
  /**
   * Set attenuation mode. The following values are possible 
   * (default is #CS_ATTN_LINEAR):
   * <ul>
   * <li>#CS_ATTN_NONE: light * 1
   * <li>#CS_ATTN_LINEAR: light * (radius - distance) / radius
   * <li>#CS_ATTN_INVERSE: light * (radius / distance)
   * <li>#CS_ATTN_REALISTIC: light * (radius^2 / distance^2)
   * </ul>
   */
  virtual void SetAttenuation (int a) = 0;

  /// Create a cross halo for this light.
  virtual iCrossHalo* CreateCrossHalo (float intensity, float cross) = 0;
  /// Create a nova halo for this light.
  virtual iNovaHalo* CreateNovaHalo (int seed, int num_spokes,
  	float roundness) = 0;
  /// Create a flare halo for this light.
  virtual iFlareHalo* CreateFlareHalo () = 0;

  /// Get the brightness of a light at a given distance.
  virtual float GetBrightnessAtDistance (float d) = 0;

  /**
   * Get flags for this light.
   * Supported flags:
   * <ul>
   * <li>#CS_LIGHT_ACTIVEHALO
   * <li>#CS_LIGHT_THINGSHADOWS
   * </ul>
   */
  virtual csFlags& GetFlags () = 0;

  /**
   * Set the light callback. This will call IncRef() on the callback
   * So make sure you call DecRef() to release your own reference.
   */
  virtual void SetLightCallback (iLightCallback* cb) = 0;

  /**
   * Remove a light callback.
   */
  virtual void RemoveLightCallback (iLightCallback* cb) = 0;

  /// Get the number of light callbacks.
  virtual int GetLightCallbackCount () const = 0;
  
  /// Get the specified light callback.
  virtual iLightCallback* GetLightCallback (int idx) const = 0;

  /**
   * Return a number that changes when the light changes (color,
   * or position).
   */
  virtual uint32 GetLightNumber () const = 0;
};

SCF_VERSION (iLightList, 0, 0, 2);

/**
 * This structure represents a list of lights.
 */
struct iLightList : public iBase
{
  /// Return the number of lights in this list.
  virtual int GetCount () const = 0;

  /// Return a light by index.
  virtual iLight *Get (int n) const = 0;

  /// Add a light.
  virtual int Add (iLight *obj) = 0;

  /// Remove a light.
  virtual bool Remove (iLight *obj) = 0;

  /// Remove the nth light.
  virtual bool Remove (int n) = 0;

  /// Remove all lights.
  virtual void RemoveAll () = 0;

  /// Find a light and return its index.
  virtual int Find (iLight *obj) const = 0;

  /// Find a light by name.
  virtual iLight *FindByName (const char *Name) const = 0;

  /// Find a light by its ID value (16-byte MD5).
  virtual iLight *FindByID (const char* id) const = 0;
};

SCF_VERSION (iLightingProcessData, 0, 0, 1);

/**
 * The iLightingProcessData interface can be implemented by a mesh
 * object so that it can attach additional information for the lighting
 * process.
 */
struct iLightingProcessData : public iBase
{
  /**
   * Finalize lighting. This function is called by the lighting
   * routines after performing CheckFrustum().
   */
  virtual void FinalizeLighting () = 0;
};

SCF_VERSION (iLightingProcessInfo, 0, 0, 2);

/**
 * The iLightingProcessInfo interface holds information for the lighting
 * system. You can query the userdata from iFrustumView for this interface
 * while in a 'portal' callback. This way you can get specific information
 * from the lighting system for your null-portal.
 */
struct iLightingProcessInfo : public iFrustumViewUserdata
{
  /// Get the light.
  virtual iLight* GetLight () const = 0;

  /// Return true if dynamic.
  virtual bool IsDynamic () const = 0;

  /// Set the current color.
  virtual void SetColor (const csColor& col) = 0;

  /// Get the current color.
  virtual const csColor& GetColor () const = 0;

  /**
   * Attach some userdata to the process info. You can later query
   * for this by doing QueryUserdata() with the correct SCF version
   * number.
   */
  virtual void AttachUserdata (iLightingProcessData* userdata) = 0;

  /**
   * Query for userdata based on SCF type.
   */
  virtual csPtr<iLightingProcessData> QueryUserdata (scfInterfaceID id,
  	int version) = 0;

  /**
   * Finalize lighting. This function is called by the lighting
   * routines after performing CheckFrustum(). It will call
   * FinalizeLighting() on all user datas.
   */
  virtual void FinalizeLighting () = 0;
};

SCF_VERSION (iLightIterator, 0, 0, 1);

/**
 * Iterator to iterate over all static lights in the engine.
 * This iterator assumes there are no fundamental changes
 * in the engine while it is being used.
 * If changes to the engine happen the results are unpredictable.
 */
struct iLightIterator : public iBase
{
  /// Restart iterator.
  virtual void Restart () = 0;

  /// Get light from iterator. Return NULL at end.
  virtual iLight* Fetch () = 0;

  /// Get the sector for the last fetched light.
  virtual iSector* GetLastSector () = 0;
};

/** @} */

#endif // __CS_IENGINE_LIGHT_H__

