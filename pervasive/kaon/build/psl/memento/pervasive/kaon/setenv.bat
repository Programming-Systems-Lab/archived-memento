@echo off
echo Setting the CLASSPATH, please run compile.bat when finished.

for %%i in (..\..\..\..\..\jars\psl\memento\pervasive\kaon\*.jar) do call :set_var %%i
goto :EOF

:set_var
set CLASSPATH=%CLASSPATH%;%1