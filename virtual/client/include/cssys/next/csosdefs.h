#ifndef __NeXT_csosdefs_h
#define __NeXT_csosdefs_h
//=============================================================================
//
//	Copyright (C)1999-2002 by Eric Sunshine <sunshine@sunshineco.com>
//
// The contents of this file are copyrighted by Eric Sunshine.  This work is
// distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
// PARTICULAR PURPOSE.  You may distribute this file provided that this
// copyright notice is retained.  Send comments to <sunshine@sunshineco.com>.
//
//=============================================================================
//-----------------------------------------------------------------------------
// csosdefs.h
//
//	Platform-specific interface to common functionality.  Compatible
//	with MacOS/X, MacOS/X Server 1.0 (Rhapsody), OpenStep, and NextStep.
//
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Define the appropriate PROC_ flag for the current architecture for MacOS/X
// Server, OpenStep, and NextStep multi-architecture binary (MAB) compilations.
// It is necessary to perform this step here since this information is not
// known at makefile configuration time or even at the time when the compiler
// is invoked on account of the ability to build multi-architecture binaries
// with a single invocation of the compiler.  Therefore, this is the first
// chance we have of actually determining the proper PROC_ flag.  Also set
// CS_PROCESSOR_NAME to an appropriate value.
//-----------------------------------------------------------------------------
#if defined(__m68k__)
#  if !defined(PROC_M68K)
#    define PROC_M68K
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "M68K"
#  endif
#elif defined(__i386__)
#  if !defined(PROC_X86)
#    define PROC_X86
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "X86"
#  endif
#elif defined(__sparc__)
#  if !defined(PROC_SPARC)
#    define PROC_SPARC
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "Sparc"
#  endif
#elif defined(__hppa__)
#  if !defined(PROC_HPPA)
#    define PROC_HPPA
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "PA-RISC"
#  endif
#elif defined(__ppc__)
#  if !defined(PROC_POWERPC)
#    define PROC_POWERPC
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "PowerPC"
#  endif
#elif defined(__s390__)
#  if !defined(PROC_S390)
#    define PROC_S390
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "s390"
#  endif
#else
#  if !defined(PROC_UNKNOWN)
#    define PROC_UNKNOWN
#    undef  CS_PROCESSOR_NAME
#    define CS_PROCESSOR_NAME "Unknown"
#  endif
#endif


//-----------------------------------------------------------------------------
// The 2D graphics driver used by the software renderer on this platform.
//-----------------------------------------------------------------------------
#undef  CS_SOFTWARE_2D_DRIVER
#if defined(OS_NEXT_MACOSXS)
#define CS_SOFTWARE_2D_DRIVER "crystalspace.graphics2d.coregraphics"
#else
#define CS_SOFTWARE_2D_DRIVER "crystalspace.graphics2d.next"
#endif

#undef  CS_OPENGL_2D_DRIVER
#define CS_OPENGL_2D_DRIVER "crystalspace.graphics2d.glosx"

#undef  CS_SOUND_DRIVER
#define CS_SOUND_DRIVER "crystalspace.sound.driver.coreaudio"


//-----------------------------------------------------------------------------
// NeXT does not supply strdup() so fake one up.
//-----------------------------------------------------------------------------
#include <stdlib.h>
#include <string.h>

#if defined(OS_NEXT_NEXTSTEP) || defined(OS_NEXT_OPENSTEP)

static inline char* strdup(char const* s)
{
  if (s == 0) s = "";
  char* p = (char*)malloc(strlen(s) + 1);
  strcpy(p, s);
  return p;
}

#endif


//-----------------------------------------------------------------------------
// Provide CS_MAXPATHLEN and PATH_SEPARATOR with proper values.
//-----------------------------------------------------------------------------
#include <sys/param.h>
#define CS_MAXPATHLEN MAXPATHLEN
#define PATH_SEPARATOR '/'


//-----------------------------------------------------------------------------
// Pull in definitions for getwd(), ntohl(), htonl(), select(), etc.
// *NOTE* On MacOS/X Server 1.0, libc.h pulls in sys/mount.h which pulls in
// net/radix.h which defines a macro named Free().  This macro interferes with
// several Crystal Space classes which have methods named Free(), so we must
// #undef it.
//-----------------------------------------------------------------------------
#if defined(CS_SYSDEF_PROVIDE_GETCWD)  || \
    defined(CS_SYSDEF_PROVIDE_SOCKETS) || \
    defined(CS_SYSDEF_PROVIDE_SELECT)  || \
    defined(CS_SYSDEF_PROVIDE_ACCESS)
#include <libc.h>
#undef Free
#endif

#if defined(CS_SYSDEF_PROVIDE_SOCKETS)
#define CS_USE_FAKE_SOCKLEN_TYPE
#endif

#if defined(CS_SYSDEF_PROVIDE_SELECT)
#include <string.h> // For memset()
#define bzero(b,len) memset(b,0,len) /* bzero used by FD_ZERO */
#undef CS_SYSDEF_PROVIDE_SELECT
#endif


//-----------------------------------------------------------------------------
// NeXT does not supply getcwd() so fake one up using getwd().
//-----------------------------------------------------------------------------
#if defined(OS_NEXT_NEXTSTEP) || \
    defined(OS_NEXT_OPENSTEP) || \
    defined(OS_NEXT_MACOSXS)

#if defined(CS_SYSDEF_PROVIDE_GETCWD)
#undef CS_SYSDEF_PROVIDE_GETCWD

#include <sys/param.h>

static inline char* getcwd(char* p, size_t size)
{
  char s[CS_MAXPATHLEN];
  char* r = getwd(s);
  if (r != 0)
  {
    strncpy(p, r, size - 1);
    p[size - 1] = '\0';
    r = p;
  }
  return r;
}

#endif // CS_SYSDEF_PROVIDE_GETCWD
#endif // OS_NEXT_NEXTSTEP || OS_NEXT_OPENSTEP || OS_NEXT_MACOSXS


//-----------------------------------------------------------------------------
// NeXT does not properly support Posix 'dirent', so fake it with 'direct'.
//-----------------------------------------------------------------------------
#ifdef CS_SYSDEF_PROVIDE_DIR

#ifdef _POSIX_SOURCE
#  undef _POSIX_SOURCE
#  include <sys/dir.h>
#  define _POSIX_SOURCE
#else
#  include <sys/dir.h>
#endif
#include <sys/dirent.h>	// Just so it gets included *before* #define below.
#define dirent direct

#define __NEED_GENERIC_ISDIR
#endif // CS_SYSDEF_PROVIDE_DIR


//-----------------------------------------------------------------------------
// Endian support.
//-----------------------------------------------------------------------------
#if defined (__LITTLE_ENDIAN__)
#  define CS_LITTLE_ENDIAN
#elif defined (__BIG_ENDIAN__)
#  define CS_BIG_ENDIAN
#else
#  error "Please define a suitable CS_XXX_ENDIAN macro in next/csosdefs.h!"
#endif


//-----------------------------------------------------------------------------
// NextStep's gcc infrequently throws an exception when confronted with an
// expression such as `static const Foo[] = {...};'.  There are two ways to
// work around this problem.  (1) Remove the `const' or (2) specify the exact
// table size, as in `Foo[3]'.  This patch employs work-around #1.
//-----------------------------------------------------------------------------
#undef CS_STATIC_TABLE
#define CS_STATIC_TABLE static


//-----------------------------------------------------------------------------
// Although the IEEE double-format optimizations of QInt() and QRound() work
// on M68K, there are cases (particularly in the software renderer) where the
// compiler corrupts the emitted code for these functions.  Therefore, disable
// these optimizations.
// Note by Matt Reda: I did some rough testing of QInt() and friends on the
// PowerPC.  It appears to work ok, but is actually slower.  Some simple
// tests show that QInt() is roughly twice as slow as a cast from double
// to long
//-----------------------------------------------------------------------------
#if !defined(PROC_X86)
#  define CS_NO_IEEE_OPTIMIZATIONS
#endif


//-----------------------------------------------------------------------------
// The special Intel assembly version of qsqrt() (from CS/include/qsqrt.h)
// fails to compile on NeXT.  However, Matthew Reda <mreda@mac.com> added a
// PowerPC version which works well for Macintosh and MacOS/X using GCC.
//-----------------------------------------------------------------------------
#if !defined(PROC_POWERPC)
#  define CS_NO_QSQRT
#endif


//-----------------------------------------------------------------------------
// MacOS/X mmap() functionality for memory-mapped I/O.
//-----------------------------------------------------------------------------
#if defined(OS_NEXT_MACOSX) && defined(CS_SYSDEF_PROVIDE_HARDWARE_MMIO)

#define CS_HAS_MEMORY_MAPPED_IO 1

#include <unistd.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

// Unix specific memory mapped I/O platform dependent stuff
struct mmioInfo
{          
  int file;               // Handle to the mapped file.
  unsigned int file_size; // File size.
  unsigned char* data;    // Base pointer to the data.
};

// Fill in the mmioInfo struct by mapping in filename.
// Returns true on success, false otherwise.
inline bool MemoryMapFile(mmioInfo* info, char const* filename)
{   
  bool ok = false;
  struct stat st;
  int const fd = open(filename, O_RDONLY);
  if (fd != -1 && fstat(fd, &st) != -1)
  {
    unsigned char* p=(unsigned char*)mmap(0, st.st_size, PROT_READ, 0, fd, 0);
    if ((int)p != -1)
    {
      info->file = fd;
      info->data = p;
      info->file_size = st.st_size;
      ok = true;
    }
  }
  if (!ok && fd != -1)
    close(fd);
  return ok;
}

inline void UnMemoryMapFile(mmioInfo* info)
{
  if (info->data != 0)
    munmap(info->data, info->file_size);
  if (info->file != -1)
    close(info->file);
}

#endif // OS_NEXT_MACOSX && CS_SYSDEF_PROVIDE_HARDWARE_MMIO

#endif // __NeXT_csosdefs_h
