#! /bin/bash

echo "Setting up classpaths..."
export CLASSPATH=.:../../../../../build:$CLASSPATH                  
export CLASSPATH=../../../../../jars/psl/memento/pervasive/crunch/openxml-1.2.jar:$CLASSPATH 
export CLASSPATH=../../../../../jars/psl/memento/pervasive/crunch/xercesImpl.jar:$CLASSPATH 
export CLASSPATH=../../../../../jars/psl/memento/pervasive/crunch/xmlParserAPIs.jar:$CLASSPATH

echo "Compiling Crunch \"javac -d . ../../../../../src/psl/memento/pervasive/crunch/*.java\""
javac -d . ../../../../../src/psl/memento/pervasive/crunch/*.java

echo Done.
