/*
    Copyright (C) 1998-2001 by Jorrit Tyberghein
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

#ifdef __CS_CSSYSDEFS_H__
#error Do not include cssysdef.h from header files please!
#else
#define __CS_CSSYSDEFS_H__

#define CSDEF_FRIEND
#include "csdef.h"
#undef CSDEF_FRIEND

/** \file

    This include file should be included from every source file.
    Just before #include directive it can contain several #define's
    that specify what the source file requires.

    The following variables can be defined:

    #define CS_SYSDEF_PROVIDE_MKDIR
      Include definition for MKDIR()

    #define CS_SYSDEF_PROVIDE_GETCWD
      Include definition for getcwd()

    #define CS_SYSDEF_PROVIDE_TEMP
      Include definitions for TEMP_DIR and TEMP_FILE.

    #define CS_SYSDEF_PROVIDE_DIR
      Include definitions required for opendir(), readdir(), closedir()
      and isdir().
 
    #define CS_SYSDEF_PROVIDE_UNLINK
      Include definitions required for unlink()

    #define CS_SYSDEF_PROVIDE_ACCESS
      Include definitions required for access()

    #define CS_SYSDEF_PROVIDE_GETOPT
      For getopt() and GNU getopt_long()

    #define CS_SYSDEF_PROVIDE_SELECT
      Includes definitions required for select(), FD_* macros, and
      struct timeval.

    The system-dependent include files can undefine some or all
    CS_SYSDEF_PROVIDE_xxx macros to avoid further definitions in this file.
    For example, if a system-dependent file defines everything needed for
    CS_SYSDEF_PROVIDE_GETOPT it should #undefine CS_SYSDEF_PROVIDE_GETOPT to
    avoid including util/gnu/getopt.h at the bottom of this file.
*/

/*
 * Pull in platform-specific overrides of the requested functionality.
 */
#include "cssys/csosdefs.h"

/*
 * Default definitions for requested functionality.  Platform-specific
 * configuration files may override these.
 */

/**\def TEMP_DIR
 * Directory for temporary files
 */
#ifdef CS_SYSDEF_PROVIDE_TEMP
#  ifndef TEMP_DIR
#    if defined(OS_UNIX)
#      define TEMP_DIR "/tmp/"
#    else
#      define TEMP_DIR ""
#    endif
#  endif
#  ifndef TEMP_FILE
#    if defined(OS_UNIX)
#      include <unistd.h>
/// Name for temporary file
#      define TEMP_FILE "cs%lud.tmp", (unsigned long)getpid()
#    else
/// Name for temporary file
#      define TEMP_FILE "$cs$.tmp"
#    endif
#  endif
#endif // CS_SYSDEF_PROVIDE_TEMP

/**\def MKDIR(path)
 * How to make a directory (not entire path, only the last on the path)
 */
#ifdef CS_SYSDEF_PROVIDE_MKDIR
#  ifndef MKDIR
#    if defined(OS_WIN32) || (defined(OS_DOS) && !defined(COMP_GCC))
#      define MKDIR(path) _mkdir (path)
#    else
#      define MKDIR(path) mkdir (path, 0755)
#    endif
#  endif
#endif // CS_SYSDEF_PROVIDE_MKDIR

#ifdef CS_SYSDEF_PROVIDE_GETCWD
#  if !defined(COMP_VC) && !defined(COMP_BC)
#    include <unistd.h>
#  endif
#endif // CS_SYSDEF_PROVIDE_GETCWD

#ifdef CS_SYSDEF_PROVIDE_DIR
// For systems without opendir()
// COMP_GCC has opendir, readdir 
# if !defined(COMP_GCC)
#  if defined(__NEED_OPENDIR_PROTOTYPE)
     struct DIR;
     struct dirent;
     extern "C" DIR *opendir (const char *name);
     extern "C" dirent *readdir (DIR *dirp);
     extern "C" int closedir (DIR *dirp);
     //extern "C" void seekdir (DIR *dirp, long off);
     //extern "C" long telldir (DIR *dirp);
     //extern "C" void rewinddir (DIR *dirp);
#  endif
# endif
// Generic ISDIR needed for COMP_GCC
#  ifdef __NEED_GENERIC_ISDIR
#    if defined (OS_WIN32) || defined (OS_DOS)
#      include <io.h>
#    endif
#    include <sys/types.h>
#    if !defined(OS_WIN32)
#      include <dirent.h>
#    endif
#    if defined(__CYGWIN32__)
#      include <sys/dirent.h>
#    endif
#    include <sys/stat.h>
     static inline bool isdir (const char *path, struct dirent *de)
     {
       char fullname [CS_MAXPATHLEN];
       int pathlen = strlen (path);
       memcpy (fullname, path, pathlen + 1);
       
       if ((pathlen) && (fullname[pathlen-1] != PATH_SEPARATOR))
       {
	 fullname[pathlen++] = PATH_SEPARATOR;
	 fullname[pathlen] = 0;
       }
              
       strcat (&fullname [pathlen], de->d_name);
       struct stat st;
       stat (fullname, &st);
       return ((st.st_mode & S_IFMT) == S_IFDIR);
     }
#  endif
#endif // CS_SYSDEF_PROVIDE_DIR

#ifdef CS_SYSDEF_PROVIDE_UNLINK
#  if !defined(COMP_VC) && !defined(COMP_BC)
#    include <unistd.h>
#  endif
#endif

/**\def CS_ALLOC_STACK_ARRAY(type, var, size)
 * Dynamic stack memory allocation.
 * \param type Type of the array elements.
 * \param var Name of the array to be allocated.
 * \param size Number of elements to be allocated.
 */
#ifdef COMP_GCC
// In GCC we are able to declare stack vars of dynamic size directly
#  define CS_ALLOC_STACK_ARRAY(type, var, size) \
	    type var [size]
#else
#  include <malloc.h>
#  define CS_ALLOC_STACK_ARRAY(type, var, size) \
	    type *var = (type *)alloca ((size) * sizeof (type))
#endif

#ifdef CS_SYSDEF_PROVIDE_ACCESS
#  if !defined(COMP_VC) && !defined(COMP_BC)
#    include <unistd.h>
#  endif
#  ifndef F_OK
#    define F_OK 0
#  endif
#  ifndef R_OK
#    define R_OK 2
#  endif
#  ifndef W_OK
#    define W_OK 4
#  endif
#endif

#ifdef CS_SYSDEF_PROVIDE_GETOPT
#  ifndef __STDC__
#    define __STDC__ 1
#  endif
#  include "cssys/getopt.h"
#endif

#ifdef CS_SYSDEF_PROVIDE_SOCKETS
#warning CS_SYSDEF_PROVIDE_SOCKETS is deprecated, include cssys/sockets.h instead
#include "cssys/sockets.h"
#endif

#ifdef CS_SYSDEF_PROVIDE_SELECT
#  include <sys/select.h>
#endif

/**
 * The CS_HEADER_GLOBAL() macro composes a pathname from two components and
 * wraps the path in `<' and `>'.  This macro is useful in cases where one does
 * not have the option of augmenting the preprocessor's header search path,
 * even though the include path for some header file may vary from platform to
 * platform.  For instance, on many platforms OpenGL headers are in a `GL'
 * directory, whereas on other platforms they are in an `OpenGL' directory.  As
 * an example, in the first case, the platform might define the preprocessor
 * macro GLPATH with the value `GL', and in the second case GLPATH would be
 * given the value `OpenGL'.  To actually include an OpenGL header, such as
 * gl.h, the following code would be used:
 * <pre>
 * #include CS_HEADER_GLOBAL(GLPATH,gl.h)
 * </pre>
 */
#define CS_HEADER_GLOBAL(X,Y) CS_HEADER_GLOBAL_COMPOSE(X,Y)
#define CS_HEADER_GLOBAL_COMPOSE(X,Y) <X/Y>

/**
 * The CS_HEADER_LOCAL() macro composes a pathname from two components and
 * wraps the path in double-quotes.  This macro is useful in cases where one
 * does not have the option of augmenting the preprocessor's header search
 * path, even though the include path for some header file may vary from
 * platform to platform.  For example, assuming that the preprocessor macro
 * UTILPATH is defined with some platform-specific value, to actually include a
 * header, such as util.h, the following code would be used:
 * <pre>
 * #include CS_HEADER_LOCAL(UTILPATH,util.h)
 * </pre>
 */
#define CS_HEADER_LOCAL(X,Y) CS_HEADER_LOCAL_COMPOSE1(X,Y)
#define CS_HEADER_LOCAL_COMPOSE1(X,Y) CS_HEADER_LOCAL_COMPOSE2(X/Y)
#define CS_HEADER_LOCAL_COMPOSE2(X) #X


/**\def CS_EXPORTED_FUNCTION
 * \internal A macro to export a function from a shared library.
 * Some platforms may need to override this.  For instance, Windows requires
 * extra `__declspec' goop when exporting a function from a plug-in module.
 */
#if !defined(CS_EXPORTED_FUNCTION)
#  define CS_EXPORTED_FUNCTION extern "C"
#endif

/**\def CS_EXPORTED_NAME(Prefix, Suffix)
 * \internal A macro used to build exported function names.
 * Usually "Prefix" is derived from shared library name, thus for each library
 * we'll have different exported names.  This prevents naming collisions when
 * static linking is used, and on platforms where plug-in symbols are exported
 * by default.  However, this may be bad for platforms which need to build
 * special export-tables on-the-fly at compile-time since distinct names make
 * the job more difficult.  Such platforms may need to override the default
 * expansion of this macro to use only the `Suffix' and ignore the `Prefix'
 * when composing the name.
 */
#if !defined(CS_EXPORTED_NAME)
#  define CS_EXPORTED_NAME(Prefix, Suffix) Prefix ## Suffix
#endif

#ifndef CS_IMPLEMENT_PLATFORM_PLUGIN
#  define CS_IMPLEMENT_PLATFORM_PLUGIN
#endif

#ifndef CS_IMPLEMENT_PLATFORM_APPLICATION
#  define CS_IMPLEMENT_PLATFORM_APPLICATION
#endif

#ifndef CS_STATIC_VAR_DESTRUCTION_REGISTRAR_FUNCTION
#  define CS_STATIC_VAR_DESTRUCTION_REGISTRAR_FUNCTION cs_static_var_cleanup
#endif

#ifndef CS_DECLARE_STATIC_VARIABLE_REGISTRATION
#  define CS_DECLARE_STATIC_VARIABLE_REGISTRATION \
void CS_STATIC_VAR_DESTRUCTION_REGISTRAR_FUNCTION (void (*p)());
#endif

#ifndef CS_DECLARE_STATIC_VARIABLE_CLEANUP
#  define CS_DECLARE_STATIC_VARIABLE_CLEANUP \
   CS_DECLARE_STATIC_VARIABLE_REGISTRATION
#endif

#ifndef CS_IMPLEMENT_STATIC_VARIABLE_REGISTRATION
#  define CS_IMPLEMENT_STATIC_VARIABLE_REGISTRATION                    \
void CS_STATIC_VAR_DESTRUCTION_REGISTRAR_FUNCTION (void (*p)())        \
{                                                                      \
  static void (**a)()=0;                                               \
  static int lastEntry=0;                                              \
  static int maxEntries=0;                                             \
                                                                       \
  if (p)                                                               \
  {                                                                    \
    if (lastEntry >= maxEntries)                                       \
    {                                                                  \
      maxEntries+=10;                                                  \
      a = (void (**)())realloc (a, maxEntries*sizeof(void*));          \
    }                                                                  \
    a[lastEntry++] = p;                                                \
  }                                                                    \
  else                                                                 \
  {                                                                    \
    int i;                                                             \
    for (i=lastEntry-1; i >= 0; i--)                                   \
      a[i] ();                                                         \
    free (a);                                                          \
   }                                                                   \
}                                                                      
#endif

#ifndef CS_IMPLEMENT_STATIC_VARIABLE_CLEANUP
#  define CS_IMPLEMENT_STATIC_VARIABLE_CLEANUP \
   CS_IMPLEMENT_STATIC_VARIABLE_REGISTRATION   
#endif

/**\def CS_IMPLEMENT_PLUGIN
 * The CS_IMPLEMENT_PLUGIN macro should be placed at the global scope in
 * exactly one compilation unit comprising a plugin module.  For maximum
 * portability, each plugin module must employ this macro.  Platforms may
 * override the definition of this macro in order to augment the implementation
 * of the plugin module with any special implementation details required by the
 * platform.
 */
#if defined(CS_STATIC_LINKED)

#  ifndef CS_IMPLEMENT_PLUGIN
#  define CS_IMPLEMENT_PLUGIN        \
          CS_IMPLEMENT_PLATFORM_PLUGIN 
#  endif

#else

#  ifndef CS_IMPLEMENT_PLUGIN
#  define CS_IMPLEMENT_PLUGIN              \
   CS_IMPLEMENT_STATIC_VARIABLE_CLEANUP    \
   CS_IMPLEMENT_PLATFORM_PLUGIN 
#  endif

#endif
/**\def CS_IMPLEMENT_APPLICATION
 * The CS_IMPLEMENT_APPLICATION macro should be placed at the global scope in
 * exactly one compilation unit comprising an application.  For maximum
 * portability, each application should employ this macro.  Platforms may
 * override the definition of this macro in order to augment the implementation
 * of an application with any special implementation details required by the
 * platform.
 */
#ifndef CS_IMPLEMENT_APPLICATION
#  define CS_IMPLEMENT_APPLICATION       \
   CS_IMPLEMENT_STATIC_VARIABLE_CLEANUP  \
   CS_IMPLEMENT_PLATFORM_APPLICATION 
#endif

/**\def CS_REGISTER_STATIC_FOR_DESTRUCTION
 * Register a method that will destruct one static variable.
 */
#ifndef CS_REGISTER_STATIC_FOR_DESTRUCTION
#define CS_REGISTER_STATIC_FOR_DESTRUCTION(getterFunc)\
        CS_STATIC_VAR_DESTRUCTION_REGISTRAR_FUNCTION (getterFunc);
#endif

/**\def CS_STATIC_VARIABLE_CLEANUP
 * Invoke the function that will call all destruction functions
 */
#ifndef CS_STATIC_VARIABLE_CLEANUP
#define CS_STATIC_VARIABLE_CLEANUP  \
        CS_STATIC_VAR_DESTRUCTION_REGISTRAR_FUNCTION (0);
#endif

/**\def CS_IMPLEMENT_STATIC_VAR(getterFunc,Type,initParam,kill_how)
 * Create a global variable thats created on demand. Create a Getter function to access 
 * the variable and a destruction function. The Getter function will register
 * the destruction function on first invocation.
 * Example:
 * CS_IMPLEMENT_STATIC_VAR (GetVertexPool, csVertexPool,)
 * This will give you a global function GetVertexPool that returns a pointer to a 
 * static variable.
 */

#ifndef CS_IMPLEMENT_STATIC_VAR_EXT
#define CS_IMPLEMENT_STATIC_VAR_EXT(getterFunc,Type,initParam,kill_how) \
CS_DECLARE_STATIC_VARIABLE_REGISTRATION                                 \
extern "C" {                                                            \
static Type* getterFunc ();                                             \
static void getterFunc ## _kill ();                                     \
static void getterFunc ## _kill_array ();                               \
void getterFunc ## _kill ()                                             \
{                                                                       \
  (void)getterFunc ## _kill_array;                                      \
  delete getterFunc ();                                                 \
}                                                                       \
void getterFunc ## _kill_array ()                                       \
{                                                                       \
  (void)getterFunc ## _kill;                                            \
  delete [] getterFunc ();                                              \
}                                                                       \
Type* getterFunc ()                                                     \
{                                                                       \
  static Type *v=0;                                                     \
  if (!v)                                                               \
  {                                                                     \
    v = new Type initParam;                                             \
    CS_REGISTER_STATIC_FOR_DESTRUCTION (getterFunc ## kill_how);        \
  }                                                                     \
  return v;                                                             \
}                                                                       \
}
#endif

#ifndef CS_IMPLEMENT_STATIC_VAR
#define CS_IMPLEMENT_STATIC_VAR(getterFunc,Type,initParam)    \
 CS_IMPLEMENT_STATIC_VAR_EXT(getterFunc,Type,initParam,_kill)    
#endif

#ifndef CS_IMPLEMENT_STATIC_VAR_ARRAY
#define CS_IMPLEMENT_STATIC_VAR_ARRAY(getterFunc,Type,initParam)    \
 CS_IMPLEMENT_STATIC_VAR_EXT(getterFunc,Type,initParam,_kill_array)    
#endif

/**\def CS_DECLARE_STATIC_CLASSVAR(var,getterFunc,Type)
 * Declare a static variable inside a class. This will also declare a Getter function.
 * Example:
 * CS_DECLARE_STATIC_CLASSVAR (pool, GetVertexPool, csVertexPool)
 */
#ifndef CS_DECLARE_STATIC_CLASSVAR
#define CS_DECLARE_STATIC_CLASSVAR(var,getterFunc,Type)       \
static Type *var;                                             \
static Type *getterFunc ();                                   
#endif

#ifndef CS_DECLARE_STATIC_CLASSVAR_REF
#define CS_DECLARE_STATIC_CLASSVAR_REF(var,getterFunc,Type)   \
static Type *var;                                             \
static Type &getterFunc ();                                   
#endif

/**\def CS_IMPLEMENT_STATIC_CLASSVAR(Class,var,getterFunc,Type,initParam)
 * Create the static class variable that has been declared with 
 * CS_DECLARE_STATIC_CLASSVAR.
 * This will also create the Getter function and the destruction function. 
 * The destruction function will be registered upon the first invocation
 * of the Getter function.
 * Example:
 * CS_IMPLEMENT_STATIC_CLASSVAR (csPolygon2D, pool, GetVertexPool, csVertexPool,)
 */
#ifndef CS_IMPLEMENT_STATIC_CLASSVAR_EXT
#define CS_IMPLEMENT_STATIC_CLASSVAR_EXT(Class,var,getterFunc,Type,initParam,kill_how)   \
Type *Class::var = 0;                                                                    \
extern "C" {                                                                             \
static void Class ## _ ## getterFunc ## _kill ();                                        \
static void Class ## _ ## getterFunc ## _kill_array ();                                  \
void Class ## _ ## getterFunc ## _kill ()                                                \
{                                                                                        \
  (void) Class ## _ ## getterFunc ## _kill_array;                                        \
  delete Class::getterFunc ();                                                           \
}                                                                                        \
void Class ## _ ## getterFunc ## _kill_array ()                                          \
{                                                                                        \
  (void) Class ## _ ## getterFunc ## _kill;                                              \
  delete [] Class::getterFunc ();                                                        \
}                                                                                        \
}                                                                                        \
Type* Class::getterFunc ()                                                               \
{                                                                                        \
  if (!var)                                                                              \
  {                                                                                      \
    var = new Type initParam;                                                            \
    CS_DECLARE_STATIC_VARIABLE_REGISTRATION                                              \
    CS_REGISTER_STATIC_FOR_DESTRUCTION (Class ## _ ## getterFunc ## kill_how);           \
  }                                                                                      \
  return var;                                                                            \
}
#endif

#ifndef CS_IMPLEMENT_STATIC_CLASSVAR
#define CS_IMPLEMENT_STATIC_CLASSVAR(Class,var,getterFunc,Type,initParam) \
        CS_IMPLEMENT_STATIC_CLASSVAR_EXT(Class,var,getterFunc,Type,initParam,_kill)
#endif

#ifndef CS_IMPLEMENT_STATIC_CLASSVAR_ARRAY
#define CS_IMPLEMENT_STATIC_CLASSVAR_ARRAY(Class,var,getterFunc,Type,initParam) \
        CS_IMPLEMENT_STATIC_CLASSVAR_EXT(Class,var,getterFunc,Type,initParam,_kill_array)
#endif

#ifndef CS_IMPLEMENT_STATIC_CLASSVAR_REF_EXT
#define CS_IMPLEMENT_STATIC_CLASSVAR_REF_EXT(Class,var,getterFunc,Type,initParam,kill_how)   \
Type *Class::var = 0;                                                                        \
extern "C" {                                                                                 \
static void Class ## _ ## getterFunc ## _kill ();                                            \
static void Class ## _ ## getterFunc ## _kill_array ();                                      \
void Class ## _ ## getterFunc ## _kill ()                                                    \
{                                                                                            \
  (void) Class ## _ ## getterFunc ## _kill_array;                                            \
  delete &Class::getterFunc ();                                                              \
}                                                                                            \
void Class ## _ ## getterFunc ## _kill_array ()                                              \
{                                                                                            \
  (void) Class ## _ ## getterFunc ## _kill;                                                  \
  delete [] &Class::getterFunc ();                                                           \
}                                                                                            \
}                                                                                            \
Type &Class::getterFunc ()                                                                   \
{                                                                                            \
  if (!var)                                                                                  \
  {                                                                                          \
    var = new Type initParam;                                                                \
    CS_DECLARE_STATIC_VARIABLE_REGISTRATION                                                  \
    CS_REGISTER_STATIC_FOR_DESTRUCTION (Class ## _ ## getterFunc ## kill_how);               \
  }                                                                                          \
  return *var;                                                                               \
}
#endif

#ifndef CS_IMPLEMENT_STATIC_CLASSVAR_REF
#define CS_IMPLEMENT_STATIC_CLASSVAR_REF(Class,var,getterFunc,Type,initParam)   \
        CS_IMPLEMENT_STATIC_CLASSVAR_REF_EXT(Class,var,getterFunc,Type,initParam,_kill)
#endif

#ifndef CS_IMPLEMENT_STATIC_CLASSVAR_REF_ARRAY
#define CS_IMPLEMENT_STATIC_CLASSVAR_REF_ARRAY(Class,var,getterFunc,Type,initParam)   \
        CS_IMPLEMENT_STATIC_CLASSVAR_REF_EXT(Class,var,getterFunc,Type,initParam,_kill_array)
#endif

// The following define should only be enabled if you have defined
// a special version of overloaded new that accepts two additional
// parameters: a (void*) pointing to the filename and an int with the
// line number. This is typically used for memory debugging.
// In csutil/memdebug.cpp there is a memory debugger which can (optionally)
// use this feature. Note that if CS_EXTENSIVE_MEMDEBUG is enabled while
// the memory debugger is not the memory debugger will still provide the
// needed overloaded operators so you can leave CS_EXTENSIVE_MEMDEBUG on in
// that case and the only overhead will be a little more arguments to 'new'.
// Do not enable CS_EXTENSIVE_MEMDEBUG if your platform or your own code
// defines its own 'new' operator, since this version will interfere with your
// own.
#ifndef CS_DEBUG
#  undef CS_EXTENSIVE_MEMDEBUG
#endif
#ifdef CS_EXTENSIVE_MEMDEBUG
extern void* operator new (size_t s, void* filename, int line);
extern void* operator new[] (size_t s, void* filename, int line);
#define NEW new ((void*)__FILE__, __LINE__)
#define new NEW
#endif

#ifdef CS_DEBUG
#  if !defined (DEBUG_BREAK)
#    if defined (PROC_X86)
#      if defined (COMP_GCC)
#        define DEBUG_BREAK	asm ("int $3")
#      else
#        define DEBUG_BREAK	_asm int 3
#      endif
#    else
#      define DEBUG_BREAK	{ static int x = 0; x /= x; }
#    endif
#  endif
#  if !defined (CS_ASSERT)
#    if defined (COMP_VC)
#      define  CS_ASSERT(x) assert(x)
#    else
#      include <stdio.h>
#      define CS_ASSERT(x)						\
         if (!(x))							\
         {								\
           fprintf (stderr, __FILE__ ":%d: failed assertion '%s'\n",\
             int(__LINE__), #x );					\
           DEBUG_BREAK;							\
         }
#    endif
#  endif
#else
#  undef DEBUG_BREAK
#  define DEBUG_BREAK
#  undef CS_ASSERT
#  define CS_ASSERT(x)
#endif

// Check if the csosdefs.h defined either CS_LITTLE_ENDIAN or CS_BIG_ENDIAN
#if !defined (CS_LITTLE_ENDIAN) && !defined (CS_BIG_ENDIAN)
#  error No CS_XXX_ENDIAN macro defined in your OS-specific csosdefs.h!
#endif

/*
 * This is a bit of overkill but if you're sure your CPU doesn't require
 * strict alignment add your CPU to the !defined below to get slightly
 * smaller and faster code in some cases.
 *
 * @@@ In the future, this should be moved to volatile.h and determined as
 * part of the configuration process.
 */
#if !defined (PROC_X86)
#  define CS_STRICT_ALIGNMENT
#endif

// Adjust some definitions contained in volatile.h
#if defined (PROC_X86) && !defined (DO_NASM)
#  undef NO_ASSEMBLER
#  define NO_ASSEMBLER
#endif

#if !defined (PROC_X86) || defined (NO_ASSEMBLER)
#  undef DO_MMX
#  undef DO_NASM
#endif

// Use fast QInt and QRound on CPUs that are known to support it
#if !defined (CS_NO_IEEE_OPTIMIZATIONS)
#  if !defined (CS_IEEE_DOUBLE_FORMAT)
#    if defined (PROC_X86) || defined (PROC_M68K)
#      define CS_IEEE_DOUBLE_FORMAT
#    endif
#  endif
#endif

// gcc can perform usefull checking for printf/scanf format strings, just add
// this define at the end of the function declaration
#if __GNUC__ > 2 || (__GNUC__ == 2 && __GNUC_MINOR__ > 4)
#  define CS_GNUC_PRINTF( format_idx, arg_idx)		\
     __attribute__((format (printf, format_idx, arg_idx)))
#  define CS_GNUC_SCANF( format_idx, arg_idx )				\
     __attribute__((format (scanf, format_idx, arg_idx)))
#else
#  define CS_GNUC_PRINTF( format_idx, arg_idx )
#  define CS_GNUC_SCANF( format_idx, arg_idx )
#endif

// Support for alignment and packing of structures.
#if !defined(CS_STRUCT_ALIGN_4BYTE_BEGIN)
#  if defined(__GNUC__) && defined(CS_STRICT_ALIGNMENT)
#    define CS_STRUCT_ALIGN_4BYTE_BEGIN
#    define CS_STRUCT_ALIGN_4BYTE_END __attribute__ ((aligned(4)))
#  else
#    define CS_STRUCT_ALIGN_4BYTE_BEGIN
#    define CS_STRUCT_ALIGN_4BYTE_END
#  endif
#endif

/// Fatal exit routine (which can be replaced if neccessary)
extern void (*fatal_exit) (int errorcode, bool canreturn);

#endif // __CS_CSSYSDEFS_H__
