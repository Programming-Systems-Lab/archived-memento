@echo off
set CLASSPATH=.;..\..\..\..\..\build;%CLASSPATH%                  
set CLASSPATH=..\..\..\..\..\jars\psl\memento\pervasive\crunch\openxml-1.2.jar;%CLASSPATH% 
set CLASSPATH=..\..\..\..\..\jars\psl\memento\pervasive\crunch\xercesImpl.jar;%CLASSPATH% 
set CLASSPATH=..\..\..\..\..\jars\psl\memento\pervasive\crunch\xmlParserAPIs.jar;%CLASSPATH%
javac -d . ..\..\..\..\..\src\psl\memento\pervasive\crunch\*.java
echo Done.
pause
