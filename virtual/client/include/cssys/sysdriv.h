/*
    Copyright (C) 1998 by Jorrit Tyberghein

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

#ifndef __CS_SYSDRIV_H__
#define __CS_SYSDRIV_H__

// Include the platform-specific driver classes.

#if defined(OS_MACOSX)		/* Must appear before OS_UNIX */
#  include "cssys/macosx/OSXSystemDriver.h"
#elif defined(OS_UNIX)
#  include "cssys/unix/unix.h"
#elif defined(OS_WIN32)
#  include "cssys/win32/win32.h"
#else
#  error Unable to locate platform-specific driver interface.
#endif

#endif // __CS_SYSDRIV_H__
