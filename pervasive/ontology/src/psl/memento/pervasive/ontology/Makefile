JAVAHOME=C:/j2sdk1.4.1_02

LIB=C:/ontology/lib
SRC=C:/ontology/src
RUNTIME=C:/ontology/runtime

JAR_FILE=ontology.jar
JAVA_FILES=$(SRC)/psl/memento/pervasive/ontology/*.java $(SRC)/psl/memento/pervasive/ontology/relationsextraction/*.java $(SRC)/psl/memento/pervasive/ontology/relationsextraction/algorithm/*.java
CLASS_FILES=psl/ psl/memento/pervasive/ontology/relationsextraction/res/* psl/memento/pervasive/ontology/relationsextraction/algorithm/res/*

CLASSPATH="$(LIB)/apionrdf.jar;$(LIB)/kaoncorpus.jar;$(LIB)/qtag.jar;$(LIB)/apiproxy.jar;$(LIB)/kaongui.jar;$(LIB)/query.jar;$(LIB)/datalog.jar;$(LIB)/kaonws-client.jar;$(LIB)/rdfapi.jar;$(LIB)/engineeringserver-client.jar;$(LIB)/lexiconindex.jar;$(LIB)/rdfcrawler.jar;$(LIB)/guibase.jar;$(LIB)/log4j.jar;$(LIB)/rdfserver-client.jar;$(LIB)/jboss-jmx.jar;$(LIB)/msbase.jar;$(LIB)/registryapi.jar;$(LIB)/jbossall-client.jar;$(LIB)/mssqlserver.jar;$(LIB)/registrygui.jar;$(LIB)/jdom.jar;$(LIB)/msutil.jar;$(LIB)/similarity.jar;$(LIB)/jpedal.jar;$(LIB)/oimodeler.jar;$(LIB)/texttoonto.jar;$(LIB)/kaonapi.jar;$(LIB)/ojdbc14.jar;$(LIB)/trie.jar"

default : build

build : compile
	mkdir psl/memento/pervasive/ontology/relationsextraction/res
	mkdir psl/memento/pervasive/ontology/relationsextraction/algorithm/res
	cp $(SRC)/psl/memento/pervasive/ontology/relationsextraction/res/* psl/memento/pervasive/ontology/relationsextraction/res/.
	cp $(SRC)/psl/memento/pervasive/ontology/relationsextraction/algorithm/res/* psl/memento/pervasive/ontology/relationsextraction/algorithm/res/.
	$(JAVAHOME)/bin/jar cf $(JAR_FILE) $(CLASS_FILES)
	rm -rf $(CLASS_FILES)
	mv $(JAR_FILE) $(RUNTIME)

compile :
	$(JAVAHOME)/bin/javac -d . -classpath $(CLASSPATH) $(JAVA_FILES)
	