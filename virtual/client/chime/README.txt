--------------------------------------------
----- CHIME Project Installation Tips ------
--------------------------------------------

1. Check out psl/memento/virtual/client from PSL CVS.
2. Open Visual Studio .NET (2002 definitely works, 2003 is under investigation).
3. Go to: Tools -> Options -> Projects -> VC++ Directories
4. Under 'Show Directories For:', select 'Include Files' and create a folder
   that points to 'psl/memento/virtual/client/include' on your local driver
5. Similarly, select 'Library Files' and create a folder that points to
   'psl/memento/virtual/client/libs'
6. Now open CHIME solution (project) from 'psl/memento/virtual/client/chime/chime.sln'
7. Hit Build -> Rebuild (at least the first time to do a clean build)
8. Hit Debug -> Run Without Debugging (or Ctrl+F5) to run CHIME.