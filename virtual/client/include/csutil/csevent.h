/*
    Crystal Space 3D engine: Event class interface
    Written by Andrew Zabolotny <bit@eltech.ru>

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

#ifndef __CSEVENT_H__
#define __CSEVENT_H__

#include "iutil/event.h"

/**
 * This class represents a system event.<p>
 * Events can be generated by hardware (keyboard, mouse)
 * as well as by software. There are so much constructors of
 * this class as much different types of events exists.
 */
class csEvent : public iEvent
{
public:
  /// Empty initializer
  csEvent ();

  /**
   * Cloning constructor.  Note that for command style events, this performs
   * only a shallow copy of the `Info' attribute.
   */
  csEvent (csEvent const&);

  /// Create a keyboard event object
  csEvent (csTicks, int type, int kcode, int kchar, int modifiers);

  /// Create a mouse event object
  csEvent (csTicks, int type, int x, int y, int button, int modifiers);

  /// Create a joystick event object
  csEvent (csTicks, int type, int n, int x, int y, int button, int modifiers);

  /// Create a command event object
  csEvent (csTicks, int type, int code, void* info = NULL);

  /// Destructor
  virtual ~csEvent ();

  SCF_DECLARE_IBASE;
};

#endif // __CSEVENT_H__
