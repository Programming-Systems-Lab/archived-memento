/*
    Copyright (C) 1998-2001 by Jorrit Tyberghein

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

#include "cssysdef.h"
#include "ChimeSystemDriver.h"
#include "infmaze.h"
#include "qint.h"
#include "qsqrt.h"
#include "csgeom/frustum.h"
#include "ivaria/view.h"
#include "imesh/thing/polygon.h"
#include "imesh/terrfunc.h"
#include "csgeom/csrect.h"
#include "csutil/dataobj.h"
#include "cstool/keyval.h"
#include "cstool/collider.h"
#include "cstool/cspixmap.h"
#include "ivaria/collider.h"
#include "iengine/movable.h"
#include "iutil/objreg.h"
#include "iutil/plugin.h"

extern ChimeSystemDriver *Driver;

int FindIntersection(csCollisionPair& cd,csVector3 line[2])
{
  csVector3 tri1[3]; tri1[0]=cd.a1; tri1[1]=cd.b1; tri1[2]=cd.c1;
  csVector3 tri2[3]; tri2[0]=cd.a2; tri2[1]=cd.b2; tri2[2]=cd.c2;

  return csMath3::FindIntersection(tri1,tri2,line);
}

// Define the player bounding box.
// The camera's lens or person's eye is assumed to be
// at 0,0,0.  The height (DY), width (DX) and depth (DZ).
// Is the size of the camera/person and the origin
// coordinates (OX,OY,OZ) locate the bbox with respect to the eye.
// This player is 1.8 metres tall (assuming 1cs unit = 1m) (6feet)
#define DX    0.5
#define DY    0.5
#define DZ    0.5
#define OY    0.5

#define DX_L  0.5
#define DZ_L  0.5

#define DX_2  (DX/2)
#define DZ_2  (DZ/2)

#define DX_2L (DX_L/2)
#define DZ_2L (DZ_L/2)

#define OYL  0.5
#define DYL  (OY-OYL)

void ChimeUser::CreateColliders ()
{
  iPolygon3D *p;
  csRef<iPolygonMesh> mesh;
  csRef<iPluginManager> plugin_mgr (
  	CS_QUERY_REGISTRY (object_reg, iPluginManager));
  csRef<iMeshObjectType> ThingType (CS_QUERY_PLUGIN_CLASS (plugin_mgr,
  	"crystalspace.mesh.object.thing", iMeshObjectType));
  if (!ThingType)
    ThingType = CS_LOAD_PLUGIN (plugin_mgr,
    	"crystalspace.mesh.object.thing", iMeshObjectType);

  csRef<iMeshObjectFactory> thing_fact (ThingType->NewFactory ());
  csRef<iMeshObject> mesh_obj (SCF_QUERY_INTERFACE (thing_fact, iMeshObject));
  csRef<iMeshFactoryWrapper> thingFact = driver->csEngine->CreateMeshFactory("crystalspace.mesh.object.thing", "thingFact");
  plbody = driver->csEngine->CreateMeshWrapper(thingFact, "CameraColliderBox");
  plbody->DeferUpdateLighting (CS_NLIGHT_STATIC|CS_NLIGHT_DYNAMIC, 10);

  thing_state->CreateVertex (csVector3 (-DX_2, OY,    -DZ_2));
  thing_state->CreateVertex (csVector3 (-DX_2, OY,    DZ_2));
  thing_state->CreateVertex (csVector3 (-DX_2, OY+DY, DZ_2));
  thing_state->CreateVertex (csVector3 (-DX_2, OY+DY, -DZ_2));
  thing_state->CreateVertex (csVector3 (DX_2,  OY,    -DZ_2));
  thing_state->CreateVertex (csVector3 (DX_2,  OY,    DZ_2));
  thing_state->CreateVertex (csVector3 (DX_2,  OY+DY, DZ_2));
  thing_state->CreateVertex (csVector3 (DX_2,  OY+DY, -DZ_2));

  // Left
  p = thing_state->CreatePolygon ();
  p->CreateVertex (0); p->CreateVertex (1);
  p->CreateVertex (2); p->CreateVertex (3);

  // Right
  p = thing_state->CreatePolygon ();
  p->CreateVertex (4); p->CreateVertex (5);
  p->CreateVertex (6); p->CreateVertex (7);

  // Bottom
  p = thing_state->CreatePolygon ();
  p->CreateVertex (0); p->CreateVertex (1);
  p->CreateVertex (5); p->CreateVertex (4);

  // Top
  p = thing_state->CreatePolygon ();
  p->CreateVertex (3); p->CreateVertex (2);
  p->CreateVertex (6); p->CreateVertex (7);

  // Front
  p = thing_state->CreatePolygon ();
  p->CreateVertex (1); p->CreateVertex (5);
  p->CreateVertex (6); p->CreateVertex (2);

  // Back
  p = thing_state->CreatePolygon ();
  p->CreateVertex (0); p->CreateVertex (4);
  p->CreateVertex (7); p->CreateVertex (3);

  mesh = SCF_QUERY_INTERFACE (mesh_obj, iPolygonMesh);
  body = new csColliderWrapper (plbody->QueryObject (), collide_system, mesh);
  body->SetName ("player body");
  plbody->GetRadius (body_radius, body_center);

  thing_fact = ThingType->NewFactory ();
  mesh_obj = SCF_QUERY_INTERFACE (thing_fact, iMeshObject);
  pllegs = driver->csEngine->CreateMeshWrapper(thingFact, "CameraColliderLegs");
  pllegs->DeferUpdateLighting (CS_NLIGHT_STATIC|CS_NLIGHT_DYNAMIC, 10);
  thing_state = SCF_QUERY_INTERFACE (mesh_obj, iThingState);

  thing_state->CreateVertex (csVector3 (-DX_2L, OYL,     -DZ_2L));
  thing_state->CreateVertex (csVector3 (-DX_2L, OYL,     DZ_2L));
  thing_state->CreateVertex (csVector3 (-DX_2L, OYL+DYL, DZ_2L));
  thing_state->CreateVertex (csVector3 (-DX_2L, OYL+DYL, -DZ_2L));
  thing_state->CreateVertex (csVector3 (DX_2L,  OYL,     -DZ_2L));
  thing_state->CreateVertex (csVector3 (DX_2L,  OYL,     DZ_2L));
  thing_state->CreateVertex (csVector3 (DX_2L,  OYL+DYL, DZ_2L));
  thing_state->CreateVertex (csVector3 (DX_2L,  OYL+DYL, -DZ_2L));

  // Left
  p = thing_state->CreatePolygon ();
  p->CreateVertex (0); p->CreateVertex (1);
  p->CreateVertex (2); p->CreateVertex (3);

  // Right
  p = thing_state->CreatePolygon ();
  p->CreateVertex (4); p->CreateVertex (5);
  p->CreateVertex (6); p->CreateVertex (7);

  // Bottom
  p = thing_state->CreatePolygon ();
  p->CreateVertex (0); p->CreateVertex (1);
  p->CreateVertex (5); p->CreateVertex (4);

  // Top
  p = thing_state->CreatePolygon ();
  p->CreateVertex (3); p->CreateVertex (2);
  p->CreateVertex (6); p->CreateVertex (7);

  // Front
  p = thing_state->CreatePolygon ();
  p->CreateVertex (1); p->CreateVertex (5);
  p->CreateVertex (6); p->CreateVertex (2);

  // Back
  p = thing_state->CreatePolygon ();
  p->CreateVertex (0); p->CreateVertex (4);
  p->CreateVertex (7); p->CreateVertex (3);

  mesh = SCF_QUERY_INTERFACE (mesh_obj, iPolygonMesh);
  legs = new csColliderWrapper (pllegs->QueryObject (), collide_system, mesh);
  legs->SetName ("player legs");
  pllegs->GetRadius ( legs_radius, legs_center);

  SCF_DEC_REF (legs);
  SCF_DEC_REF (body);

  if (!body || !legs)
    do_cd = false;
}

#define MAXSECTORSOCCUPIED  20

// No more than 1000 collisions ;)
csCollisionPair our_cd_contact[1000];
int num_our_cd;

int FindSectors (csVector3 v, csVector3 d, iSector *s, iSector **sa)
{
  int c = 0;
  // @@@ Avoid this sqrt somehow? i.e. by having it in the objects.
  float size = qsqrt (d.x * d.x + d.y * d.y + d.z * d.z);
  csRef<iSectorIterator> it (Driver->csEngine->GetNearbySectors (s, v, size));
  iSector* sector;
  while ((sector = it->Fetch ()) != NULL)
  {
    sa[c++] = sector;
    if (c >= MAXSECTORSOCCUPIED) break;
  }
  return c;
}

int CollisionDetect (iEngine* csEngine, csColliderWrapper *c, iSector* sp,
	csReversibleTransform *cdt)
{
  int hit = 0;
  int j;

  // Check collision with this sector.
  csCollisionPair* CD_contact;

  csRef<iObjectIterator> objit (csEngine->GetNearbyObjects (sp,
	cdt->GetOrigin (), 3));		// 3 should be enough for moving around.
  while (!objit->IsFinished ())
  {
    iObject* mw_obj = objit->GetObject ();
    csRef<iMeshWrapper> mw (SCF_QUERY_INTERFACE (mw_obj, iMeshWrapper));
    if (mw)
    {
      Driver->collide_system->ResetCollisionPairs ();
      if (c->Collide (mw_obj, cdt,
    	  &mw->GetMovable ()->GetTransform ())) hit++;

      CD_contact = Driver->collide_system->GetCollisionPairs ();
      for (j=0 ; j<Driver->collide_system->GetCollisionPairCount () ; j++)
        our_cd_contact[num_our_cd++] = CD_contact[j];

      if (Driver->collide_system->GetOneHitOnly () && hit)
        return 1;
      // TODO, should test which one is the closest.
    }
    objit->Next ();
  }

  return hit;
}

void DoGravity (iEngine* csEngine, csVector3& pos, csVector3& vel)
{
  pos=Driver->view->GetCamera ()->GetTransform ().GetOrigin ();

  csVector3 new_pos = pos+vel;
  csMatrix3 m;
  csOrthoTransform test (m, new_pos);

  iSector *n[MAXSECTORSOCCUPIED];
  int num_sectors = FindSectors (new_pos, 4.0f*Driver->body_radius,
    Driver->view->GetCamera()->GetSector(), n);

  num_our_cd = 0;
  Driver->collide_system->SetOneHitOnly (false);
  int hits = 0;

  // Check to see if there are any terrains, if so test against those.
  // This routine will automatically adjust the transform to the highest
  // terrain at this point.

  // @@@@@@ This is not efficient if there are lots of objects in the level!!!
  int k;
  for ( k = 0; k < num_sectors ; k++)
  {
    iMeshList* ml = n[k]->GetMeshes ();
    if (ml->GetCount () > 0)
    {
      int i;
      for (i = 0 ; i < ml->GetCount () ; i++)
      {
	iMeshWrapper* terrain = ml->Get (i);
	csRef<iTerrFuncState> state (SCF_QUERY_INTERFACE (terrain
		->GetMeshObject (), iTerrFuncState));
	if (state)
	  hits += state->CollisionDetect( &test );
      }
    }
  }

  // If there were hits with the terrain we update our new position
  // here. Side note: this could moved outside the loop above because
  // a compiler bug with gcc 2.7.2 prevented it from working when inside
  // the loop.
  if (hits) new_pos = test.GetOrigin ();

  int j;

  if (hits == 0)
  {
    Driver->collide_system->ResetCollisionPairs ();

    for ( ; num_sectors-- ; )
      hits += CollisionDetect (csEngine, Driver->body, n[num_sectors], &test);

#if 0
    if (num_our_cd > 0)
	printf ("body: hits=%d num_our_cd=%d\n", hits, num_our_cd);
#endif
    for (j=0 ; j<num_our_cd; j++)
    {
      csCollisionPair& cd = our_cd_contact[j];
      csVector3 n = ((cd.c2-cd.b2)%(cd.b2-cd.a2)).Unit();
      if (n*vel<0)
        continue;
      vel = -(vel%n)%n;
    }

    // We now know our (possible) velocity. Let's try to move up or down, if possible
    new_pos = pos+vel;
    
    // Try again, and don't move if we're still in a wall
    int num_sectors = FindSectors (new_pos, 4.0f*Driver->body_radius,
	    Driver->view->GetCamera()->GetSector(), n);
    Driver->collide_system->SetOneHitOnly (false);
    Driver->collide_system->ResetCollisionPairs ();
    test = csOrthoTransform (csMatrix3(), new_pos);
    int hit = 0;
    for (; num_sectors--;)
      if (CollisionDetect (csEngine, Driver->body, n[num_sectors], &test) > 0)
      {
	new_pos-=vel;
	break;
      }
    
    test = csOrthoTransform (csMatrix3(), new_pos);

    num_sectors = FindSectors (new_pos, 4.0f*Driver->legs_radius,
		Driver->view->GetCamera()->GetSector(), n);

    num_our_cd = 0;
    Driver->collide_system->SetOneHitOnly (false);
    Driver->collide_system->ResetCollisionPairs ();

    for ( ; num_sectors-- ; )
      hit += CollisionDetect (csEngine, Driver->legs, n[num_sectors], &test);

    if (!hit)
    {
      Driver->on_ground = false;
      if (Driver->do_gravity && !Driver->move_3d)
	vel.y -= 0.002;
    }
    else
    {
      float max_y=-1e10;

      for (j=0 ; j<num_our_cd ; j++)
      {
	csCollisionPair cd = our_cd_contact[j];
        csVector3 n = ((cd.c2-cd.b2)%(cd.b2-cd.a2)).Unit();

	if (n*csVector3(0,-1,0)<0.7) continue;

	csVector3 line[2];

	cd.a1 += new_pos;
	cd.b1 += new_pos;
	cd.c1 += new_pos;

	if (FindIntersection (cd,line))
	{
	  if (line[0].y>max_y)
	    max_y=line[0].y;
	  if (line[1].y>max_y)
	    max_y=line[1].y;
	}
      }

      float p = new_pos.y-max_y+OYL+0.01;
      if (ABS(p)<DYL-0.01)
      {
	if (max_y != -1e10)
	  new_pos.y = max_y-OYL-0.01;

	if (vel.y<0)
	  vel.y = 0;
      }
      Driver->on_ground = true;
    }
  }
  new_pos -= Driver->view->GetCamera ()->GetTransform ().GetOrigin ();
  Driver->view->GetCamera ()->MoveWorld (new_pos);
  Driver->velocity = Driver->view->GetCamera ()->GetTransform ().GetO2T ()*vel;

  if(!Driver->do_gravity)
    Driver->velocity.y -= SIGN (Driver->velocity.y) * MIN (0.017, ABS (Driver->velocity.y));

}

