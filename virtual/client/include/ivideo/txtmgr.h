/*
    Copyright (C) 1998 by Jorrit Tyberghein
    Written by Jorrit Tyberghein.

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

#ifndef __CS_IVIDEO_TXTMGR_H__
#define __CS_IVIDEO_TXTMGR_H__

/**\file
 * Texture manager interface
 */
 
/**
 * \addtogroup gfx3d
 * @{ */

#include "csutil/scf.h"

class Vector2;
class csMatrix3;
class csVector3;
class csRect;

struct iImage;
struct iTextureHandle;
struct iMaterial;
struct iMaterialHandle;

/**\name Texture registration flags.
 * During texture registration you should tell
 * the manager which way you're going to use the texture: whenever you're
 * going to use it for 2D (DrawPixmap ()), for 3D (DrawPolygon ()), whenever
 * the texture will be dynamically modified.
 * @{ */
/// You're going to use the texture for 2D drawing
#define CS_TEXTURE_2D			0x00000001
/// You're going to use the texture for 3D drawing
#define CS_TEXTURE_3D			0x00000002
/**
 * Dither texture or not.<p>
 * Some renderers may use dithering while converting textures to internal
 * format (say from truecolor to 8-bit paletted). For most textures dithering
 * won't give any visual effect, but very seldom there are textures that looks
 * relatively bad after being converted. In this case you can enable this
 * per-texture flag.
 */
#define CS_TEXTURE_DITHER		0x00000004
/**
 * Create mipmaps for this texture?<p>
 * Sometimes we know in advance that some texture will need just one
 * mipmap (or we just don't care about the mipmapping artifacts because of,
 * say, how texture is looking (smoothed etc)). This flag is a <b>hint</b>
 * for texture manager so that it will know this.<p>
 * Note that if texture is not registered for 3D usage (i.e. if CS_TEXTURE_3D
 * is not set) this flag does not matter - 2D textures do not use mipmaps.
 */
#define CS_TEXTURE_NOMIPMAPS		0x00000008
/** @} */

SCF_VERSION (iTextureManager, 2, 1, 0);

/**
 * This is the standard texture manager interface.
 * A 3D rasterizer will have to implement a subclass of this one and
 * return a pointer to it in Graphics3D.
 * This class is responsible for receiving all textures
 * from the 3D engine, converting them to an internal format if
 * needed, calculating a palette if needed, and calculating all
 * lookup tables related to the textures. Mipmap creation is
 * also done in this class.
 */
struct iTextureManager : public iBase
{
  /**
   * Register a texture. The given input image is IncRef'd and DecRef'ed
   * later when FreeImages () is called. If you want to keep the input image
   * make sure you have called IncRef yourselves.
   *<p>
   * The texture is not converted immediately. Instead, you can make
   * intermediate calls to iTextureHandle::SetKeyColor (). Finally,
   * if you want to merge the texture into the current environment, you
   * should call txt->Prepare(). Alternatively you can call the
   * PrepareTextures () method to compute a optimal palette and convert
   * all the textures into the internal format.
   *<p>
   * This function returns a handle which should be given
   * to the 3D rasterizer or 2D driver when drawing or otherwise using
   * the texture.
   *<p>
   * The `flags' contains one or several of CS_TEXTURE_XXX flags OR'ed
   * together. They define the mode texture is going to be used in.
   *<p>
   * The texture manager will reject the texture if it is an inappropiate
   * format (see GetTextureFormat () method).
   *<p>
   * The texture is unregistered at destruction, i.e. as soon as the last
   * reference to the texture handle is released.
   */
  virtual csPtr<iTextureHandle> RegisterTexture (iImage *image, int flags) = 0;

  /**
   * After all textures have been added, this function does all
   * needed calculations (palette, lookup tables, mipmaps, ...).
   * PrepareTextures () must be able to handle being called twice
   * or more without ill effects.
   */
  virtual void PrepareTextures () = 0;

  /**
   * Call this function if you want to release all iImage's as
   * given to this texture manager. After FreeImages() has been called
   * it is no longer allowed to call Prepare() again. So the advantage
   * of calling FreeImages() is that you gain memory (may be a lot)
   * but the disadvantage is that when you want to add textures later
   * you have to reload them all and start all over.
   */
  virtual void FreeImages () = 0;

  /**
   * Register a material. The input material is IncRef'd and DecRef'ed
   * later when FreeMaterials () is called or the material handle is destroyed
   * by calling DecRef on it enough times. If you want to keep the input
   * material make sure you have called IncRef yourselves. <p>
   *
   * The material is unregistered at destruction, i.e. as soon as the last
   * reference to the material handle is released.
   */
  virtual csPtr<iMaterialHandle> RegisterMaterial (iMaterial* material) = 0;

  /**
   * Register a material based on a texture handle. This is a short-cut
   * to quickly make materials based on a single texture.
   *
   * The material is unregistered at destruction, i.e. as soon as the last
   * reference to the material handle is released.
   */
  virtual csPtr<iMaterialHandle> RegisterMaterial (
  	iTextureHandle* txthandle) = 0;

  /**
   * Prepare all materials.
   */
  virtual void PrepareMaterials () = 0;

  /**
   * Call this function if you want to release all iMaterial's as
   * given to this texture manager.
   */
  virtual void FreeMaterials () = 0;

  /**
   * Set verbose mode on/off. In verbose mode, texture manager will
   * Printf() through the system driver during all initialization and
   * preparation operations.
   */
  virtual void SetVerbose (bool vb) = 0;

  /**
   * Query the basic format of textures that can be registered with this
   * texture manager. It is very likely that the texture manager will
   * reject the texture if it is in an improper format. The alpha channel
   * is optional; the texture can have it and can not have it. Only the
   * bits that fit the CS_IMGFMT_MASK mask matters.
   */
  virtual int GetTextureFormat () = 0;
};

/** @} */

#endif // __CS_IVIDEO_TXTMGR_H__
