#!/bin/bash
@echo off
export CLASSPATH=.:../../../../../build;$CLASSPATH                  
export CLASSPATH=../../../../../jars/psl/memento/pervasive/crunch/openxml-1.2.jar:$CLASSPATH
export CLASSPATH=../../../../../jars/psl/memento/pervasive/crunch/xercesImpl.jar:$CLASSPATH
export CLASSPATH=../../../../../jars/psl/memento/pervasive/crunch/xmlParserAPIs.jar:$CLASSPATH
java Proxy
echo Done.
pause
