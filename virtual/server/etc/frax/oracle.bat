@echo off
cd ..\..
java -cp .;libs/commons-beanutils.jar;libs/commons-collections.jar;libs/commons-digester.jar;libs/commons-httpclient.jar;libs/commons-logging.jar;libs/commons-logging-api.jar;libs/dom4j.jar;libs/htmlparser.jar;libs/icu4j.jar;libs/jakarta-oro-2.0.6.jar;libs/jakarta-poi-1.5.1.jar;libs/jalopy-1.0b10.jar;libs/jcvsii.jar;libs/jdom.jar;libs/jena.jar;psl-frax.jar;libs/jftp-bea.jar;libs/log4j.jar;libs/mindbright.jar;libs/mysql-connector-java-2.0.14-bin.jar;libs/pgjdbc2.jar;libs/xerces.jar -Djava.security.policy=rmi.policy -Djava.rmi.server.codebase=file:psl-frax.jar psl.memento.server.frax.OracleImpl
cd etc\frax